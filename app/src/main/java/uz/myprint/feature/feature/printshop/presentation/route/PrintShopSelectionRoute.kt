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
import uz.myprint.feature.feature.printshop.presentation.screen.PrintShopSelectionScreen
import uz.myprint.feature.feature.printshop.presentation.viewmodel.PrintShopSelectionViewModel

@Composable
fun PrintShopSelectionRoute(

    productId: String,
    materialId: String,
    printTypeId: String,
    lines: String,

    onBackClick: () -> Unit = {},

    /** Savatga qo'shilgach chaqiriladi. */
    onAddedToCart: () -> Unit = {}

) {

    val viewModel: PrintShopSelectionViewModel = viewModel(
        factory = PrintShopSelectionViewModelFactory()
    )

    val uiState by viewModel.uiState.collectAsState()

    val addedToCart by viewModel.addedToCart.collectAsState()

    LaunchedEffect(productId, materialId, printTypeId, lines) {

        viewModel.load(
            productId = productId,
            materialId = materialId,
            printTypeId = printTypeId,
            lines = lines
        )
    }

    LaunchedEffect(addedToCart) {

        if (addedToCart) {
            onAddedToCart()
        }
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
                onContinue = viewModel::addToCart
            )
        }
    }
}