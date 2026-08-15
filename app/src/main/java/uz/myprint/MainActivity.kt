package uz.myprint
import uz.myprint.app.App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import uz.myprint.core.designsystem.theme.MyPrintTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()


        setContent {
            MyPrintTheme {
                App()
            }
        }
    }
}