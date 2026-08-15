package uz.myprint.feature.feature.home.components

import androidx.compose.runtime.Composable
import uz.myprint.R
import uz.myprint.feature.feature.project.ProjectSection
import uz.myprint.feature.feature.project.model.Project
import uz.myprint.feature.feature.project.model.ProjectCategory

@Composable
fun HomeProjectSection(
    onProjectClick: (Project) -> Unit = {},
    onMenuClick: (Project) -> Unit = {},
    onSeeAllClick: () -> Unit = {}
) {

    val projects = listOf(

        Project(
            id = "1",
            title = "Premium vizitka",
            category = ProjectCategory.VIZITKA,
            imageRes = R.drawable.product_vizitka,
            updatedAt = "2 soat oldin tahrirlangan"
        ),

        Project(
            id = "2",
            title = "Eco banner design",
            category = ProjectCategory.BANNER,
            imageRes = R.drawable.product_banner,
            updatedAt = "4 soat oldin tahrirlangan"
        ),

        Project(
            id = "3",
            title = "Bakal design",
            category = ProjectCategory.MUG,
            imageRes = R.drawable.product_bakal,
            updatedAt = "1 kun oldin tahrirlangan"
        )
    )

    ProjectSection(
        projects = projects,
        onProjectClick = onProjectClick,
        onMenuClick = onMenuClick,
        onSeeAllClick = onSeeAllClick
    )
}