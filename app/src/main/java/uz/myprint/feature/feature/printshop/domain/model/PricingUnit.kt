package uz.myprint.feature.feature.printshop.domain.model

/**
 * Tarif nima uchun narx belgilaydi.
 *
 * PER_ITEM — vizitka, futbolka, bakal: dona bilan.
 *
 * PER_SQUARE_METER — kvadrat metr bilan. Ba'zi poligrafiyalar
 * bannerni shunday hisoblaydi.
 *
 * PER_LINEAR_METER — pogon metr. Rulon eni qat'iy (masalan 3.2 m),
 * mijoz faqat uzunlik uchun to'laydi. O'zbekistonda banner va
 * plyonka ko'pincha shunday sotiladi.
 */
enum class PricingUnit {

    PER_ITEM,

    PER_SQUARE_METER,

    PER_LINEAR_METER
}