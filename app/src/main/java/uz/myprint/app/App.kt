package uz.myprint.app

import androidx.compose.runtime.Composable
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import uz.myprint.core.designsystem.theme.MyPrintTheme
import uz.myprint.core.navigation.AppNavHost
@Composable
fun App() {

    MyPrintTheme {

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            AppNavHost()
        }

    }

}