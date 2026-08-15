package uz.myprint.feature.feature.promotion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PromotionIndicator(
    total: Int,
    selected: Int
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        repeat(total) { index ->

            Box(
                modifier = Modifier
                    .width(if (index == selected) 18.dp else 8.dp)
                    .height(8.dp)
                    .background(
                        color = if (index == selected)
                            Color(0xFF6C63FF)
                        else
                            Color(0xFFD6D6D6),
                        shape = CircleShape
                    )
            )

            if (index != total - 1) {
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.width(6.dp)
                )
            }
        }
    }
}