package uz.myprint.feature.feature.product.domain.model

data class ProductColorMode(

    val id: String,

    val name: String,

    val description: String = "",

    val isDefault: Boolean = false

)