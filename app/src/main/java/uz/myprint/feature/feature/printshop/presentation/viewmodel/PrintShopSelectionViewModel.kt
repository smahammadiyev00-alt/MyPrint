package uz.myprint.feature.feature.printshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.myprint.feature.feature.printshop.domain.model.ProductConfig
import uz.myprint.feature.feature.printshop.domain.usecase.GetPrintShopOffersUseCase
import uz.myprint.feature.feature.printshop.presentation.state.PrintShopSelectionUiState
import uz.myprint.feature.feature.product.domain.usecase.GetProductByIdUseCase

class PrintShopSelectionViewModel(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val getPrintShopOffersUseCase: GetPrintShopOffersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrintShopSelectionUiState())

    val uiState: StateFlow<PrintShopSelectionUiState> = _uiState.asStateFlow()

    /**
     * Navigatsiya argumentlaridan konfiguratsiyani qayta yig'adi.
     * ID'lar uzatiladi, obyektlar shu yerda mahsulotdan topiladi —
     * shunda jarayon o'ldirilsa ham holat tiklanadi.
     *
     * @param lines "m:10,l:15,xl:5" ko'rinishida kodlangan qatorlar.
     */
    fun load(
        productId: String,
        materialId: String,
        printTypeId: String,
        lines: String
    ) {

        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true, error = null) }

            try {

                val product = getProductByIdUseCase(productId)

                if (product == null) {

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Mahsulot topilmadi."
                        )
                    }

                    return@launch
                }

                val configLines = ProductConfig.decodeLines(
                    encoded = lines,
                    availableSizes = product.sizes
                )

                val config = ProductConfig(
                    productId = product.id,
                    category = product.category,
                    material = product.materials.firstOrNull { it.id == materialId },
                    printType = product.printTypes.firstOrNull { it.id == printTypeId },
                    lines = configLines,

                    // Yetkazib berish hozircha doim so'raladi. Keyinchalik
                    // checkout ekranida foydalanuvchi o'zi tanlaydi.
                    needsDelivery = true
                )

                val offers = getPrintShopOffersUseCase(
                    config = config,

                    // Vaqtinchalik: Toshkent markazi. Joylashuvga ruxsat
                    // qo'shilgach LocationProvider'dan keladi.
                    userLatitude = 41.3111,
                    userLongitude = 69.2797
                )

                _uiState.update {
                    it.copy(
                        productName = product.name,
                        quantity = config.quantity,
                        offers = offers,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Poligrafiyalarni yuklab bo'lmadi."
                    )
                }
            }
        }
    }
}