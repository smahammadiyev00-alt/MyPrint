package uz.myprint.feature.feature.home.components

import androidx.compose.runtime.Composable

@Composable
fun HomeCategorySection(
    onCategoryClick: (CategoryItem) -> Unit
) {
    CategorySection(
        onCategoryClick = onCategoryClick
    )
}