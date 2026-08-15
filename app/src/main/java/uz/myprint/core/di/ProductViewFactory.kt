package uz.myprint.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.myprint.feature.feature.product.presentation.viewmodel.ProductViewModel

class ProductViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {

            return ProductViewModel(
                getProductsUseCase = AppContainer.getProductsUseCase,
                getProductByIdUseCase = AppContainer.getProductByIdUseCase
            ) as T

        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}