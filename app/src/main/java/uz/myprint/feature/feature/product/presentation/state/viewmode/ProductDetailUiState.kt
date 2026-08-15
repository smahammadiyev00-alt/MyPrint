package uz.myprint.feature.feature.product.presentation.state.viewmode

import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.domain.model.ProductMaterial
import uz.myprint.feature.feature.product.domain.model.ProductPrintType
import uz.myprint.feature.feature.product.domain.model.ProductSize

data class ProductDetailUiState(

    val product: Product? = null,

    val selectedMaterial: ProductMaterial? = null,

    val selectedPrintType: ProductPrintType? = null,

    val selectedSize: ProductSize? = null,

    val quantity: Int = 1,

    val isLoading: Boolean = false,

    val error: String? = null

)