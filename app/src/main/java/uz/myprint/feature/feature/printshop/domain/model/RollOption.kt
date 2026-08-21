package uz.myprint.feature.feature.printshop.domain.model

/**
 * Poligrafiyadagi bitta rulon: eni va pogon metr narxi.
 *
 * O'zbekistonda keng tarqalgan enlar: 1000, 1200, 1500, 1700, 3000 mm.
 * Keng rulon qimmatroq turadi, shuning uchun dizayn sig'adigan
 * eng arzon rulon tanlanadi.
 */
data class RollOption(

    val widthMeters: Float,

    val pricePerLinearMeter: Long

) {

    /** "1.2 m" */
    val label: String
        get() = if (widthMeters == widthMeters.toInt().toFloat()) {
            "${widthMeters.toInt()} m"
        } else {
            "$widthMeters m"
        }
}