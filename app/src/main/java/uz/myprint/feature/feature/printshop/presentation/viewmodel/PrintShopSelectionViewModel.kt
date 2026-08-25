package uz.myprint.feature.feature.printshop.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.myprint.feature.feature.cart.domain.model.CartItem
import uz.myprint.feature.feature.printshop.domain.model.PrintShopOffer
import uz.myprint.feature.feature.printshop.domain.model.ProductConfig
import uz.myprint.feature.feature.printshop.domain.usecase.GetPrintShopOffersUseCase
import uz.myprint.feature.feature.printshop.presentation.state.PrintShopSelectionUiState
import uz.myprint.feature.feature.product.domain.usecase.GetProductByIdUseCase
import java.util.UUID
import uz.myprint.feature.feature.cart.domain.repository.CartRepository

class PrintShopSelectionViewModel(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val getPrintShopOffersUseCase: GetPrintShopOffersUseCase,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrintShopSelectionUiState())

    val uiState: StateFlow<PrintShopSelectionUiState> = _uiState.asStateFlow()

    /** Savatga qo'shilgach ekran yopilishi uchun. */
    private val _addedToCart = MutableStateFlow(false)

    val addedToCart: StateFlow<Boolean> = _addedToCart.asStateFlow()

    private var loadedConfig: ProductConfig? = null

    private var loadedProductName: String = ""

    /**
     * Navigatsiya argumentlaridan konfiguratsiyani qayta yig'adi.
     * ID'lar uzatiladi, obyektlar shu yerda mahsulotdan topiladi —
     * shunda jarayon o'ldirilsa ham holat tiklanadi.
     */
    fun load(
        productId: String,
        materialId: String,
        printTypeId: String,
        finishIds: String,
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

                val config = ProductConfig(
                    productId = product.id,
                    category = product.category,
                    material = product.materials.firstOrNull { it.id == materialId },
                    printType = product.printTypes.firstOrNull { it.id == printTypeId },
                    finishes = ProductConfig.decodeFinishes(finishIds, product.printTypes),
                    lines = ProductConfig.decodeLines(lines, product.sizes),

                    // Yetkazib berish hozircha doim so'raladi. Keyinchalik
                    // checkout ekranida foydalanuvchi o'zi tanlaydi.
                    needsDelivery = true
                )

                loadedConfig = config
                loadedProductName = product.name

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

    fun addToCart(offer: PrintShopOffer) {

        val config = loadedConfig ?: return

        viewModelScope.launch {

            cartRepository.addItem(
                CartItem(
                    id = UUID.randomUUID().toString(),
                    productId = config.productId,
                    productName = loadedProductName,
                    config = config,
                    shopId = offer.shop.id,
                    shopName = offer.shop.name,
                    quote = offer.quote
                )
            )

            _addedToCart.value = true
        }
    }
}