package uz.myprint.feature.feature.promotion.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uz.myprint.feature.feature.promotion.model.Partner

@Composable
fun PromotionCard(
    partner: Partner,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .width(250.dp)
            .height(150.dp),

        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        onClick = onClick
    ) {
        PromotionCover(
            imageRes = partner.coverImage,
            modifier = Modifier.fillMaxSize()
        )
    }
}