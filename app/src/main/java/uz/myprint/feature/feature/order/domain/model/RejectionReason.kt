package uz.myprint.feature.feature.order.domain.model

/**
 * Poligrafiya nima uchun rad etdi.
 *
 * Sabab muhim, chunki mijozga nima qilishni aytish kerak:
 * band bo'lsa — boshqa poligrafiya, fayl yaroqsiz bo'lsa —
 * faylni almashtirish.
 */
enum class RejectionReason {

    /** Ish ko'p, muddatga ulgurmaydi. */
    BUSY,

    /** Material tugagan. */
    OUT_OF_MATERIAL,

    /** Uskuna ishlamayapti. */
    EQUIPMENT_DOWN,

    /** Fayl chop etishga yaroqsiz: past sifat, noto'g'ri format. */
    INVALID_FILE,

    /** Bu o'lcham yoki tirajni bajara olmaydi. */
    CANNOT_FULFILL,

    /** Boshqa sabab, izohda yoziladi. */
    OTHER;

    /** Mijoz faylni tuzatib qayta yuborishi mumkinmi. */
    val isFixableByCustomer: Boolean
        get() = this == INVALID_FILE

    /**
     * Boshqa poligrafiyaga yuborish mantiqiymi.
     * Fayl yaroqsiz bo'lsa boshqasi ham rad etadi.
     */
    val shouldSuggestOtherShops: Boolean
        get() = this != INVALID_FILE
}