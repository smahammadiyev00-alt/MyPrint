package uz.myprint.feature.feature.promotion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import uz.myprint.feature.feature.promotion.components.PromotionCard
import uz.myprint.feature.feature.promotion.components.PromotionHeader
import uz.myprint.feature.feature.promotion.model.Partner
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import uz.myprint.feature.feature.promotion.components.PromotionIndicator


@Composable
fun PromotionSection(
    partners: List<Partner>,
    modifier: Modifier = Modifier,
    onPartnerClick: (Partner) -> Unit = {},
    onSeeAllClick: () -> Unit = {}
) {

    val startIndex = Int.MAX_VALUE / 2

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = startIndex
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(3500)

            listState.animateScrollToItem(
                listState.firstVisibleItemIndex + 1
            )
        }
    }
    val currentIndex by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex
        }
    }
    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        PromotionHeader(
            onSeeAllClick = onSeeAllClick
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {

            items(
                count = Int.MAX_VALUE
            ) { index ->

                val partner = partners[index % partners.size]

                PromotionCard(
                    partner = partner,
                    onClick = {
                        onPartnerClick(partner)
                    }
                )

            }

        }
        Spacer(modifier = Modifier.height(12.dp))

        PromotionIndicator(
            total = partners.size,
            selected = currentIndex % partners.size
        )

    }

}