package uz.myprint.feature.feature.order.domain.model

import uz.myprint.feature.feature.cart.domain.model.CartItem

/**
 * Bitta poligrafiyaga yuborilgan buyurtma.
 *
 * Muhim qaror: bitta buyurtma — bitta poligrafiya. Savatda turli
 * poligrafiyalarning mahsulotlari bo'lsa, checkout ularni alohida
 * buyurtmalarga bo'ladi. Sababi oddiy: har bir poligrafiya mustaqil
 * qabul qiladi yoki rad etadi, ularni bitta holatga bog'lab
 * bo'lmaydi.
 */
data class Order(

    val id: String,

    val orderNumber: String,

    val customerId: String,

    val shopId: String,

    val shopName: String,

    val items: List<CartItem>,

    val status: OrderStatus = OrderStatus.PENDING,

    /** Rad etilgan bo'lsa to'ldiriladi. */
    val rejectionReason: RejectionReason? = null,
    val rejectionNote: String? = null,

    /**
     * Urinib ko'rilgan poligrafiyalar, joriysi ham shu ro'yxatda.
     * Rad etilgandan keyin muqobil taklif qilishda ular chiqarib
     * tashlanadi — bir poligrafiyaga ikki marta yuborilmasin.
     */
    val triedShopIds: List<String> = emptyList(),

    // Yetkazib berish
    val needsDelivery: Boolean = false,
    val deliveryAddress: String? = null,
    val contactPhone: String,

    // Summalar. Buyurtma yuborilgan paytda qotib qoladi.
    val itemsTotal: Long,
    val deliveryPrice: Long = 0L,
    val total: Long,

    // Vaqtlar
    val createdAtMillis: Long = System.currentTimeMillis(),
    val respondByMillis: Long? = null,
    val acceptedAtMillis: Long? = null,
    val readyAtMillis: Long? = null,
    val completedAtMillis: Long? = null,

    val statusHistory: List<OrderStatusChange> = emptyList()

) {

    val isActive: Boolean
        get() = !status.isFinal

    /**
     * Poligrafiya javob berish muddati o'tdimi.
     * Kutish holatida bo'lmasa, ma'nosi yo'q.
     */
    fun isExpiredAt(nowMillis: Long): Boolean =
        status == OrderStatus.PENDING &&
                respondByMillis != null &&
                nowMillis > respondByMillis

    /**
     * Rad etilgandan keyin mijozga boshqa poligrafiya taklif
     * qilinadimi. Fayl yaroqsiz bo'lsa taklif qilinmaydi —
     * avval fayl tuzatilishi kerak.
     */
    val shouldSuggestOtherShops: Boolean
        get() = (status == OrderStatus.REJECTED &&
                rejectionReason?.shouldSuggestOtherShops == true) ||
                status == OrderStatus.EXPIRED
}

/**
 * Holat o'zgarishi tarixi. Nizo chiqqanda kim nima qilganini
 * ko'rsatadi, shuning uchun buyurtma bilan birga saqlanadi.
 */
data class OrderStatusChange(

    val status: OrderStatus,

    val atMillis: Long,

    /** Kim o'zgartirdi: mijoz, poligrafiya yoki tizim. */
    val actor: Actor,

    val note: String? = null

) {

    enum class Actor {
        CUSTOMER,
        SHOP,
        SYSTEM
    }
}