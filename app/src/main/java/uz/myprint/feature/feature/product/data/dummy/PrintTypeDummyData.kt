package uz.myprint.feature.feature.product.data.dummy

import uz.myprint.feature.feature.product.domain.model.PrintOptionKind
import uz.myprint.feature.feature.product.domain.model.ProductPrintType

object PrintTypeDummyData {

    // Business Card
    // Taraf — bir-birini istisno qiladi.
    val singleSide = ProductPrintType(
        id = "single_side",
        name = "1 taraf",
        description = "Faqat old tomoni bosiladi",
        kind = PrintOptionKind.SIDE,
        isDefault = true
    )

    val doubleSide = ProductPrintType(
        id = "double_side",
        name = "2 taraf",
        description = "Old va orqa tomoni bosiladi",
        kind = PrintOptionKind.SIDE
    )

    // Qo'shimcha qoplamalar — tarafdan mustaqil, birga tanlanadi.
    val uvPrint = ProductPrintType(
        id = "uv_print",
        name = "UV Print",
        description = "Tanlangan joyga relefli UV lak",
        kind = PrintOptionKind.FINISH,
        allowedMaterialIds = MaterialDummyData.softTouchIds,
        unavailableHint = "Faqat Soft Touch qog'ozida"
    )

    val laminate = ProductPrintType(
        id = "laminate",
        name = "Lamination",
        description = "Glyansli yoki matoviy laminatsiya",
        kind = PrintOptionKind.FINISH
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