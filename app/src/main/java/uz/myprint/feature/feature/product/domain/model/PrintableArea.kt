package uz.myprint.feature.feature.product.domain.model

data class PrintableArea(

    val id: String,

    val title: String,

    val width: Float,

    val height: Float,

    val unit: SizeUnit,

    val shape: PrintableShape = PrintableShape.RECTANGLE,

    val isDefault: Boolean = false

)