package uz.myprint.feature.feature.promotion.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun PromotionCover(
    @DrawableRes imageRes: Int,
    modifier: Modifier = Modifier
) {

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp)),
        contentScale = ContentScale.Crop
    )

}