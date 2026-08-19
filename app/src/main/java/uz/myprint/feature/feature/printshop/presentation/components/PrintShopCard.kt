package uz.myprint.feature.feature.printshop.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.feature.feature.printshop.domain.model.PriceQuote
import uz.myprint.feature.feature.printshop.domain.model.PrintShopOffer
import uz.myprint.feature.feature.printshop.domain.model.ShopBadge

@Composable
fun PrintShopCard(
    offer: PrintShopOffer,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val shop = offer.shop

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MyPrintColors.Surface)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MyPrintColors.Primary
                else MyPrintColors.Border,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth()) {

            // Logotip o'rni. Coil ulangach AsyncImage bilan almashadi.
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MyPrintColors.Background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = shop.name.take(1),
                    fontWeight = FontWeight.Bold,
                    color = MyPrintColors.Primary,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = shop.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MyPrintColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (shop.isVerified) {

                        Spacer(modifier = Modifier.width(4.dp))

                        Icon(
                            imageVector = Icons.Rounded.Verified,
                            contentDescription = "Tasdiqlangan",
                            tint = MyPrintColors.Primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (ShopBadge.PARTNER in shop.badges) {

                        Spacer(modifier = Modifier.width(6.dp))

                        PartnerLabel()
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = MyPrintColors.Primary,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(3.dp))

                    Text(
                        text = "${shop.rating}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyPrintColors.TextPrimary
                    )

                    Text(
                        text = " (${shop.reviewCount})",
                        fontSize = 13.sp,
                        color = MyPrintColors.TextSecondary
                    )

                    Text(
                        text = " · ${shop.district}",
                        fontSize = 13.sp,
                        color = MyPrintColors.TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    offer.formattedDistance()?.let { distance ->
                        Text(
                            text = " · $distance",
                            fontSize = 13.sp,
                            color = MyPrintColors.TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (val quote = offer.quote) {

            is PriceQuote.Available -> AvailableRow(quote, shop.hasDelivery)

            is PriceQuote.OnRequest -> OnRequestRow(quote)
        }
    }
}

@Composable
private fun AvailableRow(
    quote: PriceQuote.Available,
    hasDelivery: Boolean
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    contentDescription = null,
                    tint = MyPrintColors.IconSecondary,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "${quote.productionDays} kun",
                    fontSize = 13.sp,
                    color = MyPrintColors.TextSecondary
                )

                if (hasDelivery) {

                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(
                        imageVector = Icons.Rounded.LocalShipping,
                        contentDescription = null,
                        tint = MyPrintColors.IconSecondary,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = if (quote.deliveryPrice == 0L) "Bepul yetkazish"
                        else "${quote.deliveryPrice.asSom()} yetkazish",
                        fontSize = 13.sp,
                        color = if (quote.deliveryPrice == 0L) MyPrintColors.Success
                        else MyPrintColors.TextSecondary
                    )
                }
            }

            if (quote.discountPercent > 0) {

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Tirajga -${quote.discountPercent}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MyPrintColors.Success
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {

            Text(
                text = quote.total.asSom(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary
            )

            Text(
                text = "${quote.unitPrice.asSom()} / dona",
                fontSize = 12.sp,
                color = MyPrintColors.TextSecondary
            )
        }
    }
}

@Composable
private fun OnRequestRow(quote: PriceQuote.OnRequest) {

    val message = when (quote.reason) {

        PriceQuote.OnRequest.Reason.NO_TARIFF ->
            "Narx so'rov bo'yicha"

        PriceQuote.OnRequest.Reason.BELOW_MIN_QUANTITY ->
            "Minimal tiraj yetarli emas"

        PriceQuote.OnRequest.Reason.RUSH_NOT_AVAILABLE ->
            "Shoshilinch buyurtma qabul qilinmaydi"

        PriceQuote.OnRequest.Reason.NOT_ACCEPTING ->
            "Hozircha buyurtma qabul qilinmayapti"
    }

    val isBlocking =
        quote.reason == PriceQuote.OnRequest.Reason.BELOW_MIN_QUANTITY ||
                quote.reason == PriceQuote.OnRequest.Reason.NOT_ACCEPTING

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MyPrintColors.Background)
            .padding(vertical = 10.dp)
    ) {

        Text(
            text = message,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isBlocking) MyPrintColors.TextSecondary
            else MyPrintColors.Primary
        )
    }
}

@Composable
private fun PartnerLabel() {

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MyPrintColors.Background)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "Reklama",
            fontSize = 10.sp,
            color = MyPrintColors.TextSecondary
        )
    }
}

/** 324000 -> "324 000 so'm" */
internal fun Long.asSom(): String {

    val text = this.toString()
        .reversed()
        .chunked(3)
        .joinToString(" ")
        .reversed()

    return "$text so'm"
}
