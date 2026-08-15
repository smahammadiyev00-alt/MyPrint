package uz.myprint.feature.feature.product.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.presentation.components.ProductGrid
import uz.myprint.feature.feature.product.presentation.viewmodel.ProductViewModel

@Composable
fun ProductScreen(

    viewModel: ProductViewModel,

    products: List<Product>,

    onProductClick: (String) -> Unit = {}

) {

    val uiState by viewModel.uiState.collectAsState()

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

                Text(
                    text = uiState.error ?: "Xatolik yuz berdi",
                    color = MaterialTheme.colorScheme.error
                )

            }

        }

        else -> {

            ProductGrid(
                products = products,
                onProductClick = { product ->
                    onProductClick(product.id)
                }
            )

        }

    }

}