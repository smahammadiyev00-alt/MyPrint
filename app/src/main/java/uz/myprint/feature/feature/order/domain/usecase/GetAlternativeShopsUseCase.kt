package uz.myprint.feature.feature.order.domain.usecase

import uz.myprint.feature.feature.order.domain.model.Order
import uz.myprint.feature.feature.printshop.domain.model.PriceQuote
import uz.myprint.feature.feature.printshop.domain.model.PrintShopOffer
import uz.myprint.feature.feature.printshop.domain.usecase.GetPrintShopOffersUseCase

/**
 * Rad etilgan yoki javobsiz qolgan buyurtma uchun muqobil
 * poligrafiyalarni topadi.
 *
 * Poligrafiya band bo'lgani uchun rad etishi normal holat —
 * mijozni boshi berk ko'chada qoldirmaslik kerak. Shuning uchun
 * xuddi shu konfiguratsiya bo'yicha qolgan poligrafiyalarning
 * narxlari qayta hisoblanadi.
 */
class GetAlternativeShopsUseCase(
    private val getPrintShopOffersUseCase: GetPrintShopOffersUseCase
) {

    suspend operator fun invoke(
        order: Order,
        userLatitude: Double? = null,
        userLongitude: Double? = null
    ): List<PrintShopOffer> {

        // Buyurtmada bir nechta pozitsiya bo'lishi mumkin, lekin
        // hammasi bitta konfiguratsiyaga tegishli — birinchisini
        // asos qilib olamiz.
        val config = order.items.firstOrNull()?.config
            ?: return emptyList()

        val tried = order.triedShopIds.toSet() + order.shopId

        return getPrintShopOffersUseCase(
            config = config,
            userLatitude = userLatitude,
            userLongitude = userLongitude
        )
            .filter { offer ->

                // Urinilganlarni va buyurtma qabul qilmayotganlarni
                // ko'rsatmaymiz.
                offer.shop.id !in tried && offer.shop.isAcceptingOrders
            }
            .sortedWith(
                compareBy(
                    // Narxi borlari oldinda, keyin arzonidan boshlab.
                    { (it.quote as? PriceQuote.Available)?.total ?: Long.MAX_VALUE },
                    { it.shop.name }
                )
            )
    }
}