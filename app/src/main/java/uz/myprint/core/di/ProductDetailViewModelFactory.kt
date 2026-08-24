package uz.myprint.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.myprint.feature.feature.product.presentation.viewmodel.ProductDetailViewModel

class ProductDetailViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(_root_ide_package_.uz.myprint.feature.feature.product.presentation.viewmodel.ProductDetailViewModel::class.java)) {

            return _root_ide_package_.uz.myprint.feature.feature.product.presentation.viewmodel.ProductDetailViewModel(
                getProductByIdUseCase = AppContainer.getProductByIdUseCase,
                getProductsByCategoryUseCase = AppContainer.getProductsByCategoryUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}