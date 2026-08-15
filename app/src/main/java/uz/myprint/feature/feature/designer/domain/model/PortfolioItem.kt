package uz.myprint.feature.feature.designer.domain.model

data class PortfolioItem(

    val id: String,

    val title: String,

    val previewImageRes: Int,

    val category: String,

    val description: String,

    val likes: Int,

    val views: Int,

    val isFeatured: Boolean,

    val designer: Designer

)