package uz.myprint.feature.feature.product.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.domain.model.ProductCategory
import uz.myprint.feature.feature.product.domain.model.usesSizeBreakdown
import uz.myprint.feature.feature.product.domain.usecase.GetProductByIdUseCase
import uz.myprint.feature.feature.product.domain.usecase.GetProductsByCategoryUseCase
import uz.myprint.feature.feature.product.presentation.viewmode.ProductDetailEvent
import uz.myprint.feature.feature.product.presentation.viewmode.ProductDetailUiState

/** Ilova darajasidagi eng kichik tiraj. Haqiqiy chegarani
 *  poligrafiyaning tarifi belgilaydi — biz uni oldindan to'smaymiz. */
private const val MIN_QUANTITY = 1

class ProductDetailViewModel(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(_root_ide_package_.uz.myprint.feature.feature.product.presentation.viewmode.ProductDetailUiState())

    val uiState: StateFlow<uz.myprint.feature.feature.product.presentation.viewmode.ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProduct(id: String) {

        load { getProductByIdUseCase(id) }
    }

    fun loadProductByCategory(categoryName: String) {

        val category = runCatching {
            ProductCategory.valueOf(categoryName)
        }.getOrElse {
            ProductCategory.ALL
        }

        load {
            getProductsByCategoryUseCase(category).firstOrNull()
        }
    }

    private fun load(block: suspend () -> Product?) {

        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true, error = null) }

            try {

                val product = block()

                if (product == null) {

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Mahsulot topilmadi."
                        )
                    }

                    return@launch
                }

                val breakdown = product.category.usesSizeBreakdown

                val defaultSize = product.sizes
                    .let { list -> list.firstOrNull { it.isDefault } ?: list.firstOrNull() }

                _uiState.update { state ->
                    state.copy(
                        product = product,
                        isLoading = false,

                        selectedMaterial = product.materials
                            .let { list -> list.firstOrNull { it.isDefault } ?: list.firstOrNull() },

                        selectedPrintType = product.printTypes
                            .let { list -> list.firstOrNull { it.isDefault } ?: list.firstOrNull() },

                        selectedSize = if (breakdown) null else defaultSize,

                        quantity = if (breakdown) 0 else startingQuantityFor(product),

                        // Taqsimot rejimida standart o'lchamga 1 ta qo'yiladi,
                        // shunda buyurtma tugmasi darhol ma'noli bo'ladi.
                        sizeQuantities = if (breakdown) {
                            product.sizes.associate { size ->
                                size.id to if (size.id == defaultSize?.id) 1 else 0
                            }
                        } else {
                            emptyMap()
                        }
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Mahsulotni yuklab bo'lmadi."
                    )
                }
            }
        }
    }

    fun onEvent(event: uz.myprint.feature.feature.product.presentation.viewmode.ProductDetailEvent) {

        when (event) {

            is ProductDetailEvent.MaterialSelected ->
                _uiState.update { it.copy(selectedMaterial = event.material) }

            is ProductDetailEvent.PrintTypeSelected ->
                _uiState.update { it.copy(selectedPrintType = event.printType) }

            is ProductDetailEvent.SizeSelected ->
                _uiState.update { it.copy(selectedSize = event.size) }

            is ProductDetailEvent.SizeQuantityChanged ->
                _uiState.update { state ->
                    state.copy(
                        sizeQuantities = state.sizeQuantities.toMutableMap().apply {
                            this[event.sizeId] = event.quantity.coerceAtLeast(0)
                        }
                    )
                }

            ProductDetailEvent.IncreaseQuantity -> changeQuantityBy(+1)

            ProductDetailEvent.DecreaseQuantity -> changeQuantityBy(-1)

            ProductDetailEvent.OrderClicked -> Unit

            ProductDetailEvent.AiDesignClicked -> Unit
        }
    }

    fun setQuantity(value: Int) {

        _uiState.update {
            it.copy(quantity = value.coerceAtLeast(MIN_QUANTITY))
        }
    }

    /**
     * +/- tugmalari. Agar joriy qiymat qadamga bo'linmasa (masalan
     * qo'lda 150 kiritilgan), avval eng yaqin qadamga tekislanadi:
     * 150 → + → 200, 150 → − → 100.
     */
    private fun changeQuantityBy(direction: Int) {

        val state = _uiState.value

        val step = stepFor(state.product)

        val current = state.quantity

        val next = when {
            current % step == 0 -> current + direction * step
            direction > 0 -> (current / step + 1) * step
            else -> (current / step) * step
        }

        _uiState.update {
            it.copy(quantity = next.coerceAtLeast(MIN_QUANTITY))
        }
    }

    private fun startingQuantityFor(product: Product?): Int =
        when (product?.category) {

            ProductCategory.BUSINESS_CARD,
            ProductCategory.FLYER,
            ProductCategory.STICKER -> 100

            else -> 1
        }

    private fun stepFor(product: Product?): Int =
        when (product?.category) {

            ProductCategory.BUSINESS_CARD,
            ProductCategory.FLYER,
            ProductCategory.STICKER -> 50

            else -> 1
        }
}
