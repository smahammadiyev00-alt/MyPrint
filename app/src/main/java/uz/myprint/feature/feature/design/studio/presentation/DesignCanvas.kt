package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import uz.myprint.feature.feature.design.studio.domain.DesignLayer

/** Ish maydonining foni. Xiralashtirish ham shu rangda. */
private val WorkspaceColor = Color(0xFFEDEFF5)

/**
 * Tahrirlash kanvasi.
 *
 * Ishorat mantiqi ataylab sodda: barmoq qatlam ustida boshlansa —
 * qatlam ko'chadi, bo'sh joyda boshlansa — kanvas suriladi.
 * Ikki barmoq tanlangan qatlamni kattalashtiradi va buradi.
 */
@Composable
fun DesignCanvas(
    viewModel: DesignEditorViewModel,
    modifier: Modifier = Modifier
) {

    val textMeasurer = rememberTextMeasurer()

    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(WorkspaceColor)
    ) {

        val document = viewModel.document

        val availableWidthPx = with(density) { maxWidth.toPx() }

        val availableHeightPx = with(density) { maxHeight.toPx() }

        val paddingPx = with(density) { 28.dp.toPx() }

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

        val fullWidthPx = document.fullWidthMm * pxPerMm

        val fullHeightPx = document.fullHeightMm * pxPerMm

        val originPx = Offset(
            x = (availableWidthPx - fullWidthPx) / 2f + viewModel.pan.x,
            y = (availableHeightPx - fullHeightPx) / 2f + viewModel.pan.y
        )

        val geometry = CanvasGeometry(
            document = document,
            pxPerMm = pxPerMm,
            originPx = originPx
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()

                .pointerInput(document.id) {

                    detectTapGestures { position ->

                        val pointMm = geometry.toDocument(position)

                        viewModel.select(document.hitTest(pointMm)?.id)
                    }
                }

                .pointerInput(document.id, viewModel.selectedLayerId) {

                    var draggingLayer = false

                    detectTransformGestures(
                        panZoomLock = false
                    ) { centroid, panChange, zoomChange, rotationChange ->

                        val selected = viewModel.selectedLayer

                        // Ishorat qayerda boshlangani birinchi hodisada
                        // aniqlanadi va shu harakat oxirigacha saqlanadi.
                        if (!draggingLayer && selected != null) {

                            val pointMm = geometry.toDocument(centroid)

                            if (selected.containsPoint(pointMm, padMm = 2f)) {
                                draggingLayer = true
                                viewModel.beginGesture()
                            }
                        }

                        if (draggingLayer && selected != null) {

                            viewModel.transformSelectedLive { t ->

                                val scaled = if (zoomChange != 1f) {

                                    val newWidth = (t.widthMm * zoomChange)
                                        .coerceAtLeast(2f)

                                    val newHeight = (t.heightMm * zoomChange)
                                        .coerceAtLeast(2f)

                                    // Markazni joyida ushlab turamiz,
                                    // aks holda shakl burchakdan o'sadi.
                                    t.copy(
                                        xMm = t.centerXMm - newWidth / 2f,
                                        yMm = t.centerYMm - newHeight / 2f,
                                        widthMm = newWidth,
                                        heightMm = newHeight
                                    )

                                } else {
                                    t
                                }

                                scaled.copy(
                                    xMm = scaled.xMm + geometry.pxToMm(panChange.x),
                                    yMm = scaled.yMm + geometry.pxToMm(panChange.y),
                                    rotationDeg = scaled.rotationDeg + rotationChange
                                )
                            }

                        } else {

                            viewModel.onCanvasTransform(panChange, zoomChange)
                        }
                    }
                }
        ) {

            drawDocument(document, geometry, textMeasurer)

            drawCutMask(geometry)

            drawGuides(geometry)

            viewModel.selectedLayer?.let { layer ->
                drawSelection(layer, geometry)
            }
        }
    }
}

/**
 * Kesish oldindan ko'rsatkichi.
 *
 * Qatlamlar kanvas chegarasidan tashqariga chiqib chizilaveradi —
 * 110 mm rasm 90 mm vizitkada ekranning yarmini egallaydi. Shundan
 * keyin ustiga niqob tortiladi va mijoz nima yo'qolishini ko'radi.
 *
 * Ikki daraja bor va bu ataylab:
 *
 *  kesim -> bleed    yengil niqob. Bu joy kesiladi, LEKIN rasm shu
 *                    yergacha yetishi shart. Qattiq xiralashtirilsa,
 *                    mijoz rasmni ichkariga tortib, bleed'ni buzadi.
 *
 *  bleed -> tashqari qattiq niqob. Bu joy umuman bosilmaydi.
 *
 * Niqob eksportga tushmaydi: eksport faqat drawDocument'ni chaqiradi
 * va bleed o'lchamidagi bitmapga chizadi.
 */
private fun DrawScope.drawCutMask(geometry: CanvasGeometry) {

    val document = geometry.document

    val trimTopLeft = geometry.toScreen(0f, 0f)

    val trimSize = Size(
        geometry.mmToPx(document.widthMm),
        geometry.mmToPx(document.heightMm)
    )

    // Bleed'dan tashqarisi butunlay isrof.
    drawFrame(
        outerTopLeft = Offset.Zero,
        outerSize = size,
        innerTopLeft = geometry.originPx,
        innerSize = geometry.fullSizePx,
        color = WorkspaceColor,
        alpha = 0.88f
    )

    // Kesim bilan bleed orasidagi halqa kerakli zaxira.
    drawFrame(
        outerTopLeft = geometry.originPx,
        outerSize = geometry.fullSizePx,
        innerTopLeft = trimTopLeft,
        innerSize = trimSize,
        color = WorkspaceColor,
        alpha = 0.42f
    )
}

/**
 * Ikki to'rtburchak orasidagi halqani bo'yaydi.
 *
 * Teshikli yo'l (Path + EvenOdd) ham ishlardi, lekin u har kadrda
 * yangi obyekt yaratadi. To'rtta to'rtburchak arzonroq va surish
 * paytida sekinlashish bermaydi.
 */
private fun DrawScope.drawFrame(
    outerTopLeft: Offset,
    outerSize: Size,
    innerTopLeft: Offset,
    innerSize: Size,
    color: Color,
    alpha: Float
) {

    val outerRight = outerTopLeft.x + outerSize.width
    val outerBottom = outerTopLeft.y + outerSize.height

    val innerRight = innerTopLeft.x + innerSize.width
    val innerBottom = innerTopLeft.y + innerSize.height

    drawRect(
        color = color,
        alpha = alpha,
        topLeft = outerTopLeft,
        size = Size(
            outerSize.width,
            (innerTopLeft.y - outerTopLeft.y).coerceAtLeast(0f)
        )
    )

    drawRect(
        color = color,
        alpha = alpha,
        topLeft = Offset(outerTopLeft.x, innerBottom),
        size = Size(
            outerSize.width,
            (outerBottom - innerBottom).coerceAtLeast(0f)
        )
    )

    drawRect(
        color = color,
        alpha = alpha,
        topLeft = Offset(outerTopLeft.x, innerTopLeft.y),
        size = Size(
            (innerTopLeft.x - outerTopLeft.x).coerceAtLeast(0f),
            innerSize.height
        )
    )

    drawRect(
        color = color,
        alpha = alpha,
        topLeft = Offset(innerRight, innerTopLeft.y),
        size = Size(
            (outerRight - innerRight).coerceAtLeast(0f),
            innerSize.height
        )
    )
}

/**
 * Bleed va xavfsiz maydon chiziqlari.
 *
 * Bular maketning bir qismi emas, faqat yordamchi ko'rsatkich —
 * shuning uchun eksportda chizilmaydi va drawDocument ichida
 * emas, alohida turadi.
 */
private fun DrawScope.drawGuides(geometry: CanvasGeometry) {

    val document = geometry.document

    val dash = PathEffect.dashPathEffect(floatArrayOf(9f, 7f))

    // Kesim chizig'i — mahsulot shu yerdan qirqiladi.
    drawRect(
        color = Color(0xFF9CA3AF),
        topLeft = geometry.toScreen(0f, 0f),
        size = Size(
            geometry.mmToPx(document.widthMm),
            geometry.mmToPx(document.heightMm)
        ),
        style = Stroke(width = 1.5f)
    )

    // Xavfsiz maydon — matn shundan tashqariga chiqmasligi kerak.
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

    // Bleed chegarasi — fon shu yergacha yetishi kerak.
    drawRect(
        color = Color(0xFFEF4444),
        topLeft = geometry.originPx,
        size = geometry.fullSizePx,
        style = Stroke(width = 1f, pathEffect = dash)
    )
}

/** Tanlangan qatlam ramkasi va burchak nuqtalari. */
private fun DrawScope.drawSelection(
    layer: DesignLayer,
    geometry: CanvasGeometry
) {

    val topLeft = geometry.layerTopLeftPx(layer)

    val size = geometry.layerSizePx(layer)

    val center = geometry.layerCenterPx(layer)

    val accent = Color(0xFF7B4DFF)

    rotate(degrees = layer.transform.rotationDeg, pivot = center) {

        drawRect(
            color = accent,
            topLeft = topLeft,
            size = size,
            style = Stroke(width = 2f)
        )

        val corners = listOf(
            topLeft,
            Offset(topLeft.x + size.width, topLeft.y),
            Offset(topLeft.x, topLeft.y + size.height),
            Offset(topLeft.x + size.width, topLeft.y + size.height)
        )

        corners.forEach { corner ->

            drawCircle(color = Color.White, radius = 9f, center = corner)

            drawCircle(
                color = accent,
                radius = 9f,
                center = corner,
                style = Stroke(width = 2f)
            )
        }
    }
}