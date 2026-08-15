package uz.myprint.feature.feature.product.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import uz.myprint.core.di.ProductViewModelFactory
import uz.myprint.feature.feature.product.domain.model.ProductCategory
import uz.myprint.feature.feature.product.presentation.screen.ProductScreen
import uz.myprint.feature.feature.product.presentation.viewmodel.ProductViewModel

@Composable
fun ProductRoute(

    category: String,

    onProductClick: (String) -> Unit

) {

    val viewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory()
    )

    val uiState by viewModel.uiState.collectAsState()

    val selectedCategory = runCatching {
        ProductCategory.valueOf(category)
    }.getOrElse {
        ProductCategory.ALL
    }

    val filteredProducts =
        if (selectedCategory == ProductCategory.ALL) {
            uiState.products
        } else {
            uiState.products.filter {
                it.category == selectedCategory
            }
        }

    ProductScreen(
        viewModel = viewModel,
        products = filteredProducts,
        onProductClick = onProductClick
    )

}