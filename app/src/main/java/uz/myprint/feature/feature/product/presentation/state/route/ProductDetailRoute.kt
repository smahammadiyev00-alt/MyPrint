package uz.myprint.feature.feature.product.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.core.di.ProductDetailViewModelFactory
import uz.myprint.feature.feature.product.presentation.state.viewmode.ProductDetailEvent
import uz.myprint.feature.feature.product.presentation.viewmodel.ProductDetailViewModel

/**
 * Ekran ikki yo'l bilan ochiladi:
 *  - productId orqali (mahsulot ro'yxatidan yoki loyihadan)
 *  - category orqali (bosh sahifadagi kategoriyadan, ro'yxatsiz)
 *
 * Ikkalasidan biri berilishi kifoya.
 */
@Composable
fun ProductDetailRoute(

    productId: String? = null,

    category: String? = null,

    onBackClick: () -> Unit = {},

    onAiClick: () -> Unit = {},

    onDesignStudioClick: () -> Unit = {},

    onOrderClick: (
        productId: String,
        materialId: String,
        printTypeId: String,
        sizeId: String,
        quantity: Int
    ) -> Unit = { _, _, _, _, _ -> }

) {

    val viewModel: ProductDetailViewModel = viewModel(
        factory = ProductDetailViewModelFactory()
    )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId, category) {

        when {
            !productId.isNullOrBlank() -> viewModel.loadProduct(productId)

            !category.isNullOrBlank() -> viewModel.loadProductByCategory(category)
        }
    }

    when {

        uiState.isLoading -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MyPrintColors.Primary)
            }
        }

        uiState.error != null -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error ?: "Xatolik yuz berdi",
                    color = MyPrintColors.Error
                )
            }
        }

        else -> {

            uiState.product?.let { product ->

                ProductDetailScreen(

                    product = product,

                    selectedMaterial = uiState.selectedMaterial,
                    selectedPrintType = uiState.selectedPrintType,
                    selectedSize = uiState.selectedSize,
                    quantity = uiState.quantity,

                    onMaterialSelected = {
                        viewModel.onEvent(ProductDetailEvent.MaterialSelected(it))
                    },

                    onPrintTypeSelected = {
                        viewModel.onEvent(ProductDetailEvent.PrintTypeSelected(it))
                    },

                    onSizeSelected = {
                        viewModel.onEvent(ProductDetailEvent.SizeSelected(it))
                    },

                    onQuantityChange = viewModel::setQuantity,

                    onIncreaseQuantity = {
                        viewModel.onEvent(ProductDetailEvent.IncreaseQuantity)
                    },

                    onDecreaseQuantity = {
                        viewModel.onEvent(ProductDetailEvent.DecreaseQuantity)
                    },

                    onBackClick = onBackClick,

                    onAiClick = onAiClick,

                    onDesignStudioClick = onDesignStudioClick,

                    onOrderClick = {

                        onOrderClick(
                            product.id,
                            uiState.selectedMaterial?.id.orEmpty(),
                            uiState.selectedPrintType?.id.orEmpty(),
                            uiState.selectedSize?.id.orEmpty(),
                            uiState.quantity
                        )
                    }
                )
            }
        }
    }
}