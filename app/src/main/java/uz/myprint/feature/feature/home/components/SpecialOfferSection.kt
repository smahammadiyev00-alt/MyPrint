package uz.myprint.feature.feature.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.myprint.feature.feature.promotion.data.sampleSpecialOffers

@Composable
fun SpecialOfferSection(
    onSeeAllClick: () -> Unit = {},
    onOfferClick: (String) -> Unit = {}
) {

    val offers = sampleSpecialOffers

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "🔥 Maxsus takliflar",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            TextButton(
                onClick = onSeeAllClick
            ) {
                Text("Barchasi")
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(offers) { offer ->

                SpecialOfferCard(
                    title = offer.title,
                    discount = offer.discount,
                    description = offer.description,
                    image = offer.imageRes,
                    onClick = {
                        onOfferClick(offer.title)
                    }
                )
            }
        }
    }
}