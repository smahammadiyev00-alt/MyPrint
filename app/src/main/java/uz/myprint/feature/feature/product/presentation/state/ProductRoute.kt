package uz.myprint.feature.feature.product.presentation.state

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import uz.myprint.feature.feature.product.presentation.screen.ProductDetailScreen
import uz.myprint.feature.feature.product.presentation.viewmodel.ProductViewModel

@Composable
fun ProductRoute(
    productId: String,
    viewModel: ProductViewModel,
    onBackClick: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadProductById(productId)
    }

    when {

        uiState.isLoading -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        }

        uiState.error != null -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(uiState.error!!)
            }

        }

        uiState.selectedProduct != null -> {

            ProductDetailScreen(
                product = uiState.selectedProduct!!
            )

        }

    }

}