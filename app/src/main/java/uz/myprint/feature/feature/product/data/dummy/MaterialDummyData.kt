package uz.myprint.feature.feature.product.data.dummy

import uz.myprint.feature.feature.product.domain.model.ProductMaterial

object MaterialDummyData {

    // Business Card
    val art300 = ProductMaterial(
        id = "art300",
        name = "Art Paper",
        thickness = "300g",
        description = "Premium coated paper",
        isDefault = true
    )

    val art350 = ProductMaterial(
        id = "art350",
        name = "Art Paper",
        thickness = "350g",
        description = "Extra thick coated paper"
    )

    val kraft = ProductMaterial(
        id = "kraft",
        name = "Kraft Paper",
        thickness = "300g",
        description = "Natural kraft paper"
    )

    val laminated = ProductMaterial(
        id = "laminated",
        name = "Laminated Paper",
        thickness = "350g",
        description = "Matte or glossy laminated"
    )

    // Banner
    val banner = ProductMaterial(
        id = "banner",
        name = "Banner",
        description = "Ichki va tashqi reklama uchun standart banner matosi.",
        isDefault = true
    )

    val bekPrint = ProductMaterial(
        id = "bek_print",
        name = "Bek print",
        description = "Roll Up uchun zich, orqasi qoraytirilgan material.",
        additionalPrice = 12_000
    )

    val orakalTransparent = ProductMaterial(
        id = "orakal_transparent",
        name = "Shaffof orakal",
        description = "Oyna va vitrina uchun shaffof yopishqoq plyonka.",
        additionalPrice = 26_000
    )

    // T-Shirt
    val cotton100 = ProductMaterial(
        id = "cotton100",
        name = "Cotton",
        thickness = "100%",
        description = "Premium cotton",
        isDefault = true
    )

    val polyester = ProductMaterial(
        id = "polyester",
        name = "Polyester",
        thickness = "100%",
        description = "Sports fabric"
    )
    // Mug
    val ceramic = ProductMaterial(
        id = "ceramic",
        name = "Keramika",
        description = "Standart oq keramika krujka.",
        isDefault = true
    )
}