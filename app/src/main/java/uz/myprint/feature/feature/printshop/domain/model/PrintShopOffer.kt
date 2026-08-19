package uz.myprint.feature.feature.printshop.domain.model

/**
 * "Poligrafiya tanlash" ekranidagi bitta qator.
 *
 * Masofa PrintShop ichida saqlanmaydi — u foydalanuvchining joriy
 * joylashuviga bog'liq va har safar qayta hisoblanadi.
 */
data class PrintShopOffer(

    val shop: PrintShop,

    val quote: PriceQuote,

    /** Metrlarda. Joylashuvga ruxsat berilmagan bo'lsa null. */
    val distanceMeters: Int? = null

) {

    val hasPrice: Boolean
        get() = quote is PriceQuote.Available

    /** "350 m" yoki "2.1 km" */
    fun formattedDistance(): String? {

        val meters = distanceMeters ?: return null

        return if (meters < 1000) {
            "$meters m"
        } else {
            "%.1f km".format(meters / 1000.0)
        }
    }
}
