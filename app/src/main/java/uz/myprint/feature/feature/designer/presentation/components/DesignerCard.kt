package uz.myprint.feature.feature.designer.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uz.myprint.feature.feature.designer.domain.model.PortfolioItem
import androidx.compose.material.icons.filled.Verified

@Composable
fun DesignerCard(
    portfolio: PortfolioItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    Card(
        modifier = modifier
            .width(240.dp)
            .height(390.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(26.dp),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {

        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {

                Image(
                    painter = painterResource(portfolio.previewImageRes),
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
                                    Color(0x1A4A1DB6),
                                    Color(0x66402080)
                                )
                            )
                        )
                )

                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
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

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "TOP",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                    }

                }
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = .95f)
                ) {

                    IconButton(
                        onClick = { }
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = Color(0xFFE53935)
                        )

                    }

                }

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF6B3DFF),   // Hero bilan yumshoq o'tish
                                Color(0xFF5425D8),
                                Color(0xFF3B147F),
                                Color(0xFF22053F)    // Chuqur premium binafsha
                            )
                        )
                    )
                    .padding(16.dp)
            ) {

                Text(
                    text = portfolio.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(portfolio.designer.avatarRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = portfolio.designer.name,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )

                            if (portfolio.designer.verified) {

                                Spacer(modifier = Modifier.width(4.dp))

                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Color(0xFF58D4FF),
                                    modifier = Modifier.size(16.dp)
                                )

                            }

                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = portfolio.designer.specialties.joinToString(" • "),
                            color = Color.White.copy(alpha = .80f),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                    }

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = .15f)
                    ) {

                        Row(
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "⭐",
                                color = Color(0xFFFFD54F)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = String.format("%.1f", portfolio.designer.rating),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                        }

                    }

                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.15f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    StatItem(
                        emoji = "❤",
                        value = formatCount(portfolio.likes),
                        label = "Likes"
                    )

                    VerticalDivider(
                        modifier = Modifier.height(24.dp),
                        color = Color.White.copy(alpha = 0.20f)
                    )

                    StatItem(
                        emoji = "👁",
                        value = formatCount(portfolio.views),
                        label = "Views"
                    )

                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF4B4DFF)
                    )
                ) {

                    Text(
                        text = "Batafsil  →",
                        fontWeight = FontWeight.Bold
                    )

                }

            }

        }

    }

}

@Composable
private fun StatItem(
    emoji: String,
    value: String,
    label: String
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(emoji)

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = Color.White.copy(alpha = .75f),
            style = MaterialTheme.typography.labelSmall
        )

    }

}

private fun formatCount(value: Int): String {

    return when {

        value >= 1_000_000 ->
            String.format("%.1fM", value / 1_000_000f)

        value >= 1_000 ->
            String.format("%.1fK", value / 1_000f)

        else ->
            value.toString()

    }

}