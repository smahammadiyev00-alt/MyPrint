package uz.myprint.feature.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.core.designsystem.theme.MyPrintRadius
import uz.myprint.core.designsystem.theme.MyPrintSpacing

private const val OTP_LENGTH = 6
private const val RESEND_SECONDS = 60

@Composable
fun OtpScreen(
    phone: String,
    onVerifySuccess: () -> Unit,
    onResendCode: () -> Unit = {}
) {

    val otp = remember { mutableStateListOf("", "", "", "", "", "") }

    val focusRequesters = remember { List(OTP_LENGTH) { FocusRequester() } }

    var focusedIndex by remember { mutableIntStateOf(0) }

    var secondsLeft by remember { mutableIntStateOf(RESEND_SECONDS) }

    var resendAttempt by remember { mutableIntStateOf(0) }

    val keyboard = LocalSoftwareKeyboardController.current

    val isComplete = otp.none { it.isEmpty() }

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }

    LaunchedEffect(resendAttempt) {
        secondsLeft = RESEND_SECONDS
        while (secondsLeft > 0) {
            delay(1_000)
            secondsLeft--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MyPrintColors.Surface)
            .padding(MyPrintSpacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Tasdiqlash kodi",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MyPrintColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(MyPrintSpacing.md))

        Text(
            text = "${formatPhone(phone)} raqamiga kod yuborildi",
            color = MyPrintColors.TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(MyPrintSpacing.sm)
        ) {

            repeat(OTP_LENGTH) { index ->

                val isFocused = focusedIndex == index

                Box(
                    modifier = Modifier
                        .size(width = 48.dp, height = 56.dp)
                        .clip(RoundedCornerShape(MyPrintRadius.lg))
                        .background(
                            if (isFocused) MyPrintColors.Background
                            else MyPrintColors.Surface
                        )
                        .border(
                            width = if (isFocused) 2.dp else 1.dp,
                            color = if (isFocused) MyPrintColors.Primary
                            else MyPrintColors.Border,
                            shape = RoundedCornerShape(MyPrintRadius.lg)
                        )
                        .clickable {
                            focusRequesters[index].requestFocus()
                        },
                    contentAlignment = Alignment.Center
                ) {

                    BasicTextField(
                        value = otp[index],
                        onValueChange = { raw ->

                            val digits = raw.filter { it.isDigit() }

                            if (digits.isEmpty()) {
                                otp[index] = ""
                            } else {

                                // Mavjud raqam ustiga yangisi yozilsa yoki kod
                                // nusxalab qo'yilsa — raqamlarni tarqatamiz.
                                val incoming =
                                    if (digits.length > 1 &&
                                        digits.first().toString() == otp[index]
                                    ) {
                                        digits.drop(1)
                                    } else {
                                        digits
                                    }

                                incoming.forEachIndexed { offset, char ->
                                    val target = index + offset
                                    if (target < OTP_LENGTH) {
                                        otp[target] = char.toString()
                                    }
                                }

                                val next = (index + incoming.length)
                                    .coerceAtMost(OTP_LENGTH - 1)

                                focusRequesters[next].requestFocus()
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword
                        ),
                        textStyle = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MyPrintColors.TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 14.dp)
                            .focusRequester(focusRequesters[index])
                            .onFocusChanged { state ->
                                if (state.isFocused) {
                                    focusedIndex = index
                                }
                            }
                            .onKeyEvent { event ->

                                if (event.type == KeyEventType.KeyDown &&
                                    event.key == Key.Backspace
                                ) {
                                    if (otp[index].isEmpty() && index > 0) {
                                        otp[index - 1] = ""
                                        focusRequesters[index - 1].requestFocus()
                                    } else {
                                        otp[index] = ""
                                    }
                                    true
                                } else {
                                    false
                                }
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        if (secondsLeft > 0) {

            Text(
                text = formatTimer(secondsLeft),
                color = MyPrintColors.Primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(MyPrintSpacing.md))

            Text(
                text = "Kodni qayta yuborish",
                color = MyPrintColors.Border
            )

        } else {

            Text(
                text = "Kod kelmadimi?",
                color = MyPrintColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(MyPrintSpacing.md))

            Text(
                text = "Kodni qayta yuborish",
                color = MyPrintColors.Primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    resendAttempt++
                    otp.indices.forEach { otp[it] = "" }
                    focusRequesters[0].requestFocus()
                    onResendCode()
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                keyboard?.hide()
                onVerifySuccess()
            },
            enabled = isComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(MyPrintRadius.xl),
            colors = ButtonDefaults.buttonColors(
                containerColor = MyPrintColors.Primary,
                contentColor = MyPrintColors.Surface,
                disabledContainerColor = MyPrintColors.Border,
                disabledContentColor = MyPrintColors.TextSecondary
            )
        ) {
            Text("Tasdiqlash")
        }
    }
}

/** 901234567 yoki +998901234567 -> "+998 90 123 45 67" */
private fun formatPhone(raw: String): String {

    val digits = raw.filter { it.isDigit() }.removePrefix("998")

    if (digits.length != 9) return "+998 $digits"

    return "+998 ${digits.substring(0, 2)} ${digits.substring(2, 5)} " +
            "${digits.substring(5, 7)} ${digits.substring(7, 9)}"
}

private fun formatTimer(seconds: Int): String {
    val minutes = seconds / 60
    val rest = seconds % 60
    return "%02d:%02d".format(minutes, rest)
}