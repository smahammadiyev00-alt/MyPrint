package uz.myprint.feature.feature.cart.presentation.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import uz.myprint.core.di.CartViewModelFactory
import uz.myprint.feature.feature.cart.presentation.screen.CartScreen
import uz.myprint.feature.feature.cart.presentation.viewmodel.CartViewModel

@Composable
fun CartRoute(
    onCheckout: () -> Unit = {},
    onBrowseProducts: () -> Unit = {}
) {

    val viewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory()
    )

    val uiState by viewModel.uiState.collectAsState()

    CartScreen(
        uiState = uiState,
        onRemoveItem = viewModel::removeItem,
        onCheckout = onCheckout,
        onBrowseProducts = onBrowseProducts
    )
}
