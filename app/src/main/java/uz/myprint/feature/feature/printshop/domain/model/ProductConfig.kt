package uz.myprint.feature.feature.printshop.domain.model

import uz.myprint.feature.feature.product.domain.model.ProductCategory
import uz.myprint.feature.feature.product.domain.model.ProductMaterial
import uz.myprint.feature.feature.product.domain.model.ProductPrintType
import uz.myprint.feature.feature.product.domain.model.ProductSize

/**
 * Mijoz "Mahsulot tanlash" ekranida tanlagan konfiguratsiya.
 * "Poligrafiya tanlash" ekraniga shu obyekt uzatiladi va har bir
 * poligrafiya uchun narx shundan hisoblanadi.
 */
data class ProductConfig(

    val productId: String,

    val category: ProductCategory,

    val material: ProductMaterial? = null,

    val printType: ProductPrintType? = null,

    val size: ProductSize? = null,

    val quantity: Int = 1,

    val isRush: Boolean = false,

    val needsDelivery: Boolean = false

) {

    /**
     * Tanlangan variantlar uchun bir donaga qo'shiladigan summa.
     */
    val optionsPricePerUnit: Long
        get() = (material?.additionalPrice ?: 0L) +
                (printType?.additionalPrice ?: 0L) +
                (size?.additionalPrice ?: 0L)
}
