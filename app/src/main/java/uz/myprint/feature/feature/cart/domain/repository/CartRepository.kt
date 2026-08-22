package uz.myprint.feature.feature.cart.domain.repository

import kotlinx.coroutines.flow.Flow
import uz.myprint.feature.feature.cart.domain.model.CartItem

interface CartRepository {

    /** Savat o'zgarganda UI avtomatik yangilanadi. */
    fun observeItems(): Flow<List<CartItem>>

    suspend fun addItem(item: CartItem)

    suspend fun removeItem(itemId: String)

    suspend fun clear()

    /** Faqat bitta poligrafiyaning pozitsiyalarini o'chirish. */
    suspend fun removeByShop(shopId: String)
}