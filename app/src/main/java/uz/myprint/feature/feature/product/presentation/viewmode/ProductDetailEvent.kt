package uz.myprint.feature.feature.product.presentation.viewmode

import uz.myprint.feature.feature.product.domain.model.ProductMaterial
import uz.myprint.feature.feature.product.domain.model.ProductPrintType
import uz.myprint.feature.feature.product.domain.model.ProductSize

sealed interface ProductDetailEvent {

    data class MaterialSelected(
        val material: ProductMaterial
    ) : ProductDetailEvent

    data class PrintTypeSelected(
        val printType: ProductPrintType
    ) : ProductDetailEvent

    /** Laminatsiya / UV lak yoqildi yoki o'chirildi. */
    data class FinishToggled(
        val finish: ProductPrintType
    ) : ProductDetailEvent

    data class SizeSelected(
        val size: ProductSize
    ) : ProductDetailEvent

    /** Futbolka kabi mahsulotlarda: bitta o'lchamning soni o'zgardi. */
    data class SizeQuantityChanged(
        val sizeId: String,
        val quantity: Int
    ) : ProductDetailEvent

    data object IncreaseQuantity : ProductDetailEvent

    data object DecreaseQuantity : ProductDetailEvent

    data object OrderClicked : ProductDetailEvent

    data object AiDesignClicked : ProductDetailEvent
}