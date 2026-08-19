package uz.myprint.feature.feature.printshop.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.feature.feature.printshop.domain.model.PriceQuote
import uz.myprint.feature.feature.printshop.domain.model.PrintShopOffer
import uz.myprint.feature.feature.printshop.presentation.components.PrintShopCard
import uz.myprint.feature.feature.printshop.presentation.components.asSom

enum class OfferSort(val label: String) {

    PRICE("Narx bo'yicha"),

    SPEED("Tezlik bo'yicha"),

    RATING("Reyting bo'yicha"),

    DISTANCE("Masofa bo'yicha")
}

@Composable
fun PrintShopSelectionScreen(
    offers: List<PrintShopOffer>,
    productName: String,
    quantity: Int,
    onBackClick: () -> Unit,
    onContinue: (PrintShopOffer) -> Unit,
    modifier: Modifier = Modifier
) {

    var sort by remember { mutableStateOf(OfferSort.PRICE) }

    var selectedShopId by remember { mutableStateOf<String?>(null) }

    val sorted = remember(offers, sort) { offers.sortedWith(comparatorFor(sort)) }

    val selectedOffer = sorted.firstOrNull { it.shop.id == selectedShopId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MyPrintColors.Background)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MyPrintColors.Surface)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Orqaga",
                    tint = MyPrintColors.IconPrimary
                )
            }

            Column {

                Text(
                    text = "Poligrafiya tanlash",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MyPrintColors.TextPrimary
                )

                Text(
                    text = "$productName · $quantity dona",
                    fontSize = 13.sp,
                    color = MyPrintColors.TextSecondary
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    OfferSort.entries.forEach { option ->

                        SortChip(
                            label = option.label,
                            isSelected = option == sort,
                            onClick = { sort = option }
                        )
                    }
                }
            }

            if (sorted.isEmpty()) {

                item {
                    EmptyState()
                }

            } else {

                items(sorted, key = { it.shop.id }) { offer ->

                    PrintShopCard(
                        offer = offer,
                        isSelected = offer.shop.id == selectedShopId,
                        onClick = {
                            selectedShopId =
                                if (selectedShopId == offer.shop.id) null
                                else offer.shop.id
                        }
                    )
                }
            }
        }

        BottomBar(
            offer = selectedOffer,
            onContinue = { selectedOffer?.let(onContinue) }
        )
    }
}

@Composable
private fun SortChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) MyPrintColors.Primary
                else MyPrintColors.Surface
            )
            .border(
                width = 1.dp,
                color = if (isSelected) MyPrintColors.Primary
                else MyPrintColors.Border,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {

        Text(
            text = label,
            fontSize = 13.sp,
            color = if (isSelected) MyPrintColors.Surface
            else MyPrintColors.TextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun EmptyState() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "Bu konfiguratsiya uchun poligrafiya topilmadi.",
            color = MyPrintColors.TextSecondary
        )
    }
}

@Composable
private fun BottomBar(
    offer: PrintShopOffer?,
    onContinue: () -> Unit
) {

    val quote = offer?.quote as? PriceQuote.Available

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MyPrintColors.Surface)
            .padding(16.dp)
    ) {

        if (offer != null) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = offer.shop.name,
                    color = MyPrintColors.TextSecondary
                )

                Text(
                    text = quote?.total?.asSom() ?: "So'rov bo'yicha",
                    fontWeight = FontWeight.Bold,
                    color = MyPrintColors.TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = onContinue,
            enabled = offer != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MyPrintColors.Primary,
                contentColor = MyPrintColors.Surface,
                disabledContainerColor = MyPrintColors.Border,
                disabledContentColor = MyPrintColors.TextSecondary
            )
        ) {

            Text(
                text = when {
                    offer == null -> "Poligrafiyani tanlang"
                    quote == null -> "So'rov yuborish"
                    else -> "Davom etish"
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun comparatorFor(sort: OfferSort): Comparator<PrintShopOffer> =
    when (sort) {

        // Narxi bor poligrafiyalar oldinda, so'rov bo'yichalari orqada.
        OfferSort.PRICE -> compareBy(
            { (it.quote as? PriceQuote.Available)?.total ?: Long.MAX_VALUE },
            { it.shop.name }
        )

        OfferSort.SPEED -> compareBy(
            { (it.quote as? PriceQuote.Available)?.productionDays ?: Int.MAX_VALUE },
            { it.shop.name }
        )

        OfferSort.RATING -> compareByDescending { it.shop.rating }

        OfferSort.DISTANCE -> compareBy(
            { it.distanceMeters ?: Int.MAX_VALUE },
            { it.shop.name }
        )
    }
