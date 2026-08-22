package uz.myprint.feature.feature.order.domain.model

/**
 * Buyurtma holati.
 *
 * O'tishlar aniq belgilangan: har bir holatdan faqat ruxsat etilgan
 * holatga o'tish mumkin. Bu backend ulanganda ham o'zgarmaydi —
 * server xuddi shu qoidalarni tekshiradi.
 *
 *   PENDING ──► ACCEPTED ──► IN_PRODUCTION ──► READY ──► COMPLETED
 *      │            │                            │
 *      ├──► REJECTED│                            └──► DELIVERING ──► COMPLETED
 *      │            │
 *      └──► EXPIRED └──► CANCELLED
 */
enum class OrderStatus {

    /** Yuborildi, poligrafiya javobini kutmoqda. */
    PENDING,

    /** Poligrafiya qabul qildi. */
    ACCEPTED,

    /** Poligrafiya rad etdi. Mijozga boshqa variant taklif qilinadi. */
    REJECTED,

    /** Poligrafiya belgilangan vaqtda javob bermadi. */
    EXPIRED,

    /** Bosilmoqda. */
    IN_PRODUCTION,

    /** Tayyor, olib ketish yoki yetkazishga tayyor. */
    READY,

    /** Yo'lda. */
    DELIVERING,

    /** Topshirildi. */
    COMPLETED,

    /** Mijoz bekor qildi. */
    CANCELLED;

    val isFinal: Boolean
        get() = this in setOf(REJECTED, EXPIRED, COMPLETED, CANCELLED)

    /** Mijoz shu holatda bekor qila oladimi. */
    val isCancellableByCustomer: Boolean
        get() = this in setOf(PENDING, ACCEPTED)

    fun canTransitionTo(next: OrderStatus): Boolean = next in when (this) {

        PENDING -> setOf(ACCEPTED, REJECTED, EXPIRED, CANCELLED)

        ACCEPTED -> setOf(IN_PRODUCTION, CANCELLED)

        IN_PRODUCTION -> setOf(READY)

        READY -> setOf(DELIVERING, COMPLETED)

        DELIVERING -> setOf(COMPLETED)

        REJECTED, EXPIRED, COMPLETED, CANCELLED -> emptySet()
    }
}