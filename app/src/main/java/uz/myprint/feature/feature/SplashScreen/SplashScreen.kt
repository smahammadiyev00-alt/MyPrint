package uz.myprint.feature.feature.SplashScreen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import uz.myprint.R
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {

    val logoScale = remember { Animatable(0.75f) }
    val logoAlpha = remember { Animatable(0f) }
    val sloganAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {

        // Logo Fade In
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(500)
        )

        // Scale Up
        logoScale.animateTo(
            targetValue = 1.08f,
            animationSpec = tween(
                durationMillis = 700,
                easing = FastOutSlowInEasing
            )
        )

        // Soft Bounce
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = 0.45f,
                stiffness = 300f
            )
        )

        delay(150.milliseconds)

        // Slogan Fade In
        sloganAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 500,
                easing = LinearOutSlowInEasing
            )
        )

        delay(150.milliseconds)

        onSplashFinished()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(R.drawable.myprint_logo),
            contentDescription = "MyPrint Logo",
            modifier = Modifier
                .size(220.dp)
                .scale(logoScale.value)
                .alpha(logoAlpha.value)
        )

        Text(
            text = "Print Smarter",
            modifier = Modifier.alpha(sloganAlpha.value),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )
    }
}