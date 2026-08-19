package uz.myprint.feature.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uz.myprint.feature.feature.designer.domain.model.PortfolioItem
import uz.myprint.feature.feature.designer.presentation.DesignerSection
import uz.myprint.feature.feature.home.components.CategoryItem
import uz.myprint.feature.feature.home.components.HomeCategorySection
import uz.myprint.feature.feature.home.components.HomeHeader
import uz.myprint.feature.feature.home.components.HomeHeroSection
import uz.myprint.feature.feature.home.components.HomeProjectSection
import uz.myprint.feature.feature.home.components.HomeSearchSection
import uz.myprint.feature.feature.home.components.SpecialOfferSection
import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.promotion.PromotionSection
import uz.myprint.feature.feature.promotion.data.samplePartners

@Composable
fun HomeScreen(

    onCategoryClick: (CategoryItem) -> Unit = {},

    onProductClick: (Product) -> Unit = {},

    onPortfolioClick: (PortfolioItem) -> Unit = {},

    onSpecialOffersClick: () -> Unit = {},

    onOfferClick: (String) -> Unit = {}

) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FB)),
        contentPadding = PaddingValues(
            top = 40.dp,
            bottom = 24.dp
        )
    ) {

        item {

            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {

                HomeHeader()

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                HomeSearchSection()
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            HomeHeroSection()

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            HomeCategorySection(
                onCategoryClick = onCategoryClick
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            PromotionSection(
                partners = samplePartners
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            HomeProjectSection()

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            DesignerSection(
                onSeeAllClick = {
                    // Keyingi bosqichda Designer navigation qo‘shamiz
                },
                onPortfolioClick = onPortfolioClick
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            SpecialOfferSection(
                onSeeAllClick = onSpecialOffersClick,

                onOfferClick = { offerTitle ->
                    onOfferClick(offerTitle)
                }
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }
}