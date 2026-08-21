package uz.myprint.feature.feature.printshop.domain.model

import uz.myprint.feature.feature.product.domain.model.ProductCategory

/**
 * Bitta poligrafiyaning bitta mahsulot turi uchun tarifi.
 *
 * Barcha summalar — so'm, butun son.
 */
data class ShopTariff(

    val category: ProductCategory,

    /**
     * Bazaviy narx. PER_ITEM da bir dona, PER_SQUARE_METER da bir
     * kvadrat metr uchun. PER_LINEAR_METER da ishlatilmaydi —
     * u yerda narx rulonga bog'liq, rolls ro'yxatiga qarang.
     */
    val basePricePerUnit: Long = 0L,

    val pricingUnit: PricingUnit = PricingUnit.PER_ITEM,

    /**
     * Poligrafiyadagi rulonlar. Faqat PER_LINEAR_METER uchun.
     * Dizayn sig'adigan eng arzon rulon tanlanadi.
     */
    val rolls: List<RollOption> = emptyList(),

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