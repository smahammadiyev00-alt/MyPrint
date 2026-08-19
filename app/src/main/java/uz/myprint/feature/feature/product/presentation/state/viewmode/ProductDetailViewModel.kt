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
import uz.myprint.feature.feature.product.domain.usecase.GetProductByIdUseCase
import uz.myprint.feature.feature.product.domain.usecase.GetProductsByCategoryUseCase
import uz.myprint.feature.feature.product.presentation.state.viewmode.ProductDetailEvent
import uz.myprint.feature.feature.product.presentation.state.viewmode.ProductDetailUiState

class ProductDetailViewModel(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())

    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProduct(id: String) {

        load { getProductByIdUseCase(id) }
    }

    /**
     * Kategoriya bo'yicha ochish: bosh sahifadagi kategoriya bosilganda
     * ro'yxat ekranini o'tkazib yuborib to'g'ridan-to'g'ri shu yerga keladi.
     *
     * Hozircha har bir kategoriyada bitta mahsulot bor. Agar kelajakda
     * bittadan ko'p bo'lsa, ro'yxat ekrani qaytadan kerak bo'ladi.
     */
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

                _uiState.update { state ->
                    state.copy(
                        product = product,
                        isLoading = false,

                        // Standart variantlar avtomatik tanlanadi, aks holda
                        // foydalanuvchi hech narsa bosmasa konfiguratsiya
                        // bo'sh qoladi va narx hisoblanmaydi.
                        selectedMaterial = product.materials
                            .let { list -> list.firstOrNull { it.isDefault } ?: list.firstOrNull() },

                        selectedPrintType = product.printTypes
                            .let { list -> list.firstOrNull { it.isDefault } ?: list.firstOrNull() },

                        selectedSize = product.sizes
                            .let { list -> list.firstOrNull { it.isDefault } ?: list.firstOrNull() },

                        quantity = minQuantityFor(product)
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

    fun onEvent(event: ProductDetailEvent) {

        when (event) {

            is ProductDetailEvent.MaterialSelected ->
                _uiState.update { it.copy(selectedMaterial = event.material) }

            is ProductDetailEvent.PrintTypeSelected ->
                _uiState.update { it.copy(selectedPrintType = event.printType) }

            is ProductDetailEvent.SizeSelected ->
                _uiState.update { it.copy(selectedSize = event.size) }

            ProductDetailEvent.IncreaseQuantity -> changeQuantityBy(+1)

            ProductDetailEvent.DecreaseQuantity -> changeQuantityBy(-1)

            // Navigatsiya hodisalari — ularni ekranning o'zi hal qiladi.
            ProductDetailEvent.OrderClicked -> Unit

            ProductDetailEvent.AiDesignClicked -> Unit
        }
    }

    fun setQuantity(value: Int) {

        val min = minQuantityFor(_uiState.value.product)

        _uiState.update { it.copy(quantity = value.coerceAtLeast(min)) }
    }

    private fun changeQuantityBy(direction: Int) {

        val state = _uiState.value

        val step = stepFor(state.product)

        val min = minQuantityFor(state.product)

        val next = (state.quantity + direction * step).coerceAtLeast(min)

        _uiState.update { it.copy(quantity = next) }
    }

    /**
     * Vizitka 100 donadan, futbolka 1 donadan buyurtma qilinadi.
     * Hozircha kategoriyaga qarab; keyinchalik Product modeliga ko'chadi.
     */
    private fun minQuantityFor(product: Product?): Int =
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
            ProductCategory.STICKER -> 100

            else -> 1
        }
}