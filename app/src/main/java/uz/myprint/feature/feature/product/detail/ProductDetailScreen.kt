package uz.myprint.feature.feature.product.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uz.myprint.feature.feature.product.detail.components.AiDesignerCard
import uz.myprint.feature.feature.product.detail.components.BottomOrderBar
import uz.myprint.feature.feature.product.detail.components.DesignStudioCard
import uz.myprint.feature.feature.product.detail.components.ProductImageSlider
import uz.myprint.feature.feature.product.detail.components.ProductInfoSection
import uz.myprint.feature.feature.product.detail.components.ProductOptionsSection
import uz.myprint.feature.feature.product.domain.model.Product

@Composable
fun ProductDetailScreen(

    product: Product,

    onBackClick: () -> Unit = {},

    onFavoriteClick: () -> Unit = {},

    onAiClick: () -> Unit = {},

    onDesignStudioClick: () -> Unit = {},

    onOrderClick: () -> Unit = {}

) {

    val background = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF7F8FC),
            Color.White
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(background),

        contentPadding = PaddingValues(
            top = 24.dp,
            bottom = 32.dp
        ),

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

            ProductInfoSection(
                product = product
            )

        }

        item {

            ProductOptionsSection(
                product = product
            )

        }

        item {

            AiDesignerCard(
                onClick = onAiClick
            )

        }

        item {

            DesignStudioCard(
                onClick = onDesignStudioClick
            )

        }

        item {

            BottomOrderBar(
                onOrderClick = onOrderClick
            )

        }

    }

}