package uz.myprint.feature.feature.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.sp
import uz.myprint.R

@Composable
fun AiBanner(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(28.dp))
            .clickable { onClick() },

        shadowElevation = 10.dp,
        color = Color.Transparent
    ) {

        Box(
            modifier = Modifier.background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFF7F3FF),
                        Color(0xFFEAE9FF),
                        Color(0xFFD8D5FF)
                    )
                )
            )
        ) {

            Row(
                modifier = Modifier.fillMaxSize()
            ) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            start = 20.dp,
                            top = 18.dp,
                            end = 20.dp,
                            bottom = 18.dp
                        ),

                    verticalArrangement = Arrangement.Center
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF6C4CF7),
                            modifier = Modifier.size(15.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "AI DESIGNER",
                            color = Color(0xFF6C4CF7),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                    }

                    Spacer(modifier = Modifier.height(0.dp))

                    Text(
                        text =
                            "Professional dizaynlarni\nAI bilan yarating",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 25.sp,
                        color = Color(0xFF1A1A1A)
                    )

                    Spacer(modifier = Modifier.height(0.dp))

                    Text(
                        text = "Logotip, banner, vizitka va boshqa\nmahsulotlarni bir necha soniyada tayyorlang.",
                        fontSize = 11.sp,
                        lineHeight = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onClick,

                        shape = RoundedCornerShape(100.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6C4CF7)

                        )
                    ) {

                        Text(
                            text = "Boshlash"
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Icon(
                            imageVector = Icons.Outlined.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                    }

                }

                Image(
                    painter = painterResource(R.drawable.ai_banner_art),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(175.dp)
                        .padding(
                            top = 8.dp,
                            end = 8.dp
                        ),
                    contentScale = ContentScale.Fit
                )

            }

        }

    }

}