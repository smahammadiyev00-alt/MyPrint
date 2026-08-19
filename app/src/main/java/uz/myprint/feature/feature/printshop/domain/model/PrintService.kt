package uz.myprint.feature.feature.printshop.domain.model

/**
 * Poligrafiya taklif qiladigan bosma turlari.
 * Maketdagi DTF / UV / Offset teglari shu enum ustiga quriladi.
 */
enum class PrintService {

    DTF,

    UV,

    OFFSET,

    DIGITAL,

    SCREEN,

    SUBLIMATION,

    LASER_CUT,

    EMBROIDERY,

    LAMINATION
}
