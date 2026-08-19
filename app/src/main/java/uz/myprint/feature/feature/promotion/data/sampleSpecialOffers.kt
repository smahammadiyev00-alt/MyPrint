package uz.myprint.feature.feature.promotion.data

import uz.myprint.R
import uz.myprint.feature.feature.promotion.model.SpecialOffer

val sampleSpecialOffers = listOf(

    SpecialOffer(
        id = "1",
        title = "Vizitka",
        discount = "20% chegirma",
        description = "Premium vizitkalarga maxsus chegirma",
        imageRes = R.drawable.product_vizitka
    ),

    SpecialOffer(
        id = "2",
        title = "Banner",
        discount = "15% chegirma",
        description = "Barcha bannerlarga maxsus taklif",
        imageRes = R.drawable.product_banner
    ),

    SpecialOffer(
        id = "3",
        title = "Futbolka",
        discount = "20% chegirma",
        description = "DTF futbolkalarga maxsus chegirma",
        imageRes = R.drawable.product_futbolka
    )

)