package uz.myprint.feature.feature.product.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.feature.feature.product.detail.components.AiDesignerCard
import uz.myprint.feature.feature.product.detail.components.BottomOrderBar
import uz.myprint.feature.feature.product.detail.components.DesignStudioCard
import uz.myprint.feature.feature.product.detail.components.ProductImageSlider
import uz.myprint.feature.feature.product.detail.components.ProductInfoSection
import uz.myprint.feature.feature.product.detail.components.ProductOptionsSection
import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.domain.model.ProductCategory
import uz.myprint.feature.feature.product.domain.model.ProductMaterial
import uz.myprint.feature.feature.product.domain.model.ProductPrintType
import uz.myprint.feature.feature.product.domain.model.ProductSize
import uz.myprint.feature.feature.product.domain.model.usesSizeBreakdown
import uz.myprint.feature.feature.product.presentation.components.QuantitySelector
import uz.myprint.feature.feature.product.presentation.components.SizeBreakdownSelector

@Composable
fun ProductDetailScreen(

    product: Product,

    selectedMaterial: ProductMaterial?,
    selectedPrintType: ProductPrintType?,
    selectedSize: ProductSize?,
    quantity: Int,
    sizeQuantities: Map<String, Int>,

    onMaterialSelected: (ProductMaterial) -> Unit,
    onPrintTypeSelected: (ProductPrintType) -> Unit,
    onSizeSelected: (ProductSize) -> Unit,
    onQuantityChange: (Int) -> Unit,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onSizeQuantityChange: (sizeId: String, quantity: Int) -> Unit,

    onBackClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onAiClick: () -> Unit = {},
    onDesignStudioClick: () -> Unit = {},
    onOrderClick: () -> Unit = {}

) {

    val breakdown = product.category.usesSizeBreakdown

    val background = Brush.verticalGradient(
        colors = listOf(
            MyPrintColors.Background,
            MyPrintColors.Surface
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(background),

        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {

        item {
            ProductImageSlider(
                product = product,
                onBackClick = onBackClick,
                onFavoriteClick = onFavoriteClick
            )
        }

        item {
            ProductInfoSection(product = product)
        }

        item {
            ProductOptionsSection(
                product = product,

                // Taqsimot rejimida o'lcham quyida alohida beriladi,
                // shuning uchun bu yerda ko'rsatilmaydi.
                showSizes = !breakdown,

                selectedMaterial = selectedMaterial,
                selectedPrintType = selectedPrintType,
                selectedSize = selectedSize,
                onMaterialSelected = onMaterialSelected,
                onPrintTypeSelected = onPrintTypeSelected,
                onSizeSelected = onSizeSelected
            )
        }

        item {

            if (breakdown) {

                SizeBreakdownSelector(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    sizes = product.sizes,
                    quantities = sizeQuantities,
                    onQuantityChange = onSizeQuantityChange
                )

            } else {

                QuantitySelector(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    quantity = quantity,
                    presets = presetsFor(product.category),
                    onQuantityChange = onQuantityChange,
                    onIncrease = onIncreaseQuantity,
                    onDecrease = onDecreaseQuantity
                )
            }
        }

        item {
            AiDesignerCard(onClick = onAiClick)
        }

        item {
            DesignStudioCard(onClick = onDesignStudioClick)
        }

        item {
            BottomOrderBar(onOrderClick = onOrderClick)
        }
    }
}

/**
 * Tayyor tiraj variantlari. Maketda faqat vizitka uchun berilgan edi,
 * lekin banner uchun 100 dona mantiqsiz.
 */
private fun presetsFor(category: ProductCategory): List<Int> =
    when (category) {

        ProductCategory.BUSINESS_CARD,
        ProductCategory.FLYER,
        ProductCategory.STICKER -> listOf(100, 300, 500, 1000)

        ProductCategory.BANNER,
        ProductCategory.ROLL_UP -> listOf(1, 2, 5, 10)

        else -> listOf(1, 5, 10, 25)
    }
