package uz.myprint.feature.feature.cart.data.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.myprint.feature.feature.cart.domain.model.CartItem
import uz.myprint.feature.feature.cart.domain.repository.CartRepository
import uz.myprint.feature.feature.cart.presentation.state.CartGroup
import uz.myprint.feature.feature.cart.presentation.state.CartUiState
import kotlinx.coroutines.flow.Flow

/**
 * Savat xotirada saqlanadi — ilova yopilganda yo'qoladi.
 *
 * Bu vaqtinchalik. Keyingi bosqichda DataStore yoki Room qo'shiladi,
 * shunda mijoz savatni yo'qotmaydi. Interfeys o'zgarmaydi.
 */
class InMemoryCartRepository : CartRepository {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())

    override fun observeItems(): Flow<List<CartItem>> = _items.asStateFlow()

    override suspend fun addItem(item: CartItem) {

        _items.update { current -> current + item }
    }

    override suspend fun removeItem(itemId: String) {

        _items.update { current -> current.filterNot { it.id == itemId } }
    }

    override suspend fun clear() {

        _items.value = emptyList()
    }

    override suspend fun removeByShop(shopId: String) {

        _items.update { current -> current.filterNot { it.shopId == shopId } }
    }
}
