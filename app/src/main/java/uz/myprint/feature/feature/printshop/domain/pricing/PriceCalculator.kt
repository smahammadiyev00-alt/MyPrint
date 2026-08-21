package uz.myprint.feature.feature.printshop.domain.pricing

import uz.myprint.feature.feature.printshop.domain.model.PriceQuote
import uz.myprint.feature.feature.printshop.domain.model.PricingUnit
import uz.myprint.feature.feature.printshop.domain.model.PrintShop
import uz.myprint.feature.feature.printshop.domain.model.ProductConfig
import uz.myprint.feature.feature.printshop.domain.model.ShopTariff
import uz.myprint.feature.feature.product.domain.model.ProductSize
import uz.myprint.feature.feature.product.domain.model.areaSquareMeters
import uz.myprint.feature.feature.product.domain.model.linearMeters

/**
 * Narx hisoblash.
 *
 * Deterministik funksiya: bir xil kirish har doim bir xil natija beradi.
 * Narxni hech qachon AI hisoblamaydi — mijoz to'laydigan summa
 * testlanadigan formula bo'lishi kerak.
 */
object PriceCalculator {

    /** Juda kichik buyurtma ham shu miqdordan arzon hisoblanmaydi. */
    private const val MIN_BILLABLE = 0.5

    fun quote(shop: PrintShop, config: ProductConfig): PriceQuote {

        if (!shop.isAcceptingOrders) {
            return PriceQuote.OnRequest(
                PriceQuote.OnRequest.Reason.NOT_ACCEPTING
            )
        }

        val tariff = shop.tariffFor(config.category)
            ?: return PriceQuote.OnRequest(
                PriceQuote.OnRequest.Reason.NO_TARIFF
            )

        val totalQuantity = config.quantity

        if (totalQuantity < tariff.minQuantity || totalQuantity <= 0) {
            return PriceQuote.OnRequest(
                PriceQuote.OnRequest.Reason.BELOW_MIN_QUANTITY
            )
        }

        if (config.isRush && !tariff.rushAvailable) {
            return PriceQuote.OnRequest(
                PriceQuote.OnRequest.Reason.RUSH_NOT_AVAILABLE
            )
        }

        val discount = tariff.discountPercentFor(totalQuantity)

        var itemsTotal = 0L

        config.lines.forEach { line ->

            if (line.quantity <= 0) return@forEach

            val priced = priceLine(tariff, line.size)
                ?: return PriceQuote.OnRequest(
                    PriceQuote.OnRequest.Reason.NO_TARIFF
                )

            // Material va bosma turi ustamasi ham o'lchovga bog'liq:
            // 10 metrli bannerda orakal ustamasi har metrga qo'shiladi,
            // vizitkada esa har donaga.
            val optionsPrice =
                (config.optionsPricePerUnit * priced.measure).toLong()

            val baseUnit = priced.basePrice +
                    optionsPrice +
                    line.sizePricePerUnit

            val unitPrice = baseUnit * (100 - discount) / 100

            itemsTotal += unitPrice * line.quantity
        }

        if (config.isRush) {
            itemsTotal = (itemsTotal * tariff.rushMultiplier).toLong()
        }

        val deliveryPrice = when {
            !config.needsDelivery -> 0L

            tariff.freeDeliveryFrom > 0 &&
                    itemsTotal >= tariff.freeDeliveryFrom -> 0L

            else -> tariff.deliveryPrice
        }

        val productionDays =
            if (config.isRush) tariff.rushProductionDays
            else tariff.productionDays

        return PriceQuote.Available(
            // O'lchamlar aralash bo'lsa dona narxi o'rtacha bo'ladi.
            unitPrice = itemsTotal / totalQuantity,
            itemsTotal = itemsTotal,
            deliveryPrice = deliveryPrice,
            total = itemsTotal + deliveryPrice,
            productionDays = productionDays,
            discountPercent = discount,
            isRush = config.isRush
        )
    }

    /**
     * @param basePrice bitta dona uchun bazaviy summa
     * @param measure ustamalar ko'paytiriladigan o'lchov:
     *        dona uchun 1, kvadrat metr uchun maydon,
     *        pogon metr uchun uzunlik
     */
    private data class PricedLine(
        val basePrice: Long,
        val measure: Double
    )

    /** Hisoblab bo'lmasa null — narx qo'lda kelishiladi. */
    private fun priceLine(
        tariff: ShopTariff,
        size: ProductSize?
    ): PricedLine? = when (tariff.pricingUnit) {

        PricingUnit.PER_ITEM ->
            PricedLine(tariff.basePricePerUnit, 1.0)

        PricingUnit.PER_SQUARE_METER -> {

            val area = (size?.areaSquareMeters ?: 0.0)
                .coerceAtLeast(MIN_BILLABLE)

            PricedLine(
                basePrice = (tariff.basePricePerUnit * area).toLong(),
                measure = area
            )
        }

        PricingUnit.PER_LINEAR_METER -> {

            if (size == null || tariff.rolls.isEmpty()) {
                null
            } else {

                // Dizayn sig'adigan har bir rulon uchun narxni
                // hisoblab, eng arzonini tanlaymiz.
                tariff.rolls
                    .mapNotNull { roll ->

                        val meters = size.linearMeters(roll.widthMeters)
                            ?: return@mapNotNull null

                        val billable = meters.toDouble()
                            .coerceAtLeast(MIN_BILLABLE)

                        PricedLine(
                            basePrice = (roll.pricePerLinearMeter * billable).toLong(),
                            measure = billable
                        )
                    }
                    .minByOrNull { it.basePrice }
            }
        }
    }
}