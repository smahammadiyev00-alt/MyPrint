package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.core.di.AppContainer
import uz.myprint.core.navigation.Screen
import uz.myprint.feature.feature.design.studio.domain.DesignDocument

/** Yuklab bo'lingan maket va uning mahsuloti. */
private data class StudioArgs(
    val productName: String,
    val document: DesignDocument
)

@Composable
fun DesignStudioRoute(
    productId: String,
    sizeId: String,
    onBackClick: () -> Unit = {}
) {

    // Eslatma: AppContainer'ga bevosita murojaat qilinyapti.
    // Kerakli use case suspend bo'lgani uchun uni ViewModel
    // factory'siga qo'yib bo'lmaydi. Keyinchalik alohida
    // yuklovchi ViewModel qilinadi.
    val argsState = produceState<StudioArgs?>(
        initialValue = null,
        key1 = productId,
        key2 = sizeId
    ) {

        val product = AppContainer.getProductByIdUseCase(productId)

        val size = product?.sizes?.firstOrNull { it.id == sizeId }
            ?: product?.sizes?.firstOrNull { it.isDefault }
            ?: product?.sizes?.firstOrNull()

        value = if (product != null && size != null) {

            StudioArgs(
                productName = product.name,
                document = DesignDocument.forProductSize(
                    id = product.id,
                    size = size
                )
            )

        } else {
            null
        }
    }

    val args = argsState.value

    if (args == null) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            if (sizeId == Screen.DesignStudio.NO_SIZE && productId.isBlank()) {

                Text(
                    text = "Mahsulot topilmadi",
                    color = MyPrintColors.Error
                )

            } else {
                CircularProgressIndicator(color = MyPrintColors.Primary)
            }
        }

        return
    }

    val viewModel: DesignEditorViewModel = viewModel(
        key = args.document.id,
        factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DesignEditorViewModel(args.document) as T
            }
        }
    )

    DesignStudioScreen(
        viewModel = viewModel,
        productName = args.productName,
        onBackClick = onBackClick
    )
}
