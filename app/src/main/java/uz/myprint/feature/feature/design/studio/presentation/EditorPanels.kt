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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import uz.myprint.feature.feature.design.studio.domain.DesignFont
import kotlin.math.roundToInt

/**
 * Poligrafiya uchun tanlangan palitra.
 *
 * Ochiq ranglar tanlagichi (HSV doirasi) qo'yilmadi: mijoz u yerdan
 * bosmada takrorlab bo'lmaydigan neon ranglarni tanlaydi va natijadan
 * norozi bo'ladi. Tayyor ro'yxat ham tezroq, ham xavfsizroq.
 */
val DesignPalette: List<Color> = listOf(
    Color(0xFF000000),
    Color(0xFF3A3A3A),
    Color(0xFF7A7A7A),
    Color(0xFFBDBDBD),
    Color(0xFFFFFFFF),
    Color(0xFF7B4DFF),
    Color(0xFF4C31C7),
    Color(0xFF2563EB),
    Color(0xFF0EA5E9),
    Color(0xFF10B981),
    Color(0xFF16A34A),
    Color(0xFFEAB308),
    Color(0xFFF59E0B),
    Color(0xFFEF4444),
    Color(0xFFBE123C),
    Color(0xFFEC4899),
    Color(0xFF9A3412),
    Color(0xFF78350F),
    Color(0xFFC9A227),
    Color(0xFF1E293B)
)

@Composable
fun PanelSurface(
    title: String,
    content: @Composable () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MyPrintColors.Background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MyPrintColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        content()
    }
}

@Composable
fun ColorPanel(
    selected: Color?,
    onPick: (Color) -> Unit
) {

    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

        items(DesignPalette) { color ->

            val isSelected = selected != null &&
                    color.value == selected.value

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) MyPrintColors.Primary
                        else MyPrintColors.Border,
                        shape = CircleShape
                    )
                    .clickable { onPick(color) }
            )
        }
    }
}

@Composable
fun FontPanel(
    selected: DesignFont,
    onPick: (DesignFont) -> Unit
) {

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        items(DesignFont.entries) { font ->

            val isSelected = font == selected

            Box(
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
                    .clickable { onPick(font) }
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            ) {

                Text(
                    text = font.label,
                    fontFamily = font.family,
                    fontSize = 14.sp,
                    color = if (isSelected) Color.White
                    else MyPrintColors.TextPrimary
                )
            }
        }
    }
}

/**
 * Sonli qiymat uchun slayder.
 *
 * Qiymat yonida raqam bilan ko'rsatiladi — mijoz "5 mm" ni ko'rsa,
 * bosmada nima chiqishini tasavvur qila oladi. Faqat slayder
 * bo'lganda bu ma'lumot yo'qoladi.
 */
@Composable
fun ValueSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    format: (Float) -> String,
    onChange: (Float) -> Unit
) {

    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = label,
                fontSize = 13.sp,
                color = MyPrintColors.TextSecondary
            )

            Text(
                text = format(value),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary
            )
        }

        Slider(
            value = value.coerceIn(range.start, range.endInclusive),
            onValueChange = onChange,
            valueRange = range,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = MyPrintColors.Primary,
                activeTrackColor = MyPrintColors.Primary
            )
        )
    }
}

/** Bir nechta variantdan bittasini tanlash uchun qator. */
@Composable
fun SegmentedRow(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MyPrintColors.Surface)
            .border(1.dp, MyPrintColors.Border, RoundedCornerShape(10.dp))
    ) {

        options.forEachIndexed { index, label ->

            val isSelected = index == selectedIndex

            Box(
                modifier = Modifier
                    .background(
                        if (isSelected) MyPrintColors.Primary
                        else Color.Transparent
                    )
                    .clickable { onSelect(index) }
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold
                    else FontWeight.Normal,
                    color = if (isSelected) Color.White
                    else MyPrintColors.TextPrimary
                )
            }

            if (index != options.lastIndex) {

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(36.dp)
                        .background(MyPrintColors.Border)
                )
            }
        }
    }
}

/** 5.0 -> "5",  1.25 -> "1.3" */
fun Float.mm(): String =
    if (this == this.roundToInt().toFloat()) "${this.roundToInt()} mm"
    else String.format("%.1f mm", this)
