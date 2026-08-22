package uz.myprint.feature.feature.cart.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.myprint.feature.feature.cart.domain.model.CartItem
import uz.myprint.feature.feature.cart.presentation.state.CartGroup
import uz.myprint.feature.feature.cart.presentation.state.CartUiState
import uz.myprint.feature.feature.cart.domain.repository.CartRepository

class CartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState(isLoading = true))

    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        observeCart()
    }

    private fun observeCart() {

        viewModelScope.launch {

            cartRepository.observeItems().collect { items ->

                _uiState.update {
                    it.copy(
                        groups = items.groupToShops(),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun addItem(item: CartItem) {

        viewModelScope.launch {
            cartRepository.addItem(item)
        }
    }

    fun removeItem(itemId: String) {

        viewModelScope.launch {
            cartRepository.removeItem(itemId)
        }
    }

    fun removeShop(shopId: String) {

        viewModelScope.launch {
            cartRepository.removeByShop(shopId)
        }
    }

    fun clear() {

        viewModelScope.launch {
            cartRepository.clear()
        }
    }
}

/**
 * Poligrafiyalar bo'yicha guruhlash. Tartib savatga qo'shilish
 * tartibida qoladi, shunda pozitsiyalar sakramaydi.
 */
private fun List<CartItem>.groupToShops(): List<CartGroup> =
    this
        .groupBy { it.shopId }
        .map { (shopId, items) ->
            CartGroup(
                shopId = shopId,
                shopName = items.first().shopName,
                items = items.sortedBy { it.addedAtMillis }
            )
        }
        .sortedBy { group -> group.items.minOf { it.addedAtMillis } }
