package uz.myprint.feature.feature.cart.presentation.state

import uz.myprint.feature.feature.cart.domain.model.CartItem

/**
 * Savat poligrafiyalar bo'yicha guruhlanadi, chunki har bir
 * poligrafiya alohida buyurtma bo'lib ketadi va alohida
 * qabul qiladi yoki rad etadi. Mijoz buni oldindan ko'rishi kerak.
 */
data class CartUiState(

    val groups: List<CartGroup> = emptyList(),

    val isLoading: Boolean = false

) {

    val isEmpty: Boolean
        get() = groups.isEmpty()

    val itemCount: Int
        get() = groups.sumOf { it.items.size }

    val total: Long
        get() = groups.sumOf { it.total }

    /** Nechta alohida buyurtma yuboriladi. */
    val orderCount: Int
        get() = groups.size
}

data class CartGroup(

    val shopId: String,

    val shopName: String,

    val items: List<CartItem>

) {

    val total: Long
        get() = items.sumOf { it.total }

    /** Narxi hisoblanmaganlar bo'lsa, summa to'liq emas. */
    val hasQuoteOnRequest: Boolean
        get() = items.any { !it.isReadyToOrder && it.total == 0L }
}
