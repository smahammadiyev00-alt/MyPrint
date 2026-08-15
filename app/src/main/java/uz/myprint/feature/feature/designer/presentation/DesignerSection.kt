package uz.myprint.feature.feature.designer.presentation

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
import uz.myprint.feature.feature.designer.data.dummy.PortfolioDummyData
import uz.myprint.feature.feature.designer.domain.model.PortfolioItem
import uz.myprint.feature.feature.designer.presentation.components.DesignerCard

@Composable
fun DesignerSection(
    onSeeAllClick: () -> Unit = {},
    onPortfolioClick: (PortfolioItem) -> Unit = {}
) {

    val portfolio = PortfolioDummyData.portfolio

    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "🎨 Dizayner xizmati",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            TextButton(
                onClick = onSeeAllClick,
                contentPadding = PaddingValues(horizontal = 4.dp)
            )  {
                Text(
                    text = "Barchasi  →",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {

            items(portfolio) { item ->

                DesignerCard(
                    portfolio = item,
                    onClick = {
                        onPortfolioClick(item)
                    }
                )

            }

        }

    }

}