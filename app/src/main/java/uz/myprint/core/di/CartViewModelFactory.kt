package uz.myprint.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.myprint.feature.feature.cart.presentation.viewmodel.CartViewModel

class CartViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {

            return CartViewModel(
                cartRepository = AppContainer.cartRepository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}