package uz.myprint.feature.feature.product.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.myprint.core.designsystem.theme.MyPrintColors

/**
 * Mahsulot sahifasidagi harakat kartasi.
 *
 * Ikki ko'rinishi bor va bu ataylab:
 *  - highlighted = true  — gradientli, asosiy taklif (AI Designer)
 *  - highlighted = false — oq, chegarali, ikkilamchi (Dizayn Studio)
 *
 * Ikkalasi bir xil ko'ringanda foydalanuvchi qaysi biri tavsiya
 * etilayotganini tushunmaydi.
 */
@Composable
fun ActionCard(

    icon: ImageVector,

    title: String,

    description: String,

    onClick: () -> Unit,

    modifier: Modifier = Modifier,

    /** Gradientli asosiy ko'rinish. */
    highlighted: Boolean = false,

    /** Sarlavha yonidagi kichik yorliq, masalan "1 daqiqada". */
    badge: String? = null

) {

    val shape = RoundedCornerShape(20.dp)

    val titleColor =
        if (highlighted) Color.White else MyPrintColors.TextPrimary

    val descriptionColor =
        if (highlighted) Color.White.copy(alpha = 0.80f)
        else MyPrintColors.TextSecondary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(shape)
            .then(
                if (highlighted) {
                    Modifier.background(
                        Brush.linearGradient(
                            listOf(
                                MyPrintColors.GradientStart,
                                MyPrintColors.GradientMiddle,
                                MyPrintColors.GradientEnd
                            )
                        )
                    )
                } else {
                    Modifier
                        .background(MyPrintColors.Surface)
                        .border(1.dp, MyPrintColors.Border, shape)
                }
            )
            .clickable { onClick() }
    ) {

        // Gradient tekis ko'rinmasligi uchun yumshoq yorug'lik dog'lari.
        if (highlighted) {

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-60).dp)
                    .size(150.dp)
                    .background(Color.White.copy(alpha = 0.10f), CircleShape)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-30).dp, y = 45.dp)
                    .size(90.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconBadge(
                icon = icon,
                highlighted = highlighted
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor
                    )

                    if (badge != null) {

                        Spacer(modifier = Modifier.width(8.dp))

                        Badge(
                            text = badge,
                            highlighted = highlighted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = descriptionColor
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Arrow(highlighted = highlighted)
        }
    }
}

@Composable
private fun IconBadge(
    icon: ImageVector,
    highlighted: Boolean
) {

    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (highlighted) Color.White.copy(alpha = 0.20f)
                else MyPrintColors.Primary.copy(alpha = 0.10f)
            ),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (highlighted) Color.White else MyPrintColors.Primary,
            modifier = Modifier.size(23.dp)
        )
    }
}

@Composable
private fun Badge(
    text: String,
    highlighted: Boolean
) {

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(
                if (highlighted) Color.White.copy(alpha = 0.22f)
                else MyPrintColors.Primary.copy(alpha = 0.10f)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {

        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlighted) Color.White else MyPrintColors.Primary
        )
    }
}

@Composable
private fun Arrow(highlighted: Boolean) {

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
                if (highlighted) Color.White.copy(alpha = 0.20f)
                else Color.Transparent
            ),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = null,
            tint = if (highlighted) Color.White else MyPrintColors.IconSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}