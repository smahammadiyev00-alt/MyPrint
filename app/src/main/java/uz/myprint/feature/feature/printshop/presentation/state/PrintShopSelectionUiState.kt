package uz.myprint.feature.feature.printshop.presentation.state

import uz.myprint.feature.feature.printshop.domain.model.PrintShopOffer

data class PrintShopSelectionUiState(

    val productName: String = "",

    val quantity: Int = 0,

    val offers: List<PrintShopOffer> = emptyList(),

    val isLoading: Boolean = false,

    val error: String? = null
)
