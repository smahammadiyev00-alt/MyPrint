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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.myprint.core.designsystem.theme.MyPrintColors

private const val MAX_QUANTITY = 100_000

@Composable
fun QuantitySelector(
    quantity: Int,
    presets: List<Int>,
    onQuantityChange: (Int) -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {

    // Maydonni tahrirlash paytida bo'sh qoldirish mumkin bo'lishi kerak,
    // shuning uchun matn alohida holatda saqlanadi.
    var text by remember { mutableStateOf(quantity.toString()) }

    var isFocused by remember { mutableStateOf(false) }

    // Tashqaridan (+/- yoki preset) o'zgarsa, maydon ham yangilanadi.
    LaunchedEffect(quantity) {
        if (!isFocused) {
            text = quantity.toString()
        }
    }

    val keyboard = LocalSoftwareKeyboardController.current

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
                    icon = Icons.Rounded.Remove,
                    description = "Kamaytirish",
                    onClick = {
                        keyboard?.hide()
                        onDecrease()
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .width(92.dp)
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isFocused) MyPrintColors.Background
                            else MyPrintColors.Surface
                        )
                        .border(
                            width = if (isFocused) 2.dp else 1.dp,
                            color = if (isFocused) MyPrintColors.Primary
                            else MyPrintColors.Border,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    BasicTextField(
                        value = text,
                        onValueChange = { input ->

                            val digits = input
                                .filter { it.isDigit() }
                                .take(6)

                            text = digits

                            digits.toIntOrNull()?.let { value ->
                                if (value in 1.._root_ide_package_.uz.myprint.feature.feature.product.presentation.components.MAX_QUANTITY) {
                                    onQuantityChange(value)
                                }
                            }
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MyPrintColors.TextPrimary
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
                            .padding(horizontal = 8.dp)
                            .onFocusChanged { state ->

                                isFocused = state.isFocused

                                // Maydon bo'sh qoldirilsa, oxirgi to'g'ri
                                // qiymatga qaytamiz.
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
                    onClick = {
                        keyboard?.hide()
                        onIncrease()
                    }
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
                        .clickable {
                            keyboard?.hide()
                            onQuantityChange(preset)
                        }
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
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

        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = MyPrintColors.IconPrimary,
            modifier = Modifier.size(20.dp)
        )
    }
}