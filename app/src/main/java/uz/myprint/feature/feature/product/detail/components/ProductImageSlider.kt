package uz.myprint.feature.feature.product.detail.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import uz.myprint.R
import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.domain.model.ProductCategory

private fun Product.imageRes(): Int {
    return when (category) {
        ProductCategory.ALL -> R.drawable.product_packaging
        ProductCategory.BUSINESS_CARD -> R.drawable.product_vizitka
        ProductCategory.BANNER -> R.drawable.product_banner
        ProductCategory.MUG -> R.drawable.product_packaging
        ProductCategory.T_SHIRT -> R.drawable.product_futbolka
        ProductCategory.STICKER -> R.drawable.product_sticker
        ProductCategory.FLYER -> R.drawable.product_flaer
        ProductCategory.BOOKLET -> R.drawable.product_buklet
        ProductCategory.ROLL_UP -> R.drawable.product_rollup
        ProductCategory.X_BANNER -> R.drawable.product_rollup
        ProductCategory.CALENDAR -> R.drawable.product_packaging
        ProductCategory.PACKAGING -> R.drawable.product_packaging
        ProductCategory.LABEL -> R.drawable.product_packaging
        ProductCategory.OTHER -> R.drawable.product_packaging
    }
}

@Composable
fun ProductImageSlider(

    product: Product,

    onBackClick: () -> Unit = {},

    onFavoriteClick: () -> Unit = {},

    onMoreClick: () -> Unit = {}

) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(330.dp)
    ) {

        Image(
            painter = painterResource(product.imageRes()),
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    RoundedCornerShape(
                        bottomStart = 32.dp,
                        bottomEnd = 32.dp
                    )
                ),
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 18.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.95f)
            ) {

                IconButton(
                    onClick = onBackClick
                ) {

                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )

                }

            }

            Row {

                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.95f)
                ) {

                    IconButton(
                        onClick = onFavoriteClick
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite"
                        )

                    }

                }

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.95f)
                ) {

                    IconButton(
                        onClick = onMoreClick
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "More"
                        )

                    }

                }

            }

        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            repeat(4) { index ->

                Box(
                    modifier = Modifier
                        .size(if (index == 0) 10.dp else 8.dp)
                        .background(
                            color = if (index == 0)
                                Color.White
                            else
                                Color.White.copy(alpha = 0.45f),
                            shape = CircleShape
                        )
                )

            }

        }

    }

}