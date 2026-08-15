package uz.myprint.feature.feature.product.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.myprint.feature.feature.product.domain.usecase.GetProductByIdUseCase
import uz.myprint.feature.feature.product.domain.usecase.GetProductsUseCase
import uz.myprint.feature.feature.product.presentation.state.viewmode.ProductDetailEvent
import uz.myprint.feature.feature.product.presentation.state.viewmode.ProductUiState

fun onEvent(event: ProductDetailEvent) {

    when (event) {

        is ProductDetailEvent.MaterialSelected -> {

        }

        is ProductDetailEvent.PrintTypeSelected -> {

        }

        is ProductDetailEvent.SizeSelected -> {

        }

        ProductDetailEvent.IncreaseQuantity -> {

        }

        ProductDetailEvent.DecreaseQuantity -> {

        }

        ProductDetailEvent.OrderClicked -> {

        }

        ProductDetailEvent.AiDesignClicked -> {

        }

    }

}

class ProductViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())

    val uiState: StateFlow<ProductUiState> =
        _uiState.asStateFlow()

    init {
        loadProducts()
    }

    /**
     * Barcha mahsulotlarni yuklash
     */
    private fun loadProducts() {

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {

                val products = getProductsUseCase()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    products = products
                )

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Noma'lum xatolik yuz berdi."
                )

            }

        }

    }

    /**
     * Bitta mahsulotni yuklash
     */
    fun loadProductById(id: String) {

        viewModelScope.launch {

            Log.d("MyPrint", "==============================")
            Log.d("MyPrint", "Requested ID = $id")

            try {

                val product = getProductByIdUseCase(id)

                Log.d(
                    "MyPrint",
                    "Loaded Product = ${product?.id} | ${product?.name}"
                )

                _uiState.value = _uiState.value.copy(
                    selectedProduct = product,
                    error = null
                )

            } catch (e: Exception) {

                Log.e(
                    "MyPrint",
                    "loadProductById() Error",
                    e
                )

                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Mahsulotni yuklab bo'lmadi."
                )

            }

        }

    }

    /**
     * Tanlangan mahsulotni tozalash
     */
    fun clearSelectedProduct() {

        _uiState.value = _uiState.value.copy(
            selectedProduct = null
        )

    }

    /**
     * Xatolikni tozalash
     */
    fun clearError() {

        _uiState.value = _uiState.value.copy(
            error = null
        )

    }

    /**
     * Mahsulotlarni qayta yuklash
     */
    fun refresh() {
        loadProducts()
    }

}