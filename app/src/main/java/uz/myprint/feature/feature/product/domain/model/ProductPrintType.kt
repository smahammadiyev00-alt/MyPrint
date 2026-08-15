package uz.myprint.feature.feature.product.domain.model

data class ProductPrintType(

    val id: String,

    val name: String,

    val description: String = "",

    val additionalPrice: Long = 0L,

    val isDefault: Boolean = false

)