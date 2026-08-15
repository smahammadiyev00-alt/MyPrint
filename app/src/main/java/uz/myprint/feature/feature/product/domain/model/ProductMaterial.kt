package uz.myprint.feature.feature.product.domain.model

data class ProductMaterial(

    val id: String,

    val name: String,

    val description: String = "",

    val thickness: String? = null,
    val additionalPrice: Long = 0L,

    val isDefault: Boolean = false,

)