package uz.myprint.feature.feature.home.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.animation.animateColorAsState

@Composable
fun CategoryCard(

    title: String,

    @DrawableRes
    iconRes: Int,

    onClick: () -> Unit = {}

) {

    var pressed by remember {
        mutableStateOf(false)
    }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        label = ""
    )

    val cardColor by animateColorAsState(
        targetValue = if (pressed)
            Color(0xFFF0E8FF)
        else
            Color(0xFFF7F3FF),
        label = ""
    )

    Card(
        onClick = {
            pressed = true
            onClick()
            pressed = false
        },
        modifier = Modifier
            .width(96.dp)
            .height(122.dp)
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFFE6DAFF)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White,
                                Color(0xFFF3ECFF)
                            )
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(56.dp),
                    contentScale = ContentScale.Fit
                )

            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF232323),
                textAlign = TextAlign.Center,
                maxLines = 1
            )

        }

    }

}