package uz.myprint.feature.feature.product.data.dummy

import uz.myprint.feature.feature.product.domain.model.ProductColorMode

object ColorModeDummyData {

    val cmyk = ProductColorMode(
        id = "cmyk",
        name = "CMYK",
        description = "Professional print color mode",
        isDefault = true
    )

    val rgb = ProductColorMode(
        id = "rgb",
        name = "RGB",
        description = "Screen color mode"
    )

    val pantone = ProductColorMode(
        id = "pantone",
        name = "Pantone",
        description = "Pantone spot colors"
    )

    val grayscale = ProductColorMode(
        id = "grayscale",
        name = "Grayscale",
        description = "Black and white printing"
    )

}