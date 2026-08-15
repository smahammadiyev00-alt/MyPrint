package uz.myprint.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = MyPrintColors.Primary,
    secondary = MyPrintColors.Secondary,
    background = MyPrintColors.Background,
    surface = MyPrintColors.Surface,
    error = MyPrintColors.Error,

    onPrimary = MyPrintColors.Surface,
    onSecondary = MyPrintColors.Surface,
    onBackground = MyPrintColors.TextPrimary,
    onSurface = MyPrintColors.TextPrimary
)

private val DarkColors = darkColorScheme(
    primary = MyPrintColors.Primary,
    secondary = MyPrintColors.Secondary,
    background = MyPrintColors.Background,
    surface = MyPrintColors.Surface,
    error = MyPrintColors.Error,

    onPrimary = MyPrintColors.Surface,
    onSecondary = MyPrintColors.Surface,
    onBackground = MyPrintColors.TextPrimary,
    onSurface = MyPrintColors.TextPrimary
)

@Composable
fun MyPrintTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MyPrintTypography,
        content = content
    )
}