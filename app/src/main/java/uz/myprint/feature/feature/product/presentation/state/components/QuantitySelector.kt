package uz.myprint.feature.feature.product.presentation.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.myprint.core.designsystem.theme.MyPrintColors

/**
 * Soni tanlash. Maketda tayyor variantlar (100 / 200 / 300 / 500 / 1000)
 * bor edi, lekin real buyurtma 250 yoki 1500 bo'lishi mumkin — shuning
 * uchun tayyor variantlar ham, +/- ham beriladi.
 */
@Composable
fun QuantitySelector(
    quantity: Int,
    presets: List<Int>,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {

    Column(modifier = modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "Soni",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {

                StepButton(
                    icon = { Icon(Icons.Rounded.Remove, contentDescription = "Kamaytirish") },
                    onClick = onDecrease
                )

                Box(
                    modifier = Modifier.width(72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = quantity.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyPrintColors.TextPrimary
                    )
                }

                StepButton(
                    icon = { Icon(Icons.Rounded.Add, contentDescription = "Ko'paytirish") },
                    onClick = onIncrease
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            presets.forEach { preset ->

                val isSelected = preset == quantity

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
                        .clickable { onQuantityChange(preset) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {

                    Text(
                        text = preset.toString(),
                        color = if (isSelected) MyPrintColors.Surface
                        else MyPrintColors.TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold
                        else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun StepButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, MyPrintColors.Border, RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}
