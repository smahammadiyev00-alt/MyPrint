package uz.myprint.feature.feature.product.presentation.state.viewmode

import uz.myprint.feature.feature.product.domain.model.PrintOptionKind
import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.domain.model.ProductMaterial
import uz.myprint.feature.feature.product.domain.model.ProductPrintType
import uz.myprint.feature.feature.product.domain.model.ProductSize
import uz.myprint.feature.feature.product.domain.model.isAvailableFor

data class ProductDetailUiState(

    val product: Product? = null,

    val selectedMaterial: ProductMaterial? = null,

    /** Taraf: 1 taraf yoki 2 taraf. Doim bittasi tanlangan bo'ladi. */
    val selectedPrintType: ProductPrintType? = null,

    /** Qo'shimcha qoplamalar: laminatsiya, UV lak. Bir nechtasi bo'lishi mumkin. */
    val selectedFinishIds: Set<String> = emptySet(),

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

    /** "Bosma turi" bo'limi uchun. */
    val sideOptions: List<ProductPrintType>
        get() = product?.printTypes
            .orEmpty()
            .filter { it.kind == PrintOptionKind.SIDE }

    /** "Qo'shimcha" bo'limi uchun. */
    val finishOptions: List<ProductPrintType>
        get() = product?.printTypes
            .orEmpty()
            .filter { it.kind == PrintOptionKind.FINISH }

    /**
     * Tanlangan qoplamalar. Material o'zgarganda mos kelmaydiganlari
     * ViewModel'da tozalanadi, bu yerda qo'shimcha filtr — himoya uchun.
     */
    val selectedFinishes: List<ProductPrintType>
        get() = finishOptions.filter {
            it.id in selectedFinishIds && it.isAvailableFor(selectedMaterial)
        }
}
