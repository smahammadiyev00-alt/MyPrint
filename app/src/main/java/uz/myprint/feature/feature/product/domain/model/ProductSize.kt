package uz.myprint.feature.feature.product.domain.model

data class ProductSize(

    val id: String,

    val title: String,

    val width: Float,

    val height: Float,

    val unit: SizeUnit,

    val additionalPrice: Long = 0L,

    val isDefault: Boolean = false

)