package uz.myprint.feature.feature.home.components

import androidx.compose.runtime.Composable
import uz.myprint.feature.feature.product.domain.model.Product

@Composable
fun HomePopularSection(
    onProductClick: (Product) -> Unit
) {
    PopularSection(
        onProductClick = onProductClick
    )
}