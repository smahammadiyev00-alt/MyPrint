package uz.myprint.feature.feature.printshop.domain.model

import uz.myprint.feature.feature.product.domain.model.ProductCategory

/**
 * Bitta poligrafiyaning bitta mahsulot turi uchun tarifi.
 *
 * Model ataylab sodda: poligrafiya faqat bazaviy narx va bir nechta
 * koeffitsient kiritadi. Material / o'lcham / bosma turi uchun qo'shimcha
 * narx mahsulot modelining o'zida (additionalPrice) turadi.
 *
 * Barcha summalar — so'm, butun son.
 */
data class ShopTariff(

    val category: ProductCategory,

    /** Bazaviy dona narxi (eng arzon material, standart o'lcham). */
    val basePricePerUnit: Long,

    /** Shundan kam buyurtma qabul qilinmaydi. */
    val minQuantity: Int = 1,

    /** Standart tayyorlash muddati, kunlarda. */
    val productionDays: Int = 2,

    /** Tirajga qarab chegirma. */
    val quantityTiers: List<QuantityTier> = emptyList(),

    /** Shoshilinch buyurtma. */
    val rushAvailable: Boolean = false,
    val rushMultiplier: Float = 1.5f,
    val rushProductionDays: Int = 1,

    /** Yetkazib berish. */
    val deliveryPrice: Long = 0L,
    val freeDeliveryFrom: Long = 0L

) {

    /**
     * Berilgan soni uchun chegirma foizi.
     * Mos keladigan eng yuqori bosqich tanlanadi.
     */
    fun discountPercentFor(quantity: Int): Int =
        quantityTiers
            .filter { quantity >= it.fromQuantity }
            .maxByOrNull { it.fromQuantity }
            ?.discountPercent
            ?: 0
}

data class QuantityTier(

    val fromQuantity: Int,

    val discountPercent: Int
)
