package uz.myprint.feature.feature.design.studio.domain

import androidx.compose.ui.graphics.Color
import uz.myprint.feature.feature.product.domain.model.ProductSize
import uz.myprint.feature.feature.product.domain.model.SizeUnit

/** Bosma sifati. Poligrafiya standarti — 300 nuqta/dyuym. */
const val PRINT_DPI = 300f

/** 1 dyuym = 25.4 mm. */
const val MM_PER_INCH = 25.4f

/** 300 DPI da bir millimetrga necha piksel to'g'ri keladi. */
const val PRINT_PX_PER_MM = PRINT_DPI / MM_PER_INCH

/**
 * Tahrirlanayotgan maket.
 *
 * Uch xil chegara bor va ular chalkashmasligi kerak:
 *
 *  trim  — tayyor mahsulot o'lchami, kesish shu yerdan o'tadi
 *  bleed — kesimdan tashqariga chiqadigan zaxira (odatda 2 mm),
 *          fon shu yergacha cho'zilishi shart, aks holda kesishda
 *          chetda oq chiziq qoladi
 *  safe  — kesimdan ichkaridagi xavfsiz maydon (odatda 3 mm),
 *          matn va logotip shundan tashqariga chiqmasligi kerak
 */
data class DesignDocument(

    val id: String,

    val widthMm: Float,

    val heightMm: Float,

    val bleedMm: Float = 2f,

    val safeMarginMm: Float = 3f,

    val background: Color = Color.White,

    val layers: List<DesignLayer> = emptyList()

) {

    /** Bleed bilan birga to'liq maydon eni. */
    val fullWidthMm: Float get() = widthMm + bleedMm * 2f

    val fullHeightMm: Float get() = heightMm + bleedMm * 2f

    /** 300 DPI da eksport o'lchami, piksel. */
    val exportWidthPx: Int
        get() = Math.round(fullWidthMm * PRINT_PX_PER_MM)

    val exportHeightPx: Int
        get() = Math.round(fullHeightMm * PRINT_PX_PER_MM)

    fun layerById(id: String): DesignLayer? =
        layers.firstOrNull { it.id == id }

    fun replaceLayer(layer: DesignLayer): DesignDocument =
        copy(layers = layers.map { if (it.id == layer.id) layer else it })

    fun addLayer(layer: DesignLayer): DesignDocument =
        copy(layers = layers + layer)

    fun removeLayer(id: String): DesignDocument =
        copy(layers = layers.filterNot { it.id == id })

    /** Qatlamni bir pog'ona yuqoriga — ro'yxatda keyinga. */
    fun bringForward(id: String): DesignDocument {

        val index = layers.indexOfFirst { it.id == id }

        if (index == -1 || index == layers.lastIndex) return this

        val next = layers.toMutableList()

        next.add(index + 1, next.removeAt(index))

        return copy(layers = next)
    }

    fun sendBackward(id: String): DesignDocument {

        val index = layers.indexOfFirst { it.id == id }

        if (index <= 0) return this

        val next = layers.toMutableList()

        next.add(index - 1, next.removeAt(index))

        return copy(layers = next)
    }

    /**
     * Xavfsiz maydondan chiqib ketgan qatlamlar. Buyurtma
     * yuborishdan oldin mijozga ogohlantirish ko'rsatiladi —
     * poligrafiyaga noto'g'ri maket ketgandan ko'ra shu yaxshi.
     */
    fun layersOutsideSafeArea(): List<DesignLayer> =
        layers.filter { layer ->

            val t = layer.transform

            layer.isVisible && (
                    t.xMm < safeMarginMm ||
                            t.yMm < safeMarginMm ||
                            t.xMm + t.widthMm > widthMm - safeMarginMm ||
                            t.yMm + t.heightMm > heightMm - safeMarginMm
                    )
        }

    companion object {

        /**
         * Mahsulot o'lchamidan bo'sh maket yasaydi.
         * Banner metrda o'lchanadi, vizitka millimetrda — shuning
         * uchun birlik shu yerda millimetrga keltiriladi.
         */
        fun forProductSize(
            id: String,
            size: ProductSize,
            bleedMm: Float = 2f,
            safeMarginMm: Float = 3f
        ): DesignDocument {

            val factor = when (size.unit) {
                SizeUnit.MM -> 1f
                SizeUnit.CM -> 10f
                SizeUnit.M -> 1000f
            }

            return DesignDocument(
                id = id,
                widthMm = size.width * factor,
                heightMm = size.height * factor,
                bleedMm = bleedMm,
                safeMarginMm = safeMarginMm
            )
        }
    }
}
