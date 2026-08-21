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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.feature.feature.product.domain.model.ProductSize

/**
 * Futbolka kabi mahsulotlar uchun: har bir o'lchamga alohida son.
 */
@Composable
fun SizeBreakdownSelector(
    sizes: List<ProductSize>,
    quantities: Map<String, Int>,
    onQuantityChange: (sizeId: String, quantity: Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val total = quantities.values.sum()

    Column(modifier = modifier.fillMaxWidth()) {

        Text(
            text = "O'lcham va soni",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MyPrintColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(
                    width = 1.dp,
                    color = MyPrintColors.Border,
                    shape = RoundedCornerShape(14.dp)
                )
        ) {

            sizes.forEachIndexed { index, size ->

                SizeRow(
                    size = size,
                    quantity = quantities[size.id] ?: 0,
                    onQuantityChange = { onQuantityChange(size.id, it) }
                )

                if (index != sizes.lastIndex) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MyPrintColors.Divider)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "Jami",
                color = MyPrintColors.TextSecondary
            )

            Text(
                text = if (total > 0) "$total dona" else "Tanlanmagan",
                fontWeight = FontWeight.Bold,
                color = if (total > 0) MyPrintColors.TextPrimary
                else MyPrintColors.IconSecondary
            )
        }
    }
}

@Composable
private fun SizeRow(
    size: ProductSize,
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {

    var text by remember { mutableStateOf(quantity.toString()) }

    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(quantity) {
        if (!isFocused) {
            text = quantity.toString()
        }
    }

    val keyboard = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = size.title,
                fontWeight = if (quantity > 0) FontWeight.Bold
                else FontWeight.Normal,
                color = MyPrintColors.TextPrimary
            )

            if (size.additionalPrice > 0) {

                Text(
                    text = "+${size.additionalPrice.formatSom()} / dona",
                    fontSize = 12.sp,
                    color = MyPrintColors.TextSecondary
                )
            }
        }

        StepButton(
            icon = Icons.Rounded.Remove,
            description = "Kamaytirish",
            enabled = quantity > 0,
            onClick = {
                keyboard?.hide()
                onQuantityChange((quantity - 1).coerceAtLeast(0))
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .width(64.dp)
                .height(38.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(
                    if (isFocused) MyPrintColors.Background
                    else MyPrintColors.Surface
                )
                .border(
                    width = if (isFocused) 2.dp else 1.dp,
                    color = if (isFocused) MyPrintColors.Primary
                    else MyPrintColors.Border,
                    shape = RoundedCornerShape(9.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            BasicTextField(
                value = text,
                onValueChange = { input ->

                    val digits = input.filter { it.isDigit() }.take(5)

                    text = digits

                    onQuantityChange(digits.toIntOrNull() ?: 0)
                },
                singleLine = true,
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (quantity > 0) MyPrintColors.TextPrimary
                    else MyPrintColors.IconSecondary
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboard?.hide() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
                    .onFocusChanged { state ->

                        isFocused = state.isFocused

                        if (!state.isFocused && text.isBlank()) {
                            text = quantity.toString()
                        }
                    }
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        StepButton(
            icon = Icons.Rounded.Add,
            description = "Ko'paytirish",
            enabled = true,
            onClick = {
                keyboard?.hide()
                onQuantityChange(quantity + 1)
            }
        )
    }
}

@Composable
private fun StepButton(
    icon: ImageVector,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(9.dp))
            .border(1.dp, MyPrintColors.Border, RoundedCornerShape(9.dp))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = if (enabled) MyPrintColors.IconPrimary
            else MyPrintColors.Border,
            modifier = Modifier.size(18.dp)
        )
    }
}

private fun Long.formatSom(): String {

    val text = this.toString()
        .reversed()
        .chunked(3)
        .joinToString(" ")
        .reversed()

    return "$text so'm"
}
