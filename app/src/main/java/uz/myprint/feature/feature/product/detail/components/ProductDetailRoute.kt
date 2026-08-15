package uz.myprint.feature.feature.product.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import uz.myprint.core.di.ProductViewModelFactory
import uz.myprint.feature.feature.product.presentation.viewmodel.ProductViewModel

@Composable
fun ProductDetailRoute(

    productId: String,

    onBackClick: () -> Unit = {}

) {

    val viewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory()
    )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadProductById(productId)
    }

    uiState.selectedProduct?.let {

        ProductDetailScreen(
            product = uiState.selectedProduct!!,
            onBackClick = onBackClick
        )
    }

}