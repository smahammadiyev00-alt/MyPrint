package uz.myprint.feature.feature.product.presentation.viewmode

import uz.myprint.feature.feature.product.domain.model.Product

data class ProductUiState(

    val isLoading: Boolean = false,

    val products: List<Product> = emptyList(),

    val selectedProduct: Product? = null,

    val error: String? = null

)