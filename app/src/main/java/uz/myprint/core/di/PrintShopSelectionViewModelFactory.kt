package uz.myprint.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.myprint.feature.feature.printshop.presentation.viewmodel.PrintShopSelectionViewModel

class PrintShopSelectionViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(PrintShopSelectionViewModel::class.java)) {

            return PrintShopSelectionViewModel(
                getProductByIdUseCase = AppContainer.getProductByIdUseCase,
                getPrintShopOffersUseCase = AppContainer.getPrintShopOffersUseCase,
                cartRepository = AppContainer.cartRepository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}