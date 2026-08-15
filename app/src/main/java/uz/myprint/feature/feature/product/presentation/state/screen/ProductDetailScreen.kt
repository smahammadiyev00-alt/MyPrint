package uz.myprint.feature.feature.product.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.presentation.components.ProductBottomActionBar
import uz.myprint.feature.feature.product.presentation.components.ProductDetailTopBar
import uz.myprint.feature.feature.product.presentation.components.ProductImageSection
import uz.myprint.feature.feature.product.presentation.components.ProductInfoSection
import uz.myprint.feature.feature.product.presentation.components.ProductRatingSection
import uz.myprint.feature.feature.product.presentation.state.components.ProductMaterialSection
import uz.myprint.feature.feature.product.presentation.state.components.ProductPrintTypeSection
import uz.myprint.feature.feature.product.presentation.state.components.ProductSizeSection

@Composable
fun ProductDetailScreen(
    product: Product,
    onBackClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onAiClick: () -> Unit = {},
    onOrderClick: () -> Unit = {}
) {

    Scaffold(

        bottomBar = {

            ProductBottomActionBar(
                modifier = Modifier.padding(16.dp),
                onAiClick = onAiClick,
                onOrderClick = onOrderClick
            )

        }

    ) { paddingValues ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(20.dp),

            verticalArrangement = Arrangement.spacedBy(24.dp)

        ) {

            ProductDetailTopBar(
                onBackClick = onBackClick,
                onFavoriteClick = onFavoriteClick,
                onShareClick = onShareClick
            )

            ProductImageSection()

            ProductInfoSection(
                product = product
            )

            ProductRatingSection()

            ProductMaterialSection(
                materials = product.materials,
                selectedMaterial = product.materials.firstOrNull(),
                onMaterialSelected = { material ->
                    // Hozircha bo'sh
                }
            )

            ProductPrintTypeSection(
                printTypes = product.printTypes,
                selectedPrintType = product.printTypes.firstOrNull(),
                onPrintTypeSelected = { }
            )

            ProductSizeSection(
                sizes = product.sizes,
                selectedSize = product.sizes.firstOrNull(),
                onSizeSelected = { }
            )

            Spacer(
                modifier = Modifier.height(80.dp)
            )

        }

    }

}