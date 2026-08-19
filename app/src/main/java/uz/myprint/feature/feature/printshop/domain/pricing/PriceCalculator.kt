package uz.myprint.feature.feature.printshop.domain.pricing

import uz.myprint.feature.feature.printshop.domain.model.PriceQuote
import uz.myprint.feature.feature.printshop.domain.model.PrintShop
import uz.myprint.feature.feature.printshop.domain.model.ProductConfig

/**
 * Narx hisoblash.
 *
 * Bu deterministik funksiya: bir xil kirish har doim bir xil natija beradi.
 * Narxni hech qachon AI hisoblamaydi — mijoz to'laydigan summa
 * testlanadigan formula bo'lishi kerak.
 *
 * Formula:
 *   donaBazaviy = tarif.bazaviyNarx + material + o'lcham + bosmaTuri
 *   donaNarx    = donaBazaviy * (100 - tirajChegirmasi) / 100
 *   mahsulotlar = donaNarx * soni
 *   shoshilinch = mahsulotlar * rushMultiplier
 *   yetkazish   = summa >= freeDeliveryFrom ? 0 : deliveryPrice
 *   jami        = mahsulotlar + yetkazish
 */
object PriceCalculator {

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

        if (config.quantity < tariff.minQuantity) {
            return PriceQuote.OnRequest(
                PriceQuote.OnRequest.Reason.BELOW_MIN_QUANTITY
            )
        }

        if (config.isRush && !tariff.rushAvailable) {
            return PriceQuote.OnRequest(
                PriceQuote.OnRequest.Reason.RUSH_NOT_AVAILABLE
            )
        }

        val baseUnit = tariff.basePricePerUnit + config.optionsPricePerUnit

        val discount = tariff.discountPercentFor(config.quantity)

        val unitPrice = baseUnit * (100 - discount) / 100

        var itemsTotal = unitPrice * config.quantity

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
            unitPrice = unitPrice,
            itemsTotal = itemsTotal,
            deliveryPrice = deliveryPrice,
            total = itemsTotal + deliveryPrice,
            productionDays = productionDays,
            discountPercent = discount,
            isRush = config.isRush
        )
    }
}