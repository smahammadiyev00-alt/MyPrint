package uz.myprint.feature.feature.product.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.myprint.feature.feature.product.domain.usecase.GetProductByIdUseCase
import uz.myprint.feature.feature.product.presentation.state.viewmode.ProductDetailUiState

class ProductDetailViewModel(
    private val getProductByIdUseCase: GetProductByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())

    val uiState: StateFlow<ProductDetailUiState> =
        _uiState.asStateFlow()

    fun loadProduct(id: String) {

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {

                val product = getProductByIdUseCase(id)

                _uiState.value = _uiState.value.copy(
                    product = product,
                    isLoading = false
                )

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Mahsulotni yuklab bo'lmadi."
                )

            }

        }

    }

}