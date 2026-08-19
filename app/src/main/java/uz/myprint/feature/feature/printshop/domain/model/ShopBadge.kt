package uz.myprint.feature.feature.printshop.domain.model

/**
 * Ishonch belgilari.
 *
 * Ataylab qisqa: maketda PREMIUM, TOP, Gold Partner va verified belgilari
 * bir vaqtda ko'rsatilgan edi. Foydalanuvchi to'rtta signalni farqlay olmaydi,
 * shuning uchun bir kartochkada eng ko'p ikkitasini ko'rsatish tavsiya etiladi.
 *
 * PARTNER — pullik joylashtirish, UI'da oshkor qilinishi kerak.
 */
enum class ShopBadge {

    VERIFIED,

    TOP,

    PARTNER
}
