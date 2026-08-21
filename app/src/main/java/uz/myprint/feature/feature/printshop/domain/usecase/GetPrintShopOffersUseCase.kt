package uz.myprint.feature.feature.printshop.domain.usecase

import uz.myprint.feature.feature.printshop.domain.model.PrintShopOffer
import uz.myprint.feature.feature.printshop.domain.model.ProductConfig
import uz.myprint.feature.feature.printshop.domain.pricing.PriceCalculator
import uz.myprint.feature.feature.printshop.domain.repository.PrintShopRepository
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Konfiguratsiya bo'yicha barcha poligrafiyalarning takliflarini qaytaradi.
 * Narx PriceCalculator orqali hisoblanadi, masofa foydalanuvchi
 * joylashuvidan (berilgan bo'lsa).
 */
class GetPrintShopOffersUseCase(
    private val repository: PrintShopRepository
) {

    suspend operator fun invoke(
        config:
        ProductConfig,
        userLatitude: Double? = null,
        userLongitude: Double? = null
    ): List<PrintShopOffer> {

        return repository.getPrintShops().map { shop ->

            val distance =
                if (userLatitude != null && userLongitude != null) {
                    distanceInMeters(
                        userLatitude, userLongitude,
                        shop.latitude, shop.longitude
                    )
                } else {
                    null
                }

            PrintShopOffer(
                shop = shop,
                quote = PriceCalculator.quote(shop, config),
                distanceMeters = distance
            )
        }
    }
}

private const val EARTH_RADIUS_METERS = 6_371_000.0

/**
 * Haversine. Shahar ichidagi masofalar uchun yetarli aniqlikda.
 */
private fun distanceInMeters(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
): Int {

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return (EARTH_RADIUS_METERS * c).roundToInt()
}
