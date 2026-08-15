package uz.myprint.core.designsystem.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AiBanner(
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit = {}
) {

    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF6C63FF),
            Color(0xFF8B5CF6),
            Color(0xFFB721FF)
        )
    )

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 8.dp
    ) {

        Column(
            modifier = Modifier
                .background(gradient)
                .padding(24.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "AI Designer",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Vizitka, banner yoki logo haqida yozing.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.95f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onStartClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF6C63FF)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {

                Text(
                    text = "AI bilan boshlash"
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    contentDescription = null
                )
            }
        }
    }
}