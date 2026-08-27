package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.feature.feature.design.studio.data.DesignFonts
import uz.myprint.feature.feature.design.studio.domain.DesignFont

/**
 * SHRIFT TANLASH.
 *
 * Har bir shrift O'Z shriftida ko'rsatiladi — ro'yxatdagi nomni
 * o'qib shriftni tasavvur qilib bo'lmaydi, uni ko'rish kerak.
 *
 * Namuna matni ataylab "Aa Аа": lotin va kirill yonma-yon.
 * Foydalanuvchi shriftning kirilli borligini darhol ko'radi va
 * bosmadan keyin emas, tanlash paytida biladi.
 */
@Composable
fun FontPanel(
    selected: DesignFont,
    onPick: (DesignFont) -> Unit,

    /**
     * Hozirgi matn. Kirill bo'lsa va shrift uni qo'llab-
     * quvvatlamasa, ogohlantirish chiqadi.
     */
    currentText: String = ""
) {

    val needsCyrillic = DesignFont.hasCyrillic(currentText)

    Column {

        if (needsCyrillic && !selected.supportsCyrillic) {

            CyrillicWarning(selected)

            Spacer(modifier = Modifier.height(10.dp))
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            items(DesignFont.entries) { font ->

                FontTile(
                    font = font,
                    isSelected = font == selected,

                    // Kirill matn terilganda mos kelmaydigan
                    // shriftlar xiralashadi — taqiqlanmaydi,
                    // chunki foydalanuvchi keyin matnni
                    // lotinga o'zgartirishi mumkin.
                    isDimmed = needsCyrillic && !font.supportsCyrillic,
                    onClick = { onPick(font) }
                )
            }
        }
    }
}

@Composable
private fun FontTile(
    font: DesignFont,
    isSelected: Boolean,
    isDimmed: Boolean,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) MyPrintColors.Primary
                else MyPrintColors.Surface
            )
            .border(
                width = 1.dp,
                color = if (isSelected) MyPrintColors.Primary
                else MyPrintColors.Border,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val contentColor = when {
            isSelected -> Color.White
            isDimmed -> MyPrintColors.IconSecondary
            else -> MyPrintColors.TextPrimary
        }

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = "Aa",
                fontFamily = DesignFonts.family(font),
                fontSize = 19.sp,
                color = contentColor
            )

            Spacer(modifier = Modifier.width(5.dp))

            // Kirill namunasi faqat shrift uni qo'llab-
            // quvvatlaganda ko'rsatiladi. Aks holda tizim boshqa
            // shriftga sakraydi va namuna yolg'on chiqadi.
            if (font.supportsCyrillic) {

                Text(
                    text = "Аа",
                    fontFamily = DesignFonts.family(font),
                    fontSize = 19.sp,
                    color = contentColor
                )

            } else {

                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            if (isSelected) Color.White.copy(alpha = 0.25f)
                            else Color(0xFFFEF3C7)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White
                        else Color(0xFF92400E)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = font.label,
            fontSize = 10.sp,
            color = if (isSelected) Color.White.copy(alpha = 0.85f)
            else MyPrintColors.TextSecondary,
            maxLines = 1
        )
    }
}

@Composable
private fun CyrillicWarning(font: DesignFont) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFEF3C7))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Rounded.WarningAmber,
            contentDescription = null,
            tint = Color(0xFF92400E),
            modifier = Modifier.size(15.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "\"${font.label}\" shriftida kirill harflari yo'q — " +
                    "bosmada boshqa shrift bilan almashadi",
            fontSize = 11.sp,
            color = Color(0xFF92400E)
        )
    }
}
