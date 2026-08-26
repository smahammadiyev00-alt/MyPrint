package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import uz.myprint.feature.feature.design.studio.domain.DesignDocument
import uz.myprint.feature.feature.design.studio.domain.DesignLayer
import uz.myprint.feature.feature.design.studio.domain.LayerTransform
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Millimetr va piksel orasidagi yagona ko'prik.
 *
 * Ekranda ham, eksportda ham SHU sinf ishlatiladi — farqi faqat
 * pxPerMm qiymatida. Ekranda ~3, 300 DPI eksportda 11.81.
 */
data class CanvasGeometry(

    val document: DesignDocument,

    val pxPerMm: Float,

    val originPx: Offset = Offset.Zero

) {

    private val trimOriginPx: Offset
        get() = Offset(
            originPx.x + document.bleedMm * pxPerMm,
            originPx.y + document.bleedMm * pxPerMm
        )

    fun mmToPx(mm: Float): Float = mm * pxPerMm

    fun pxToMm(px: Float): Float = px / pxPerMm

    fun toScreen(xMm: Float, yMm: Float): Offset =
        Offset(
            trimOriginPx.x + xMm * pxPerMm,
            trimOriginPx.y + yMm * pxPerMm
        )

    fun toDocument(point: Offset): Offset =
        Offset(
            (point.x - trimOriginPx.x) / pxPerMm,
            (point.y - trimOriginPx.y) / pxPerMm
        )

    // Quyidagilar LayerTransform qabul qiladi, DesignLayer emas.
    // Sabab: guruh tanlanganda ramka biror qatlamga tegishli
    // bo'lmaydi — u bir nechta qatlamni qamragan hisoblangan
    // to'rtburchak. Uni ham xuddi shu funksiyalar bilan chizish
    // kerak, aks holda har biri ikki nusxada yozilardi.

    fun sizePx(t: LayerTransform): Size =
        Size(t.widthMm * pxPerMm, t.heightMm * pxPerMm)

    fun topLeftPx(t: LayerTransform): Offset = toScreen(t.xMm, t.yMm)

    fun centerPx(t: LayerTransform): Offset =
        toScreen(t.centerXMm, t.centerYMm)

    fun layerSizePx(layer: DesignLayer): Size = sizePx(layer.transform)

    fun layerTopLeftPx(layer: DesignLayer): Offset =
        topLeftPx(layer.transform)

    fun layerCenterPx(layer: DesignLayer): Offset =
        centerPx(layer.transform)

    val fullSizePx: Size
        get() = Size(
            document.fullWidthMm * pxPerMm,
            document.fullHeightMm * pxPerMm
        )

    companion object {

        fun fitScale(
            document: DesignDocument,
            availableWidthPx: Float,
            availableHeightPx: Float,
            paddingPx: Float = 0f
        ): Float {

            val w = (availableWidthPx - paddingPx * 2f).coerceAtLeast(1f)

            val h = (availableHeightPx - paddingPx * 2f).coerceAtLeast(1f)

            return minOf(
                w / document.fullWidthMm,
                h / document.fullHeightMm
            )
        }
    }
}

// =====================================================================
//  CHO'ZISH NUQTALARI
// =====================================================================

enum class ResizeHandle(
    val dirX: Int,
    val dirY: Int
) {
    TOP_LEFT(-1, -1),
    TOP(0, -1),
    TOP_RIGHT(1, -1),
    RIGHT(1, 0),
    BOTTOM_RIGHT(1, 1),
    BOTTOM(0, 1),
    BOTTOM_LEFT(-1, 1),
    LEFT(-1, 0);

    val isCorner: Boolean get() = dirX != 0 && dirY != 0
}

private fun toLocal(dx: Float, dy: Float, rotationDeg: Float): Offset {

    val rad = (-rotationDeg * Math.PI / 180.0).toFloat()

    return Offset(
        dx * cos(rad) - dy * sin(rad),
        dx * sin(rad) + dy * cos(rad)
    )
}

private fun toWorld(dx: Float, dy: Float, rotationDeg: Float): Offset {

    val rad = (rotationDeg * Math.PI / 180.0).toFloat()

    return Offset(
        dx * cos(rad) - dy * sin(rad),
        dx * sin(rad) + dy * cos(rad)
    )
}

fun LayerTransform.containsPoint(
    pointMm: Offset,
    padMm: Float = 0f
): Boolean {

    val t = this

    val local = toLocal(
        pointMm.x - t.centerXMm,
        pointMm.y - t.centerYMm,
        t.rotationDeg
    )

    val halfW = t.widthMm / 2f + padMm
    val halfH = t.heightMm / 2f + padMm

    return local.x >= -halfW && local.x <= halfW &&
            local.y >= -halfH && local.y <= halfH
}

fun DesignLayer.containsPoint(
    pointMm: Offset,
    padMm: Float = 0f
): Boolean = transform.containsPoint(pointMm, padMm)

/**
 * Nuqta shu qatlamga tegadimi.
 *
 * Ichiga qirqilgan qatlam uchun qo'shimcha shart bor: u NISHON
 * ichida ham bo'lishi kerak. Aks holda foydalanuvchi bo'sh joyni
 * bosib, ko'rinmayotgan elementni tanlab olardi — chunki qatlamning
 * ramkasi nishondan tashqariga chiqib turishi mumkin, u yerda esa
 * hech narsa chizilmaydi.
 */
private fun DesignDocument.isHit(
    layer: DesignLayer,
    pointMm: Offset
): Boolean {

    if (!layer.isVisible || layer.isLocked) return false

    if (!layer.containsPoint(pointMm)) return false

    val targetId = layer.clipToId ?: return true

    val target = layerById(targetId) ?: return true

    return target.containsPoint(pointMm)
}

fun DesignDocument.hitTest(pointMm: Offset): DesignLayer? =
    layers.lastOrNull { isHit(it, pointMm) }

fun DesignDocument.hitTestAll(pointMm: Offset): List<DesignLayer> =
    layers.filter { isHit(it, pointMm) }

fun DesignDocument.cycleHit(
    pointMm: Offset,
    currentId: String?
): DesignLayer? {

    val hits = hitTestAll(pointMm)

    if (hits.isEmpty()) return null

    val index = hits.indexOfFirst { it.id == currentId }

    return if (index == -1) hits.last()
    else hits[(index - 1 + hits.size) % hits.size]
}

fun ResizeHandle.positionPx(
    t: LayerTransform,
    geometry: CanvasGeometry
): Offset {

    val localX = dirX * t.widthMm / 2f
    val localY = dirY * t.heightMm / 2f

    val world = toWorld(localX, localY, t.rotationDeg)

    return geometry.toScreen(
        t.centerXMm + world.x,
        t.centerYMm + world.y
    )
}

/**
 * Bosilgan nuqtaga eng yaqin cho'zish nuqtasi.
 *
 * ==== ASOSIY TUZATISH ====
 *
 * Ilgari bu yerda firstOrNull ishlatilardi. Kichik elementda —
 * masalan 54 × 12 mm matnda — 24dp radius ichiga sakkizala nuqta
 * ham tushib qolardi va enum tartibida BIRINCHI turgan TOP_LEFT
 * doim g'olib chiqardi. TOP_LEFT esa burchak, burchakda esa
 * keepAspect yoqilgan. Natijada foydalanuvchi yon nuqtani bosgan
 * bo'lsa ham element proporsiya bo'yicha o'zgarardi.
 *
 * Endi eng yaqini tanlanadi. Bundan tashqari yon nuqtalarga kichik
 * ustunlik beriladi: ular kamroq chalkashtiradi, chunki burchakni
 * bosmoqchi bo'lgan odam aniq burchakka tegadi.
 */
fun findHandleAt(
    positionPx: Offset,
    transform: LayerTransform,
    geometry: CanvasGeometry,
    radiusPx: Float,
    allowed: List<ResizeHandle> = ResizeHandle.entries
): ResizeHandle? {

    var best: ResizeHandle? = null

    var bestScore = Float.MAX_VALUE

    allowed.forEach { handle ->

        val handlePx = handle.positionPx(transform, geometry)

        val dx = positionPx.x - handlePx.x
        val dy = positionPx.y - handlePx.y

        val distance = kotlin.math.sqrt(dx * dx + dy * dy)

        if (distance > radiusPx) return@forEach

        // Yon nuqta 15% "yaqinroq" hisoblanadi.
        val score = if (handle.isCorner) distance else distance * 0.85f

        if (score < bestScore) {
            bestScore = score
            best = handle
        }
    }

    return best
}

/**
 * Element juda kichik bo'lib qolganda nuqtalar ustma-ust tushadi.
 * Shunda faqat burchaklarni ko'rsatgan ma'qul — yon nuqtalar
 * baribir tegib bo'lmaydigan holga keladi.
 */
fun visibleHandles(
    transform: LayerTransform,
    geometry: CanvasGeometry,
    minSpacingPx: Float
): List<ResizeHandle> {

    val widthPx = geometry.mmToPx(transform.widthMm)

    val heightPx = geometry.mmToPx(transform.heightMm)

    return ResizeHandle.entries.filter { handle ->

        when {
            handle.isCorner -> true
            handle.dirX == 0 -> widthPx >= minSpacingPx
            else -> heightPx >= minSpacingPx
        }
    }
}

/**
 * Nuqtani surganda yangi o'lcham.
 *
 * keepAspect endi TASHQARIDAN beriladi — burchak bo'lgani uchun
 * avtomatik yoqilmaydi. Ustki paneldagi qulf tugmasi shuni
 * boshqaradi. Photoshop'da ham shunday: burchak default'da erkin,
 * Shift bosilsa proporsional.
 */
fun LayerTransform.resizedBy(
    handle: ResizeHandle,
    deltaXMm: Float,
    deltaYMm: Float,
    keepAspect: Boolean,
    minSizeMm: Float = 2f
): LayerTransform {

    val local = toLocal(deltaXMm, deltaYMm, rotationDeg)

    var newWidth = widthMm
    var newHeight = heightMm

    // dirX == 0 bo'lsa kenglikka umuman tegilmaydi va aksincha.
    if (handle.dirX != 0) {
        newWidth = (widthMm + handle.dirX * local.x)
            .coerceAtLeast(minSizeMm)
    }

    if (handle.dirY != 0) {
        newHeight = (heightMm + handle.dirY * local.y)
            .coerceAtLeast(minSizeMm)
    }

    if (keepAspect && widthMm > 0f && heightMm > 0f) {

        val ratio = heightMm / widthMm

        when {
            // Yon nuqtada ham qulf ishlaydi: yetakchi o'lcham
            // qaysi bo'lsa, ikkinchisi unga ergashadi.
            handle.dirX == 0 -> newWidth = (newHeight / ratio)
                .coerceAtLeast(minSizeMm)

            handle.dirY == 0 -> newHeight = (newWidth * ratio)
                .coerceAtLeast(minSizeMm)

            else -> {
                // Burchakda barmoq ko'proq surgan o'q yetakchi
                // bo'ladi — shunda harakat tabiiy tuyuladi.
                val growX = abs(newWidth - widthMm)
                val growY = abs(newHeight - heightMm)

                if (growX >= growY) {
                    newHeight = (newWidth * ratio).coerceAtLeast(minSizeMm)
                } else {
                    newWidth = (newHeight / ratio).coerceAtLeast(minSizeMm)
                }
            }
        }
    }

    val growthX = (newWidth - widthMm) * handle.dirX / 2f
    val growthY = (newHeight - heightMm) * handle.dirY / 2f

    val shift = toWorld(growthX, growthY, rotationDeg)

    return copy(
        xMm = (centerXMm + shift.x) - newWidth / 2f,
        yMm = (centerYMm + shift.y) - newHeight / 2f,
        widthMm = newWidth,
        heightMm = newHeight
    )
}