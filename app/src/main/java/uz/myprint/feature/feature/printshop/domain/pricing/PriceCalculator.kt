package uz.myprint.feature.feature.printshop.domain.pricing

import uz.myprint.feature.feature.printshop.domain.model.PriceQuote
import uz.myprint.feature.feature.printshop.domain.model.PrintShop
import uz.myprint.feature.feature.printshop.domain.model.ProductConfig

/**
 * Narx hisoblash.
 *
 * Deterministik funksiya: bir xil kirish har doim bir xil natija beradi.
 * Narxni hech qachon AI hisoblamaydi — mijoz to'laydigan summa
 * testlanadigan formula bo'lishi kerak.
 *
 * Har bir qator alohida hisoblanadi, chunki o'lchamning qo'shimcha narxi
 * har xil bo'lishi mumkin (XXL futbolka S dan qimmatroq). Tirajga
 * beriladigan chegirma esa jami songa qarab belgilanadi.
 *
 *   qatorDona  = tarif.bazaviy + material + bosmaTuri + o'lcham
 *   qatorNarx  = qatorDona * (100 - chegirma) / 100 * qatorSoni
 *   mahsulotlar = qatorlar yig'indisi
 *   shoshilinch = mahsulotlar * rushMultiplier
 *   yetkazish  = summa >= freeDeliveryFrom ? 0 : deliveryPrice
 *   jami       = mahsulotlar + yetkazish
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

            val baseUnit = tariff.basePricePerUnit +
                    config.optionsPricePerUnit +
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
}
