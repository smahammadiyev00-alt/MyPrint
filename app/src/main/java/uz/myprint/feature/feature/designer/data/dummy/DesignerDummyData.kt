package uz.myprint.feature.feature.designer.data.dummy

import uz.myprint.R
import uz.myprint.feature.feature.designer.domain.model.Designer

object DesignerDummyData {

    val designers = listOf(

        Designer(
            id = "1",
            name = "Sardor Design",
            avatarRes = R.drawable.myprint,
            specialties = listOf(
                "Logo",
                "Branding",
                "Print"
            ),
            rating = 4.9,
            reviewCount = 248,
            completedProjects = 120,
            verified = true
        ),

        Designer(
            id = "2",
            name = "Madina Creative",
            avatarRes = R.drawable.myprint_wordmark,
            specialties = listOf(
                "Banner",
                "Packaging",
                "Print"
            ),
            rating = 4.8,
            reviewCount = 196,
            completedProjects = 94,
            verified = true
        ),

        Designer(
            id = "3",
            name = "Pixel Lab",
            avatarRes = R.drawable.myprint,
            specialties = listOf(
                "DTF",
                "Banner",
                "Branding"
            ),
            rating = 5.0,
            reviewCount = 321,
            completedProjects = 210,
            verified = true
        ),

        Designer(
            id = "4",
            name = "Art Studio",
            avatarRes = R.drawable.myprint,
            specialties = listOf(
                "Outdoor",
                "Roll Up",
                "Branding"
            ),
            rating = 4.7,
            reviewCount = 174,
            completedProjects = 86,
            verified = false
        )

    )

}