package uz.myprint.feature.feature.product.data.dummy

import uz.myprint.feature.feature.product.domain.model.ProductPrintType

object PrintTypeDummyData {

    // Business Card
    val singleSide = ProductPrintType(
        id = "single_side",
        name = "Single Side",
        description = "Front side printing",
        isDefault = true
    )

    val doubleSide = ProductPrintType(
        id = "double_side",
        name = "Double Side",
        description = "Front and back printing"
    )

    val uvPrint = ProductPrintType(
        id = "uv_print",
        name = "UV Print",
        description = "Premium UV printing"
    )

    val laminate = ProductPrintType(
        id = "laminate",
        name = "Lamination",
        description = "Glossy or matte lamination"
    )

    // Banner
    val ecoSolvent = ProductPrintType(
        id = "eco_solvent",
        name = "Eco Solvent",
        description = "Outdoor banner printing",
        isDefault = true
    )

    val largeUv = ProductPrintType(
        id = "large_uv",
        name = "UV Banner Print",
        description = "High quality UV banner printing"
    )

    // T-Shirt
    val dtf = ProductPrintType(
        id = "dtf",
        name = "DTF",
        description = "Direct To Film",
        isDefault = true
    )

    val sublimation = ProductPrintType(
        id = "sublimation",
        name = "Sublimation",
        description = "Heat transfer printing"
    )

    val silkScreen = ProductPrintType(
        id = "silk_screen",
        name = "Silk Screen",
        description = "Screen printing"
    )
}