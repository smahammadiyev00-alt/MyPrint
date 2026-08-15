package uz.myprint.feature.feature.product.data.dummy

import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.domain.model.ProductCategory

object ProductDummyData {

    val products = listOf(

        // ===========================
        // BUSINESS CARD
        // ===========================

        Product(
            id = "business_card",
            name = "Vizitka",
            description = "Premium sifatdagi vizitka bosib chiqarish.",
            category = ProductCategory.BUSINESS_CARD,

            thumbnail = "",
            gallery = emptyList(),

            materials = listOf(
                MaterialDummyData.art300,
                MaterialDummyData.art350,
                MaterialDummyData.kraft,
                MaterialDummyData.laminated
            ),

            printTypes = listOf(
                PrintTypeDummyData.singleSide,
                PrintTypeDummyData.doubleSide,
                PrintTypeDummyData.uvPrint,
                PrintTypeDummyData.laminate
            ),

            colorModes = listOf(
                ColorModeDummyData.cmyk
            ),

            sizes = listOf(
                SizeDummyData.businessCard90x50,
                SizeDummyData.businessCard85x55
            ),

            studioSupported = true,
            aiSupported = true,

            isAvailable = true,
            isPopular = true,
            isFeatured = true
        ),

        // ===========================
        // BANNER
        // ===========================

        Product(
            id = "banner",
            name = "Banner",
            description = "Ichki va tashqi reklama bannerlari.",

            category = ProductCategory.BANNER,

            thumbnail = "",
            gallery = emptyList(),

            materials = listOf(
                MaterialDummyData.banner440,
                MaterialDummyData.banner510,
                MaterialDummyData.blockout
            ),

            printTypes = listOf(
                PrintTypeDummyData.ecoSolvent,
                PrintTypeDummyData.largeUv
            ),

            colorModes = listOf(
                ColorModeDummyData.cmyk
            ),

            sizes = listOf(
                SizeDummyData.banner100x200,
                SizeDummyData.banner200x300
            ),

            studioSupported = true,
            aiSupported = true,

            isAvailable = true,
            isPopular = true,
            isFeatured = false
        ),

        // ===========================
        // T-SHIRT
        // ===========================

        Product(
            id = "tshirt",
            name = "Futbolka",

            description = "DTF, Sublimation va Silk Screen bosma.",

            category = ProductCategory.T_SHIRT,

            thumbnail = "",
            gallery = emptyList(),

            materials = listOf(
                MaterialDummyData.cotton100,
                MaterialDummyData.polyester
            ),

            printTypes = listOf(
                PrintTypeDummyData.dtf,
                PrintTypeDummyData.sublimation,
                PrintTypeDummyData.silkScreen
            ),

            colorModes = listOf(
                ColorModeDummyData.cmyk
            ),

            sizes = listOf(
                SizeDummyData.tshirtS,
                SizeDummyData.tshirtM,
                SizeDummyData.tshirtL,
                SizeDummyData.tshirtXL
            ),

            studioSupported = true,
            aiSupported = true,

            isAvailable = true,
            isPopular = true,
            isFeatured = false
        ),
Product(
id = "sticker",
name = "Sticker",
description = "Vinyl va qog'oz stikerlar.",

category = ProductCategory.STICKER,

thumbnail = "",
gallery = emptyList(),

materials = listOf(
MaterialDummyData.laminated,
MaterialDummyData.kraft
),

printTypes = listOf(
PrintTypeDummyData.singleSide,
PrintTypeDummyData.uvPrint
),

colorModes = listOf(
ColorModeDummyData.cmyk
),

sizes = listOf(
SizeDummyData.a6,
SizeDummyData.a5
),

studioSupported = true,
aiSupported = true,

isAvailable = true,
isPopular = true,
isFeatured = false
),
Product(
id = "flyer",
name = "Flyer",
description = "Reklama flyer va varaqalari.",

category = ProductCategory.FLYER,

thumbnail = "",
gallery = emptyList(),

materials = listOf(
MaterialDummyData.art300,
MaterialDummyData.art350
),

printTypes = listOf(
PrintTypeDummyData.singleSide,
PrintTypeDummyData.doubleSide
),

colorModes = listOf(
ColorModeDummyData.cmyk
),

sizes = listOf(
SizeDummyData.a6,
SizeDummyData.a5,
SizeDummyData.a4
),

studioSupported = true,
aiSupported = true,

isAvailable = true,
isPopular = true,
isFeatured = false
),
Product(
id = "booklet",
name = "Buklet",
description = "Ko'p sahifali reklama bukletlari.",

category = ProductCategory.BOOKLET,

thumbnail = "",
gallery = emptyList(),

materials = listOf(
MaterialDummyData.art300,
MaterialDummyData.laminated
),

printTypes = listOf(
PrintTypeDummyData.doubleSide,
PrintTypeDummyData.laminate
),

colorModes = listOf(
ColorModeDummyData.cmyk
),

sizes = listOf(
SizeDummyData.a5,
SizeDummyData.a4
),

studioSupported = true,
aiSupported = true,

isAvailable = true,
isPopular = false,
isFeatured = false
),
        Product(
            id = "rollup",
            name = "Roll Up",
            description = "Ko'rgazma va reklama uchun Roll Up banner.",

            category = ProductCategory.ROLL_UP,

            thumbnail = "",
            gallery = emptyList(),

            materials = listOf(
                MaterialDummyData.banner440,
                MaterialDummyData.blockout
            ),

            printTypes = listOf(
                PrintTypeDummyData.ecoSolvent,
                PrintTypeDummyData.largeUv
            ),

            colorModes = listOf(
                ColorModeDummyData.cmyk
            ),

            sizes = listOf(
                SizeDummyData.rollUp85x200
            ),

            studioSupported = true,
            aiSupported = true,

            isAvailable = true,
            isPopular = true,
            isFeatured = true
        ),
        Product(
            id = "mug",
            name = "Mug",
            description = "Keramika krujkaga sublimatsiya bosma.",

            category = ProductCategory.MUG,

            thumbnail = "",
            gallery = emptyList(),

            materials = listOf(
                MaterialDummyData.cotton100
            ),

            printTypes = listOf(
                PrintTypeDummyData.sublimation
            ),

            colorModes = listOf(
                ColorModeDummyData.cmyk
            ),

            sizes = listOf(
                SizeDummyData.mugStandard
            ),

            studioSupported = true,
            aiSupported = true,

            isAvailable = true,
            isPopular = true,
            isFeatured = false
        )
    )
}