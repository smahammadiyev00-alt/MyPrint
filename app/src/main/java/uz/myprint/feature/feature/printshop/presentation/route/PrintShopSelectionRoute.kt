package uz.myprint.feature.feature.printshop.presentation.route

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.core.di.PrintShopSelectionViewModelFactory
import uz.myprint.feature.feature.printshop.domain.model.PrintShopOffer
import uz.myprint.feature.feature.printshop.presentation.screen.PrintShopSelectionScreen
import uz.myprint.feature.feature.printshop.presentation.viewmodel.PrintShopSelectionViewModel

@Composablev
fun PrintShopSelectionRoute(

    productId: String,
    materialId: String,
    printTypeId: String,
    sizeId: String,
    quantity: Int,

    onBackClick: () -> Unit = {},

    onContinue: (PrintShopOffer) -> Unit = {}

) {

    val viewModel: PrintShopSelectionViewModel = viewModel(
        factory = PrintShopSelectionViewModelFactory()
    )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId, materialId, printTypeId, sizeId, quantity) {

        viewModel.load(
            productId = productId,
            materialId = materialId,
            printTypeId = printTypeId,
            sizeId = sizeId,
            quantity = quantity
        )
    }

    when {

        uiState.isLoading -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MyPrintColors.Primary)
            }
        }

        uiState.error != null -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error ?: "Xatolik yuz berdi",
                    color = MyPrintColors.Error
                )
            }
        }

        else -> {

            PrintShopSelectionScreen(
                offers = uiState.offers,
                productName = uiState.productName,
                quantity = uiState.quantity,
                onBackClick = onBackClick,
                onContinue = onContinue
            )
        }
    }
}
