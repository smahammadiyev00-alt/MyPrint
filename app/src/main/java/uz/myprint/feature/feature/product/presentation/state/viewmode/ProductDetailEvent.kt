package uz.myprint.feature.feature.product.presentation.state.viewmode

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

    data class SizeSelected(
        val size: ProductSize
    ) : ProductDetailEvent

    data object IncreaseQuantity : ProductDetailEvent

    data object DecreaseQuantity : ProductDetailEvent

    data object OrderClicked : ProductDetailEvent

    data object AiDesignClicked : ProductDetailEvent

}