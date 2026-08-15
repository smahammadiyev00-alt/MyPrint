package uz.myprint.feature.feature.designer.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.myprint.feature.feature.designer.domain.model.PortfolioItem

@Composable
fun DesignerHero(
    portfolio: PortfolioItem
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 26.dp,
                    topEnd = 26.dp
                )
            )
    ) {

        Image(
            painter = painterResource(id = portfolio.previewImageRes),
            contentDescription = portfolio.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color(0x660B63FF),
                            Color(0xCC3B0E7A)
                        )
                    )
                )
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            shape = RoundedCornerShape(50),
            color = Color(0xFFFF9800)
        ) {

            Row(
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 5.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )

                Text(
                    text = " TOP",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

            }

        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = .95f)
        ) {

            IconButton(
                onClick = {}
            ) {

                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = Color(0xFFE53935)
                )

            }

        }

    }

}