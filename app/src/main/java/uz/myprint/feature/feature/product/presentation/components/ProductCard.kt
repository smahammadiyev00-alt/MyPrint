package uz.myprint.feature.feature.product.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material.icons.rounded.ViewDay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.domain.model.ProductCategory
import uz.myprint.core.designsystem.theme.MyPrintColors

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {}
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MyPrintColors.Surface)
            .border(
                width = 1.dp,
                color = MyPrintColors.Border,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
    ) {

        // Rasm maydoni. Coil ulangach bu Box AsyncImage bilan almashadi,
        // aspectRatio o'zgarmaydi — shuning uchun tarmoq siljimaydi.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.15f)
                .background(MyPrintColors.Background)
        ) {

            Icon(
                imageVector = _root_ide_package_.uz.myprint.feature.feature.product.presentation.components.iconFor(
                    product.category
                ),
                contentDescription = null,
                tint = MyPrintColors.Primary,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(44.dp)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(30.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(MyPrintColors.Surface)
                    .clickable { onFavoriteClick() },
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Favorite
                    else Icons.Rounded.FavoriteBorder,
                    contentDescription = "Sevimlilar",
                    tint = if (isFavorite) MyPrintColors.Notification
                    else MyPrintColors.IconSecondary,
                    modifier = Modifier.size(17.dp)
                )
            }

            if (product.aiSupported) {

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MyPrintColors.Surface)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = MyPrintColors.Primary,
                        modifier = Modifier.size(12.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "AI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyPrintColors.Primary
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Balandligi qat'iy: ikki qator joy doim band bo'ladi,
            // shuning uchun yonma-yon kartochkalar bir tekisda turadi.
            Text(
                text = product.description,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MyPrintColors.TextSecondary,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun iconFor(category: ProductCategory): ImageVector =
    when (category) {

        ProductCategory.BUSINESS_CARD -> Icons.Rounded.CreditCard

        ProductCategory.BANNER,
        ProductCategory.ROLL_UP -> Icons.Rounded.Flag

        ProductCategory.T_SHIRT -> Icons.Rounded.Style

        ProductCategory.MUG -> Icons.Rounded.LocalCafe

        ProductCategory.BOOKLET,
        ProductCategory.CALENDAR -> Icons.AutoMirrored.Rounded.MenuBook

        ProductCategory.STICKER,
        ProductCategory.LABEL -> Icons.Rounded.ViewDay

        else -> Icons.Rounded.Description
    }