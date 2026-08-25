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
import uz.myprint.feature.feature.printshop.domain.model.ConfigLine
import uz.myprint.feature.feature.printshop.domain.model.ProductConfig
import uz.myprint.feature.feature.product.domain.model.usesSizeBreakdown
import uz.myprint.feature.feature.product.presentation.viewmode.ProductDetailEvent
import uz.myprint.feature.feature.product.presentation.viewmodel.ProductDetailViewModel

/**
 * Ekran ikki yo'l bilan ochiladi:
 *  - productId orqali (mahsulot ro'yxatidan yoki loyihadan)
 *  - category orqali (bosh sahifadagi kategoriyadan, ro'yxatsiz)
 */
@Composable
fun ProductDetailRoute(

    productId: String? = null,

    category: String? = null,

    onBackClick: () -> Unit = {},

    onAiClick: () -> Unit = {},

    onDesignStudioClick: () -> Unit = {},

    /**
     * lines — "m:10,l:15,xl:5" ko'rinishida kodlangan o'lcham/son juftliklari.
     */
    onOrderClick: (
        productId: String,
        materialId: String,
        printTypeId: String,
        finishIds: String,
        lines: String
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
                    selectedFinishIds = uiState.selectedFinishIds,
                    selectedSize = uiState.selectedSize,
                    quantity = uiState.quantity,
                    sizeQuantities = uiState.sizeQuantities,

                    onMaterialSelected = {
                        viewModel.onEvent(ProductDetailEvent.MaterialSelected(it))
                    },

                    onPrintTypeSelected = {
                        viewModel.onEvent(ProductDetailEvent.PrintTypeSelected(it))
                    },

                    onFinishToggled = {
                        viewModel.onEvent(ProductDetailEvent.FinishToggled(it))
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

                    onSizeQuantityChange = { sizeId, value ->
                        viewModel.onEvent(
                            ProductDetailEvent.SizeQuantityChanged(sizeId, value)
                        )
                    },

                    onBackClick = onBackClick,

                    onAiClick = onAiClick,

                    onDesignStudioClick = onDesignStudioClick,

                    onOrderClick = {

                        val lines =
                            if (product.category.usesSizeBreakdown) {

                                product.sizes.map { size ->
                                    ConfigLine(
                                        size = size,
                                        quantity = uiState.sizeQuantities[size.id] ?: 0
                                    )
                                }

                            } else {

                                listOf(
                                    ConfigLine(
                                        size = uiState.selectedSize,
                                        quantity = uiState.quantity
                                    )
                                )
                            }

                        // Hech narsa tanlanmagan bo'lsa navigatsiya qilmaymiz.
                        if (lines.sumOf { it.quantity } > 0) {

                            onOrderClick(
                                product.id,
                                uiState.selectedMaterial?.id.orEmpty(),
                                uiState.selectedPrintType?.id.orEmpty(),

                                // Faqat tanlangan qog'ozda haqiqatan
                                // mavjud bo'lganlari yuboriladi.
                                ProductConfig.encodeFinishes(
                                    uiState.selectedFinishes.map { it.id }
                                ),

                                ProductConfig.encodeLines(lines)
                            )
                        }
                    }
                )
            }
        }
    }
}