package uz.myprint.feature.feature.product.presentation.viewmode

import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.domain.model.ProductMaterial
import uz.myprint.feature.feature.product.domain.model.ProductPrintType
import uz.myprint.feature.feature.product.domain.model.ProductSize

data class ProductDetailUiState(

    val product: Product? = null,

    val selectedMaterial: ProductMaterial? = null,

    val selectedPrintType: ProductPrintType? = null,

    /** Bitta o'lchamli mahsulotlar uchun (vizitka, banner). */
    val selectedSize: ProductSize? = null,

    /** Bitta o'lchamli mahsulotlar uchun soni. */
    val quantity: Int = 1,

    /**
     * O'lcham bo'yicha taqsimot (futbolka): sizeId -> soni.
     * Bitta o'lchamli mahsulotlarda bo'sh qoladi.
     */
    val sizeQuantities: Map<String, Int> = emptyMap(),

    val isLoading: Boolean = false,

    val error: String? = null

) {

    /** Buyurtmadagi umumiy son — ikkala rejim uchun. */
    val totalQuantity: Int
        get() = if (sizeQuantities.isNotEmpty()) {
            sizeQuantities.values.sum()
        } else {
            quantity
        }
}
