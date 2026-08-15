package uz.myprint.feature.feature.home.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.myprint.R
import uz.myprint.feature.feature.product.domain.model.ProductCategory

data class CategoryItem(
    val title: String,

    @DrawableRes
    val iconRes: Int,

    val category: ProductCategory
)

@Composable
fun CategorySection(
    onCategoryClick: (CategoryItem) -> Unit = {}
) {

    val categories = listOf(

        CategoryItem(
            title = "Vizitka",
            iconRes = R.drawable.iconka_vizitka,
            category = ProductCategory.BUSINESS_CARD
        ),

        CategoryItem(
            title = "Banner",
            iconRes = R.drawable.iconca_baner,
            category = ProductCategory.BANNER
        ),

        CategoryItem(
            title = "Futbolka",
            iconRes = R.drawable.iconca_futbolka,
            category = ProductCategory.T_SHIRT
        ),

        CategoryItem(
            title = "Sticker",
            iconRes = R.drawable.iconca_sticker,
            category = ProductCategory.STICKER
        ),

        CategoryItem(
            title = "Buklet",
            iconRes = R.drawable.iconca_buklet,
            category = ProductCategory.BOOKLET
        ),

        CategoryItem(
            title = "Mug",
            iconRes = R.drawable.iconca_bakal,
            category = ProductCategory.MUG
        ),

        CategoryItem(
            title = "Roll Up",
            iconRes = R.drawable.iconca_rollup,
            category = ProductCategory.ROLL_UP
        ),

        CategoryItem(
            title = "Barchasi",
            iconRes = R.drawable.iconca_barchasi,
            category = ProductCategory.ALL
        )

    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Kategoriyalar",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {

            items(
                items = categories,
                key = { it.category }
            ) { item ->

                CategoryCard(
                    title = item.title,
                    iconRes = item.iconRes,
                    onClick = {
                        onCategoryClick(item)
                    }
                )

            }

        }

    }

}