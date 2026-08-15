package uz.myprint.feature.feature.product.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.myprint.feature.feature.product.domain.model.Product

@Composable
fun ProductGrid(
    products: List<Product>,
    modifier: Modifier = Modifier,
    onProductClick: (Product) -> Unit = {}
) {

    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        items(products) { product ->

            ProductCard(
                product = product,
                onClick = {
                    onProductClick(product)
                }
            )

        }

    }

}