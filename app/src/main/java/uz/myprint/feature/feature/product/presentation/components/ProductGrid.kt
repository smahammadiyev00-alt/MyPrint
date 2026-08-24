package uz.myprint.feature.feature.product.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.feature.feature.product.domain.model.Product

@Composable
fun ProductGrid(
    products: List<Product>,
    modifier: Modifier = Modifier,
    onProductClick: (Product) -> Unit = {}
) {

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .background(MyPrintColors.Background),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        if (products.isEmpty()) {

            item(span = { GridItemSpan(maxLineSpan) }) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Mahsulot topilmadi.",
                        color = MyPrintColors.TextSecondary
                    )
                }
            }

        } else {

            items(products, key = { it.id }) { product ->

                _root_ide_package_.uz.myprint.feature.feature.product.presentation.components.ProductCard(
                    product = product,
                    onClick = { onProductClick(product) }
                )
            }
        }
    }
}