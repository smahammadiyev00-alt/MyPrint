package uz.myprint.feature.feature.product.data.dummy

import uz.myprint.feature.feature.product.domain.model.ProductMaterial

object MaterialDummyData {

    // Vizitka qog'ozlari
    val glossy160 = ProductMaterial(
        id = "glossy_160",
        name = "Glyansli",
        thickness = "160g",
        additionalPrice = 0
    )

    val glossy200 = ProductMaterial(
        id = "glossy_200",
        name = "Glyansli",
        thickness = "200g",
        additionalPrice = 100
    )

    val glossy250 = ProductMaterial(
        id = "glossy_250",
        name = "Glyansli",
        thickness = "250g",
        additionalPrice = 200
    )

    val glossy300 = ProductMaterial(
        id = "glossy_300",
        name = "Glyansli",
        thickness = "300g",
        description = "Zich va qattiq, eng ko'p tanlanadi.",
        additionalPrice = 350,
        isDefault = true
    )

    val matte160 = ProductMaterial(
        id = "matte_160",
        name = "Matoviy",
        thickness = "160g",
        additionalPrice = 0
    )

    val matte200 = ProductMaterial(
        id = "matte_200",
        name = "Matoviy",
        thickness = "200g",
        additionalPrice = 100
    )

    val matte250 = ProductMaterial(
        id = "matte_250",
        name = "Matoviy",
        thickness = "250g",
        additionalPrice = 200
    )

    val matte300 = ProductMaterial(
        id = "matte_300",
        name = "Matoviy",
        thickness = "300g",
        additionalPrice = 350
    )

    // Soft Touch — UV lak faqat shu qog'ozda ishlaydi.
    val softTouch300 = ProductMaterial(
        id = "soft_touch_300",
        name = "Soft Touch",
        thickness = "300g",
        description = "Baxmal his beruvchi sirt, UV lak uchun asos.",
        additionalPrice = 700
    )

    val softTouch350 = ProductMaterial(
        id = "soft_touch_350",
        name = "Soft Touch",
        thickness = "350g",
        description = "Qalinroq Soft Touch, premium vizitkalar uchun.",
        additionalPrice = 900
    )

    /** UV lakka ruxsat etilgan qog'ozlar. */
    val softTouchIds = setOf(softTouch300.id, softTouch350.id)

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