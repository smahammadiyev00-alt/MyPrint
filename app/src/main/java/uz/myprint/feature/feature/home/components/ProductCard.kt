package uz.myprint.feature.feature.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.R
import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.domain.model.ProductCategory
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.foundation.clickable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.scale
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.animation.core.spring

private fun Product.imageRes(): Int {
    return when (category) {
        ProductCategory.BUSINESS_CARD -> R.drawable.product_vizitka
        ProductCategory.BANNER -> R.drawable.product_banner
        ProductCategory.T_SHIRT -> R.drawable.product_futbolka
        ProductCategory.STICKER -> R.drawable.product_sticker
        ProductCategory.FLYER -> R.drawable.product_flaer
        ProductCategory.BOOKLET -> R.drawable.product_buklet
        ProductCategory.ROLL_UP -> R.drawable.product_rollup
        else -> R.drawable.product_packaging
    }
}

@Composable
fun ProductCard(

    image: Int,

    title: String,

    description: String,

    modifier: Modifier = Modifier,

    onClick: () -> Unit = {},

    onFavoriteClick: () -> Unit = {},

    onAiClick: () -> Unit = {},

    onStudioClick: () -> Unit = {},

    onLocationClick: () -> Unit = {}

)

{
    var pressed by remember { mutableStateOf(false) }
    var favorite by remember { mutableStateOf(false) }

    val heartScale by animateFloatAsState(
        targetValue = if (favorite) 1.25f else 1f,
        animationSpec = spring(
            dampingRatio = 0.45f,
            stiffness = 450f
        ),
        label = "HeartScale"
    )
    val scope = rememberCoroutineScope()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(120),
        label = "ProductCardScale"

    )
    Card(
        modifier = modifier
            .width(245.dp)
            .height(340.dp)
            .scale(scale),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        onClick = {
            scope.launch {
                pressed = true
                delay(120.milliseconds)
                pressed = false
                onClick()
            }
        }
    ) {

        Box {

            Image(
                painter = painterResource(image),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp
                        )
                    ),
                contentScale = ContentScale.Crop
            )

            Surface(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.TopEnd),
                shape = CircleShape,
                tonalElevation = 2.dp,
                shadowElevation = 2.dp
            ) {

                IconButton(
                    onClick = {
                        favorite = !favorite
                        onFavoriteClick()
                    }
                ) {

                    Icon(
                        imageVector = if (favorite)
                            Icons.Filled.Favorite
                        else
                            Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.scale(heartScale),
                        tint = if (favorite) Color.Red else Color.Black
                    )
                }

            }

        }

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MyPrintColors.TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Surface(
                    onClick = onAiClick,
                    shape = RoundedCornerShape(50.dp),
                    color = Color(0xFFF5F5F5)
                ) {

                    Row(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MyPrintColors.Primary
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = "AI",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )

                    }

                }

                Surface(
                    onClick = onStudioClick,
                    shape = RoundedCornerShape(50.dp),
                    color = Color(0xFFF5F5F5)
                ) {

                    Row(
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 7.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.Store,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MyPrintColors.Primary
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = "Studio",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )

                    }

                }

                Row(
                    modifier = Modifier.clickable(
                        onClick = onLocationClick
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MyPrintColors.TextSecondary
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = "Yaqin",
                        style = MaterialTheme.typography.labelLarge,
                        color = MyPrintColors.TextSecondary
                    )

                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "So'rov bo'yicha",
                    modifier = Modifier.weight(1f),
                    color = MyPrintColors.Primary,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    shape = CircleShape,
                    color = MyPrintColors.Primary,
                    shadowElevation = 6.dp,
                    tonalElevation = 2.dp
                ) {

                    IconButton(
                        onClick = onClick
                    ) {

                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )


                    }

                }

            }

        }

    }

}