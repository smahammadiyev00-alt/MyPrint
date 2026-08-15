package uz.myprint.feature.feature.designer.data.dummy

import uz.myprint.R
import uz.myprint.feature.feature.designer.domain.model.PortfolioItem

object PortfolioDummyData {

    val portfolio = listOf(

        PortfolioItem(
            id = "1",
            title = "Premium Vizitka",
            previewImageRes = R.drawable.product_vizitka,
            category = "Vizitka",
            description = "Premium vizitka dizayni zamonaviy va minimal uslubda tayyorlangan.",
            likes = 1240,
            views = 8420,
            isFeatured = true,
            designer = DesignerDummyData.designers[0]
        ),

        PortfolioItem(
            id = "2",
            title = "Luxury Banner",
            previewImageRes = R.drawable.product_banner,
            category = "Banner",
            description = "Tashqi reklama va banner dizayni.",
            likes = 980,
            views = 6150,
            isFeatured = true,
            designer = DesignerDummyData.designers[0]
        ),

        PortfolioItem(
            id = "3",
            title = "Restaurant Menu",
            previewImageRes = R.drawable.product_flaer,
            category = "Menu",
            description = "Restoran va kafelar uchun premium menu dizayni.",
            likes = 760,
            views = 4910,
            isFeatured = false,
            designer = DesignerDummyData.designers[1]
        ),

        PortfolioItem(
            id = "4",
            title = "Creative Sticker",
            previewImageRes = R.drawable.product_sticker,
            category = "Sticker",
            description = "Brending va reklama uchun kreativ sticker dizayni.",
            likes = 690,
            views = 4580,
            isFeatured = false,
            designer = DesignerDummyData.designers[1]
        ),

        PortfolioItem(
            id = "5",
            title = "DTF Futbolka",
            previewImageRes = R.drawable.product_futbolka,
            category = "DTF",
            description = "DTF bosma uchun futbolka dizayni.",
            likes = 1450,
            views = 9870,
            isFeatured = true,
            designer = DesignerDummyData.designers[2]
        ),

        PortfolioItem(
            id = "6",
            title = "Premium Buklet",
            previewImageRes = R.drawable.product_buklet,
            category = "Buklet",
            description = "Kompaniya va mahsulotlar uchun buklet dizayni.",
            likes = 540,
            views = 3720,
            isFeatured = false,
            designer = DesignerDummyData.designers[2]
        ),

        PortfolioItem(
            id = "7",
            title = "Roll Up Banner",
            previewImageRes = R.drawable.product_rollup,
            category = "Roll Up",
            description = "Ko'rgazma va tadbirlar uchun Roll Up banner dizayni.",
            likes = 860,
            views = 6240,
            isFeatured = true,
            designer = DesignerDummyData.designers[3]
        ),

        PortfolioItem(
            id = "8",
            title = "Product Packaging",
            previewImageRes = R.drawable.product_packaging,
            category = "Packaging",
            description = "Mahsulot qadog'i uchun premium dizayn.",
            likes = 720,
            views = 5030,
            isFeatured = false,
            designer = DesignerDummyData.designers[3]
        )

    )
}