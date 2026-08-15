package uz.myprint.feature.feature.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.myprint.R
import uz.myprint.feature.feature.product.data.dummy.ProductDummyData
import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.domain.model.ProductCategory

private fun productImage(product: Product): Int =
    when (product.category) {
        ProductCategory.BUSINESS_CARD -> R.drawable.product_vizitka
        ProductCategory.BANNER -> R.drawable.product_banner
        ProductCategory.T_SHIRT -> R.drawable.product_futbolka
        ProductCategory.FLYER -> R.drawable.product_flaer
        ProductCategory.BOOKLET -> R.drawable.product_buklet
        ProductCategory.STICKER -> R.drawable.product_sticker
        ProductCategory.ROLL_UP -> R.drawable.product_rollup
        else -> R.drawable.product_packaging
    }

@Composable
fun PopularSection(
    onProductClick: (Product) -> Unit = {},
    onSeeAllClick: () -> Unit = {}
) {

    val products = ProductDummyData.products

    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Dizayner xizmati",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            TextButton(
                onClick = onSeeAllClick
            ) {
                Text("Barchasi")
            }

        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(products) { product ->

                ProductCard(
                    image = productImage(product),
                    title = product.name,
                    description = product.description,
                    onClick = {
                        onProductClick(product)
                    },
                    onAiClick = {},
                    onStudioClick = {},
                    onLocationClick = {}
                )

            }

        }

    }

}