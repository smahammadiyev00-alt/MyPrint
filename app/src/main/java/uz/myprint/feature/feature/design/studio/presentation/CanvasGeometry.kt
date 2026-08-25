package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import uz.myprint.feature.feature.design.studio.domain.DesignDocument
import uz.myprint.feature.feature.design.studio.domain.DesignLayer
import kotlin.math.cos
import kotlin.math.sin

/**
 * Millimetr va piksel orasidagi yagona ko'prik.
 *
 * Ekranda ham, eksportda ham SHU sinf ishlatiladi — farqi faqat
 * pxPerMm qiymatida. Ekranda ~3, 300 DPI eksportda 11.81.
 * Ikkinchi alohida hisob-kitob yozilsa, ekrandagi ko'rinish bilan
 * bosma bir-biriga mos kelmay qoladi.
 */
data class CanvasGeometry(

    val document: DesignDocument,

    /** Bir millimetrga necha piksel. */
    val pxPerMm: Float,

    /** Bleed maydonining chap yuqori burchagi, piksel. */
    val originPx: Offset = Offset.Zero

) {

    /** Kesim chizig'ining boshi — qatlam koordinatalari shundan. */
    private val trimOriginPx: Offset
        get() = Offset(
            originPx.x + document.bleedMm * pxPerMm,
            originPx.y + document.bleedMm * pxPerMm
        )

    fun mmToPx(mm: Float): Float = mm * pxPerMm

    fun pxToMm(px: Float): Float = px / pxPerMm

    /** Maket koordinatasi (mm) -> ekran koordinatasi (px). */
    fun toScreen(xMm: Float, yMm: Float): Offset =
        Offset(
            trimOriginPx.x + xMm * pxPerMm,
            trimOriginPx.y + yMm * pxPerMm
        )

    /** Ekran koordinatasi (px) -> maket koordinatasi (mm). */
    fun toDocument(point: Offset): Offset =
        Offset(
            (point.x - trimOriginPx.x) / pxPerMm,
            (point.y - trimOriginPx.y) / pxPerMm
        )

    fun layerSizePx(layer: DesignLayer): Size =
        Size(
            layer.transform.widthMm * pxPerMm,
            layer.transform.heightMm * pxPerMm
        )

    fun layerTopLeftPx(layer: DesignLayer): Offset =
        toScreen(layer.transform.xMm, layer.transform.yMm)

    fun layerCenterPx(layer: DesignLayer): Offset =
        toScreen(layer.transform.centerXMm, layer.transform.centerYMm)

    /** To'liq maydon (bleed bilan) piksel o'lchami. */
    val fullSizePx: Size
        get() = Size(
            document.fullWidthMm * pxPerMm,
            document.fullHeightMm * pxPerMm
        )

    companion object {

        /**
         * Maketni berilgan maydonga sig'diradigan masshtab.
         * padding — chetlarda qoladigan bo'sh joy, piksel.
         */
        fun fitScale(
            document: DesignDocument,
            availableWidthPx: Float,
            availableHeightPx: Float,
            paddingPx: Float = 0f
        ): Float {

            val w = (availableWidthPx - paddingPx * 2f)
                .coerceAtLeast(1f)

            val h = (availableHeightPx - paddingPx * 2f)
                .coerceAtLeast(1f)

            return minOf(
                w / document.fullWidthMm,
                h / document.fullHeightMm
            )
        }
    }
}

/**
 * Nuqta qatlam ichidami.
 *
 * Qatlam burilgan bo'lishi mumkin, shuning uchun nuqta avval
 * qatlamning o'z koordinata tizimiga o'tkaziladi: markaz atrofida
 * teskari yo'nalishda buriladi, keyin oddiy to'rtburchak tekshiruvi
 * qilinadi.
 */
fun DesignLayer.containsPoint(
    pointMm: Offset,
    padMm: Float = 0f
): Boolean {

    val t = transform

    val dx = pointMm.x - t.centerXMm
    val dy = pointMm.y - t.centerYMm

    val rad = (-t.rotationDeg * Math.PI / 180.0).toFloat()

    val localX = dx * cos(rad) - dy * sin(rad)
    val localY = dx * sin(rad) + dy * cos(rad)

    val halfW = t.widthMm / 2f + padMm
    val halfH = t.heightMm / 2f + padMm

    return localX >= -halfW && localX <= halfW &&
            localY >= -halfH && localY <= halfH
}

/**
 * Bosilgan nuqtadagi qatlam. Ro'yxat teskari aylanadi, chunki
 * ustki qatlam avval tanlanishi kerak.
 */
fun DesignDocument.hitTest(pointMm: Offset): DesignLayer? =
    layers.lastOrNull { layer ->
        layer.isVisible && !layer.isLocked && layer.containsPoint(pointMm)
    }
