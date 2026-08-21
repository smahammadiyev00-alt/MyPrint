package uz.myprint.feature.feature.product.data.dummy

import uz.myprint.feature.feature.product.domain.model.ProductSize
import uz.myprint.feature.feature.product.domain.model.SizeUnit

object SizeDummyData {

    // ==========================================================
    // BUSINESS CARD
    // ==========================================================

    val businessCard90x50 = ProductSize(
        id = "business_card_90x50",
        title = "90 × 50 mm",
        width = 90f,
        height = 50f,
        unit = SizeUnit.MM,
        isDefault = true
    )

    val businessCard85x55 = ProductSize(
        id = "business_card_85x55",
        title = "85 × 55 mm",
        width = 85f,
        height = 55f,
        unit = SizeUnit.MM
    )

    // ==========================================================
    // STICKER / FLYER / BOOKLET
    // ==========================================================

    val a6 = ProductSize(
        id = "a6",
        title = "A6",
        width = 105f,
        height = 148f,
        unit = SizeUnit.MM
    )

    val a5 = ProductSize(
        id = "a5",
        title = "A5",
        width = 148f,
        height = 210f,
        unit = SizeUnit.MM,
        isDefault = true
    )

    val a4 = ProductSize(
        id = "a4",
        title = "A4",
        width = 210f,
        height = 297f,
        unit = SizeUnit.MM
    )

    // ==========================================================
    // BANNER
    // ==========================================================

    val banner100x200 = ProductSize(
        id = "banner_100x200",
        title = "100 × 200 cm",
        width = 100f,
        height = 200f,
        unit = SizeUnit.CM,
        isDefault = true
    )

    val banner200x300 = ProductSize(
        id = "banner_200x300",
        title = "200 × 300 cm",
        width = 200f,
        height = 300f,
        unit = SizeUnit.CM
    )

    // ==========================================================
    // ROLL UP
    // ==========================================================


    val rollUp85x200 = ProductSize(
        id = "rollup_85x200",
        title = "85 × 200 cm",
        width = 85f,
        height = 200f,
        unit = SizeUnit.CM,
        isDefault = true
    )

    // ==========================================================
    // T-SHIRT
    // (haqiqiy futbolka o'lchamlariga yaqin qiymatlar)
    // ==========================================================

    val tshirtS = ProductSize(
        id = "tshirt_s",
        title = "S",
        width = 480f,
        height = 680f,
        unit = SizeUnit.MM
    )

    val tshirtM = ProductSize(
        id = "tshirt_m",
        title = "M",
        width = 510f,
        height = 710f,
        unit = SizeUnit.MM,
        isDefault = true
    )

    val tshirtL = ProductSize(
        id = "tshirt_l",
        title = "L",
        width = 540f,
        height = 740f,
        unit = SizeUnit.MM
    )

    val tshirtXL = ProductSize(
        id = "tshirt_xl",
        title = "XL",
        width = 570f,
        height = 770f,
        unit = SizeUnit.MM
    )

    // ==========================================================
    // MUG
    // ==========================================================

    val mugStandard = ProductSize(
        id = "mug_standard",
        title = "330 ml",
        width = 82f,
        height = 95f,
        unit = SizeUnit.MM,
        isDefault = true
    )
    val xBanner60x160 = ProductSize(
        id = "xbanner_60x160",
        title = "60 × 160 cm",
        width = 60f,
        height = 160f,
        unit = SizeUnit.CM,
        isDefault = true
    )

    val xBanner80x180 = ProductSize(
        id = "xbanner_80x180",
        title = "80 × 180 cm",
        width = 80f,
        height = 180f,
        unit = SizeUnit.CM
    )
}