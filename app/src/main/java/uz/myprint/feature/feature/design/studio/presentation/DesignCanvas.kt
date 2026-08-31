package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.myprint.core.di.AppContainer
import uz.myprint.feature.feature.design.studio.domain.DesignLayer
import uz.myprint.feature.feature.design.studio.domain.ImageLayer
import uz.myprint.feature.feature.design.studio.domain.LayerTransform
import kotlin.math.atan2

private val WorkspaceColor = Color(0xFFEDEFF5)

private val AccentColor = Color(0xFF7B4DFF)

private val SnapColor = Color(0xFFFF2D8E)

/**
 * Nuqtani ushlash radiusi.
 *
 * Ilgari 24dp edi va kichik elementda sakkizala nuqta bitta doiraga
 * tiqilib qolardi. Endi radius kichikroq, lekin tanlash "eng yaqini"
 * bo'yicha ketadi — natijada aniqlik oshdi.
 */
private val HandleTouchRadius = 18.dp

/**
 * Yon nuqta ko'rsatiladigan eng kichik uzunlik.
 *
 * Bundan qisqa tomonda yon nuqta burchaklar orasiga siqilib qoladi
 * va unga tegib bo'lmaydi — shunda uni umuman chizmagan ma'qul.
 */
private val MinSideForHandle = 56.dp

/**
 * Yopishish masofasi.
 *
 * Kichraytirildi. Avval ~17 px edi va element deyarli har doim
 * biror yo'riqchining ta'sir doirasida bo'lardi — magnitdan
 * qutulib bo'lmasdi.
 */
private val SnapThreshold = 5.dp

/** Kontekst menyu chiqishi uchun barmoqni ushlab turish vaqti. */
private const val LONG_PRESS_MS = 420L

@Composable
fun DesignCanvas(
    viewModel: DesignEditorViewModel,
    modifier: Modifier = Modifier,
    onLongPress: (DesignLayer) -> Unit = {}
) {

    val textMeasurer = rememberTextMeasurer()

    val density = LocalDensity.current

    val touchSlop = LocalViewConfiguration.current.touchSlop

    // Rasmlar chizishdan OLDIN yuklanadi.
    //
    // Chizish funksiyasi diskdan o'qimaydi: u har kadrda
    // takrorlanadi va fayl o'qish sakrashga olib kelardi. Bu yerda
    // esa yuklash faqat rasm qatlamlari ro'yxati o'zgarganda
    // qayta ishga tushadi.
    var images by remember { mutableStateOf<Map<String, ImageBitmap>>(emptyMap()) }

    val imagePaths = viewModel.document.layers
        .filterIsInstance<ImageLayer>()
        .map { it.sourceUri }

    LaunchedEffect(imagePaths) {

        if (imagePaths.isEmpty()) {
            images = emptyMap()
            return@LaunchedEffect
        }

        val store = AppContainer.imageStore

        images = withContext(Dispatchers.IO) {

            imagePaths
                .distinct()
                .mapNotNull { path ->
                    // Ekran uchun 1600 px yetarli: eng katta
                    // telefon ekrani ham bundan tor.
                    store.load(path, maxPx = 1600)?.let { path to it }
                }
                .toMap()
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(WorkspaceColor)
    ) {

        val document = viewModel.document

        val availableWidthPx = with(density) { maxWidth.toPx() }

        val availableHeightPx = with(density) { maxHeight.toPx() }

        val paddingPx = with(density) { 28.dp.toPx() }

        val touchRadiusPx = with(density) { HandleTouchRadius.toPx() }

        val minSidePx = with(density) { MinSideForHandle.toPx() }

        val snapPx = with(density) { SnapThreshold.toPx() }

        val baseScale = remember(
            document.fullWidthMm,
            document.fullHeightMm,
            availableWidthPx,
            availableHeightPx
        ) {
            CanvasGeometry.fitScale(
                document = document,
                availableWidthPx = availableWidthPx,
                availableHeightPx = availableHeightPx,
                paddingPx = paddingPx
            )
        }

        val pxPerMm = baseScale * viewModel.zoom

        val geometry = CanvasGeometry(
            document = document,
            pxPerMm = pxPerMm,
            originPx = Offset(
                x = (availableWidthPx - document.fullWidthMm * pxPerMm) / 2f +
                        viewModel.pan.x,
                y = (availableHeightPx - document.fullHeightMm * pxPerMm) / 2f +
                        viewModel.pan.y
            )
        )

        val geometryState = rememberUpdatedState(geometry)

        val longPressState = rememberUpdatedState(onLongPress)

        Canvas(
            modifier = Modifier
                .fillMaxSize()

                .pointerInput(document.id) {

                    awaitEachGesture {

                        val down = awaitFirstDown(requireUnconsumed = false)

                        // Guruh tanlangan bo'lsa nuqtalar guruh
                        // chegarasida turadi, bitta a'zoda emas.
                        val selection = viewModel.selectionTransform

                        val handle = selection?.let { t ->
                            findHandleAt(
                                positionPx = down.position,
                                transform = t,
                                geometry = geometryState.value,
                                radiusPx = touchRadiusPx,
                                allowed = visibleHandles(
                                    transform = t,
                                    geometry = geometryState.value,
                                    minSpacingPx = minSidePx
                                )
                            )
                        }

                        if (handle != null) {

                            down.consume()

                            resizeLoop(
                                pointerId = down.id,
                                handle = handle,
                                viewModel = viewModel,
                                snapThresholdPx = snapPx,
                                geometry = { geometryState.value }
                            )

                            return@awaitEachGesture
                        }

                        moveOrPan(
                            downPosition = down.position,
                            touchSlop = touchSlop,
                            viewModel = viewModel,
                            snapThresholdPx = snapPx,
                            geometry = { geometryState.value },
                            onLongPress = { longPressState.value(it) }
                        )
                    }
                }
        ) {

            drawDocument(
                document = document,
                geometry = geometry,
                textMeasurer = textMeasurer,
                images = images
            )

            drawCutMask(geometry)

            drawGuides(geometry)

            drawSnapLines(viewModel.snapLines, geometry)

            viewModel.selectedLayer?.let { layer ->

                // Ichiga qirqilgan qatlam tanlansa, nishonning
                // konturi ham ko'rsatiladi.
                layer.clipToId?.let { targetId ->

                    document.layerById(targetId)?.let { target ->
                        drawClipTargetOutline(
                            target.transform,
                            geometry,
                            Color(0xFF22C55E)
                        )
                    }
                }
            }

            viewModel.selectionTransform?.let { selection ->

                // Guruhda har bir a'zoning o'z konturi ham xira
                // ko'rsatiladi — foydalanuvchi guruhda nechta
                // element borligini ko'rib tursin.
                if (viewModel.isGroupSelected) {

                    viewModel.selectionIds.forEach { id ->

                        document.layerById(id)?.let { member ->
                            drawClipTargetOutline(
                                member.transform,
                                geometry,
                                AccentColor.copy(alpha = 0.45f)
                            )
                        }
                    }
                }

                drawSelection(
                    transform = selection,
                    geometry = geometry,
                    handles = visibleHandles(selection, geometry, minSidePx),

                    // Burchaklar rasm uchun ham to'la bo'yaladi:
                    // foydalanuvchi nisbat saqlanishini oldindan
                    // ko'rib tursin, cho'zib ko'rgandan keyin
                    // emas.
                    cornersLocked = viewModel.aspectLocked ||
                            viewModel.selectionHasImage,

                    // Yon nuqtalar hech qachon cheklanmaydi,
                    // shuning uchun ular hamisha to'liq ko'rinadi.
                    sidesDimmed = viewModel.aspectLocked &&
                            !viewModel.selectionHasImage
                )
            }
        }
    }
}

/**
 * Cho'zish sikli.
 *
 * Alohida funksiyaga chiqarildi, chunki bu yerda ikki narsa
 * qo'shildi: harakat oxirida magnit chiziqlarini tozalash va
 * KUMULATIV delta. Kadr-ma-kadr delta bilan magnit ishlamaydi —
 * element yopishgan joyidan qimirlamay qoladi, chunki har kadrda
 * "yopishgan" holatdan yangi kichik delta qo'shiladi va u yana
 * o'sha yerga qaytariladi. Shuning uchun boshlang'ich o'lcham
 * eslab qolinadi va har kadrda TO'LIQ delta qayta qo'llanadi.
 */
private suspend fun AwaitPointerEventScope.resizeLoop(
    pointerId: PointerId,
    handle: ResizeHandle,
    viewModel: DesignEditorViewModel,
    snapThresholdPx: Float,
    geometry: () -> CanvasGeometry
) {

    val start = viewModel.selectionTransform ?: return

    viewModel.beginGesture()

    var totalDelta = Offset.Zero

    while (true) {

        val event = awaitPointerEvent()

        val change = event.changes.firstOrNull { it.id == pointerId } ?: break

        if (!change.pressed) break

        totalDelta += change.position - change.previousPosition

        val geo = geometry()

        val layerId = viewModel.selectedLayerId ?: break

        val proposed = start.resizedBy(
            handle = handle,
            deltaXMm = geo.pxToMm(totalDelta.x),
            deltaYMm = geo.pxToMm(totalDelta.y),

            // ==== ASOSIY O'ZGARISH ====
            // Ilgari bu yerda handle.isCorner turardi va burchakni
            // erkin cho'zishning iloji yo'q edi. Endi qarorni
            // foydalanuvchi ustki paneldagi qulf orqali beradi.
            keepAspect = viewModel.keepAspectFor(handle)
        )

        val snapped = SnapEngine(
            document = geo.document,
            pxPerMm = geo.pxPerMm,
            enabled = viewModel.snapEnabled,
            thresholdPx = snapThresholdPx
        ).snapResize(layerId, handle, proposed)

        viewModel.transformSelectedLive { snapped.transform }

        viewModel.updateSnapLines(snapped.lines)

        change.consume()
    }

    viewModel.endGesture()
}

private suspend fun AwaitPointerEventScope.moveOrPan(
    downPosition: Offset,
    touchSlop: Float,
    viewModel: DesignEditorViewModel,
    snapThresholdPx: Float,
    geometry: () -> CanvasGeometry,
    onLongPress: (DesignLayer) -> Unit
) {

    var pastSlop = false

    var accumulated = 0f

    var movingLayer = false

    // Boshlang'ich holat va JAMI surilish.
    //
    // Aynan shu ikkalasi yetishmagani uchun magnit "juda kuchli"
    // tuyulardi. Ilgari har kadrda element o'zining YOPISHGAN
    // holatidan yangi kichik delta qo'shib hisoblanardi va o'sha
    // chiziqqa qaytarilaverardi — barmoq surilsa ham element
    // qimirlamasdi. Endi element pozitsiyasi doim boshlang'ich
    // holat + barmoqning jami yo'lidan hisoblanadi, magnit esa
    // faqat yakuniy natijani biroz tortadi.
    var startTransform: LayerTransform? = null

    var totalDelta = Offset.Zero

    // ---- uzun bosish: kontekst menyu ----
    //
    // withTimeoutOrNull null qaytarsa — vaqt tugadi, ya'ni barmoq
    // qimirlamay turdi. Bu uzun bosish. Agar barmoq surilgan yoki
    // ko'tarilgan bo'lsa, sikl vaqtidan oldin tugaydi va odatdagi
    // harakat mantiqi davom etadi.
    val interrupted: Boolean? = withTimeoutOrNull(LONG_PRESS_MS) {

        var stopped = false

        while (!stopped) {

            val event = awaitPointerEvent()

            val active = event.changes.filter { it.pressed }

            stopped = active.isEmpty() ||
                    (active.first().position - downPosition)
                        .getDistance() > touchSlop
        }

        true
    }

    if (interrupted == null) {

        val geo = geometry()

        val hit = geo.document.hitTest(geo.toDocument(downPosition))

        if (hit != null) {

            viewModel.select(hit.id)

            onLongPress(hit)

            // Barmoq ko'tarilguncha kutiladi, aks holda menyu
            // ochilgan holda element ham sudralib ketadi.
            while (true) {

                val event = awaitPointerEvent()

                if (event.changes.none { it.pressed }) break

                event.changes.forEach { it.consume() }
            }

            return
        }
    }

    while (true) {

        val event = awaitPointerEvent()

        val active = event.changes.filter { it.pressed }

        if (active.isEmpty()) break

        val centerNow = active.centroid(previous = false)

        val centerBefore = active.centroid(previous = true)

        val frameDelta = centerNow - centerBefore

        if (!pastSlop) {

            accumulated += frameDelta.getDistance()

            if (accumulated < touchSlop) continue

            pastSlop = true

            val pointMm = geometry().toDocument(downPosition)

            val unit = viewModel.selectionTransform

            movingLayer = unit != null &&
                    !viewModel.isSelectionLocked &&
                    unit.containsPoint(pointMm, padMm = 2f)

            if (movingLayer) {
                viewModel.beginGesture()
                startTransform = unit
            }
        }

        val zoom = if (active.size >= 2) active.zoomChange() else 1f

        val rotation = if (active.size >= 2) {
            active.rotationChange(centerBefore, centerNow)
        } else {
            0f
        }

        val geo = geometry()

        val base = startTransform

        if (movingLayer && base != null) {

            totalDelta += frameDelta

            val layerId = viewModel.selectedLayerId

            val current = viewModel.selectionTransform

            if (layerId != null && current != null) {

                // Masshtab va burilish barmoqdan keladi, shuning
                // uchun ular JORIY holatdan hisoblanadi. Surilish
                // esa boshlang'ich holatdan — magnit yopishtirgan
                // qiymat keyingi kadrga o'tib ketmasligi uchun.
                val scaled = if (zoom != 1f) {
                    current.resizedAroundCenter(
                        current.widthMm * zoom,
                        current.heightMm * zoom
                    )
                } else {
                    current
                }

                val proposed = scaled.copy(
                    xMm = base.xMm + geo.pxToMm(totalDelta.x),
                    yMm = base.yMm + geo.pxToMm(totalDelta.y),
                    rotationDeg = scaled.rotationDeg + rotation
                )

                val snapped = SnapEngine(
                    document = geo.document,
                    pxPerMm = geo.pxPerMm,

                    // Ikki barmoq bilan burayotganda magnit halaqit
                    // beradi — element burchakka yopishib sakraydi.
                    enabled = viewModel.snapEnabled && active.size < 2,
                    thresholdPx = snapThresholdPx
                ).snapMove(layerId, viewModel.selectionIds, proposed)

                viewModel.transformSelectedLive { snapped.transform }

                viewModel.updateSnapLines(snapped.lines)
            }

        } else if (pastSlop) {

            viewModel.onCanvasTransform(frameDelta, zoom)
        }

        active.forEach { it.consume() }
    }

    viewModel.endGesture()

    if (!pastSlop) {

        val geo = geometry()

        val next = geo.document.cycleHit(
            pointMm = geo.toDocument(downPosition),
            currentId = viewModel.selectedLayerId
        )

        viewModel.select(next?.id)
    }
}

private fun List<PointerInputChange>.centroid(previous: Boolean): Offset {

    if (isEmpty()) return Offset.Zero

    var sum = Offset.Zero

    forEach { sum += if (previous) it.previousPosition else it.position }

    return sum / size.toFloat()
}

private fun List<PointerInputChange>.zoomChange(): Float {

    val before = spread(previous = true)

    val now = spread(previous = false)

    return if (before > 0.5f) now / before else 1f
}

private fun List<PointerInputChange>.spread(previous: Boolean): Float {

    if (isEmpty()) return 0f

    val center = centroid(previous)

    var total = 0f

    forEach {
        val point = if (previous) it.previousPosition else it.position
        total += (point - center).getDistance()
    }

    return total / size
}

private fun List<PointerInputChange>.rotationChange(
    centerBefore: Offset,
    centerNow: Offset
): Float {

    if (size < 2) return 0f

    var total = 0f

    forEach { change ->

        val before = change.previousPosition - centerBefore

        val now = change.position - centerNow

        if (before.getDistance() < 1f || now.getDistance() < 1f) {
            return@forEach
        }

        val angleBefore = atan2(before.y, before.x)

        val angleNow = atan2(now.y, now.x)

        var delta = Math
            .toDegrees((angleNow - angleBefore).toDouble())
            .toFloat()

        while (delta > 180f) delta -= 360f

        while (delta < -180f) delta += 360f

        total += delta
    }

    return total / size
}

// =====================================================================
//  CHIZISH
// =====================================================================

private fun DrawScope.drawSnapLines(
    lines: List<SnapLine>,
    geometry: CanvasGeometry
) {

    if (lines.isEmpty()) return

    val dash = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))

    lines.forEach { line ->

        val width = if (line.isCenter) 2f else 1.4f

        when (line.axis) {

            SnapAxis.VERTICAL -> {

                val x = geometry.toScreen(line.positionMm, 0f).x

                drawLine(
                    color = SnapColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = width,
                    pathEffect = dash
                )
            }

            SnapAxis.HORIZONTAL -> {

                val y = geometry.toScreen(0f, line.positionMm).y

                drawLine(
                    color = SnapColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = width,
                    pathEffect = dash
                )
            }
        }
    }
}

/** Ichiga qirqilgan qatlam tanlanganda nishon konturi. */
private fun DrawScope.drawClipTargetOutline(
    target: LayerTransform,
    geometry: CanvasGeometry,
    color: Color
) {

    val center = geometry.centerPx(target)

    rotate(degrees = target.rotationDeg, pivot = center) {

        drawRect(
            color = color,
            topLeft = geometry.topLeftPx(target),
            size = geometry.sizePx(target),
            style = Stroke(
                width = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f))
            )
        )
    }
}

private fun DrawScope.drawCutMask(geometry: CanvasGeometry) {

    val document = geometry.document

    drawFrame(
        outerTopLeft = Offset.Zero,
        outerSize = size,
        innerTopLeft = geometry.originPx,
        innerSize = geometry.fullSizePx,
        alpha = 0.88f
    )

    drawFrame(
        outerTopLeft = geometry.originPx,
        outerSize = geometry.fullSizePx,
        innerTopLeft = geometry.toScreen(0f, 0f),
        innerSize = Size(
            geometry.mmToPx(document.widthMm),
            geometry.mmToPx(document.heightMm)
        ),
        alpha = 0.42f
    )
}

private fun DrawScope.drawFrame(
    outerTopLeft: Offset,
    outerSize: Size,
    innerTopLeft: Offset,
    innerSize: Size,
    alpha: Float
) {

    val outerRight = outerTopLeft.x + outerSize.width
    val outerBottom = outerTopLeft.y + outerSize.height

    val innerRight = innerTopLeft.x + innerSize.width
    val innerBottom = innerTopLeft.y + innerSize.height

    drawRect(
        color = WorkspaceColor,
        alpha = alpha,
        topLeft = outerTopLeft,
        size = Size(
            outerSize.width,
            (innerTopLeft.y - outerTopLeft.y).coerceAtLeast(0f)
        )
    )

    drawRect(
        color = WorkspaceColor,
        alpha = alpha,
        topLeft = Offset(outerTopLeft.x, innerBottom),
        size = Size(
            outerSize.width,
            (outerBottom - innerBottom).coerceAtLeast(0f)
        )
    )

    drawRect(
        color = WorkspaceColor,
        alpha = alpha,
        topLeft = Offset(outerTopLeft.x, innerTopLeft.y),
        size = Size(
            (innerTopLeft.x - outerTopLeft.x).coerceAtLeast(0f),
            innerSize.height
        )
    )

    drawRect(
        color = WorkspaceColor,
        alpha = alpha,
        topLeft = Offset(innerRight, innerTopLeft.y),
        size = Size(
            (outerRight - innerRight).coerceAtLeast(0f),
            innerSize.height
        )
    )
}

private fun DrawScope.drawGuides(geometry: CanvasGeometry) {

    val document = geometry.document

    val dash = PathEffect.dashPathEffect(floatArrayOf(9f, 7f))

    drawRect(
        color = Color(0xFF6B7280),
        topLeft = geometry.toScreen(0f, 0f),
        size = Size(
            geometry.mmToPx(document.widthMm),
            geometry.mmToPx(document.heightMm)
        ),
        style = Stroke(width = 1.5f)
    )

    val margin = document.safeMarginMm

    drawRect(
        color = Color(0xFF22C55E),
        topLeft = geometry.toScreen(margin, margin),
        size = Size(
            geometry.mmToPx(document.widthMm - margin * 2f),
            geometry.mmToPx(document.heightMm - margin * 2f)
        ),
        style = Stroke(width = 1f, pathEffect = dash)
    )

    drawRect(
        color = Color(0xFFEF4444),
        topLeft = geometry.originPx,
        size = geometry.fullSizePx,
        style = Stroke(width = 1f, pathEffect = dash)
    )
}

/**
 * Tanlangan qatlam ramkasi va nuqtalari.
 *
 * Nuqta shakli endi ma'no tashiydi:
 *   doira        — burchak, ikkala o'lchamni o'zgartiradi
 *   cho'ziq      — yon, faqat bitta o'lchamni o'zgartiradi
 *   to'liq bo'yalgan doira — qulf yoqilgan, proporsiya saqlanadi
 *
 * Qulf yoqilganda yon nuqtalar HAM chiziladi, lekin xira: ular
 * ishlaydi, faqat ikkinchi o'lcham ergashadi.
 */
private fun DrawScope.drawSelection(
    transform: LayerTransform,
    geometry: CanvasGeometry,
    handles: List<ResizeHandle>,
    cornersLocked: Boolean,
    sidesDimmed: Boolean
) {

    val topLeft = geometry.topLeftPx(transform)

    val size = geometry.sizePx(transform)

    val center = geometry.centerPx(transform)

    rotate(degrees = transform.rotationDeg, pivot = center) {

        drawRect(
            color = AccentColor,
            topLeft = topLeft,
            size = size,
            style = Stroke(width = 2f)
        )
    }

    handles.forEach { handle ->

        val position = handle.positionPx(transform, geometry)

        if (handle.isCorner) {

            drawCircle(
                color = if (cornersLocked) AccentColor else Color.White,
                radius = 12f,
                center = position
            )

            drawCircle(
                color = AccentColor,
                radius = 12f,
                center = position,
                style = Stroke(width = 2.5f)
            )

        } else {

            val halfLong = 15f
            val halfShort = 5.5f

            val w = if (handle.dirX == 0) halfLong else halfShort
            val h = if (handle.dirX == 0) halfShort else halfLong

            rotate(degrees = transform.rotationDeg, pivot = position) {

                drawRect(
                    color = Color.White,
                    alpha = if (sidesDimmed) 0.5f else 1f,
                    topLeft = Offset(position.x - w, position.y - h),
                    size = Size(w * 2f, h * 2f)
                )

                drawRect(
                    color = AccentColor,
                    alpha = if (sidesDimmed) 0.5f else 1f,
                    topLeft = Offset(position.x - w, position.y - h),
                    size = Size(w * 2f, h * 2f),
                    style = Stroke(width = 2.5f)
                )
            }
        }
    }
}