package uz.myprint.feature.feature.cart.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import uz.myprint.feature.feature.cart.domain.model.CartItem
import uz.myprint.feature.feature.cart.presentation.state.CartGroup
import uz.myprint.feature.feature.cart.presentation.state.CartUiState

@Composable
fun CartScreen(
    uiState: CartUiState,
    onRemoveItem: (String) -> Unit,
    onCheckout: () -> Unit,
    onBrowseProducts: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MyPrintColors.Background)
    ) {

        Text(
            text = "Savat",
            modifier = Modifier.padding(20.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MyPrintColors.TextPrimary
        )

        if (uiState.isEmpty) {

            EmptyCart(
                modifier = Modifier.weight(1f),
                onBrowseProducts = onBrowseProducts
            )

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                items(uiState.groups, key = { it.shopId }) { group ->

                    ShopGroup(
                        group = group,
                        onRemoveItem = onRemoveItem
                    )
                }

                if (uiState.orderCount > 1) {

                    item {
                        MultiOrderNote(count = uiState.orderCount)
                    }
                }
            }

            CheckoutBar(
                total = uiState.total,
                orderCount = uiState.orderCount,
                onCheckout = onCheckout
            )
        }
    }
}

@Composable
private fun ShopGroup(
    group: CartGroup,
    onRemoveItem: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MyPrintColors.Surface)
            .border(1.dp, MyPrintColors.Border, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                imageVector = Icons.Rounded.Storefront,
                contentDescription = null,
                tint = MyPrintColors.Primary,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = group.shopName,
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        group.items.forEachIndexed { index, item ->

            CartItemRow(
                item = item,
                onRemove = { onRemoveItem(item.id) }
            )

            if (index != group.items.lastIndex) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .height(1.dp)
                        .background(MyPrintColors.Divider)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "Buyurtma summasi",
                color = MyPrintColors.TextSecondary,
                fontSize = 13.sp
            )

            Text(
                text = group.total.asSom(),
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary
            )
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onRemove: () -> Unit
) {

    Row(modifier = Modifier.fillMaxWidth()) {

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = item.productName,
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = item.configSummary(),
                fontSize = 13.sp,
                color = MyPrintColors.TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (item.designFileUrl.isNullOrBlank()) {

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Fayl yuklanmagan",
                    fontSize = 12.sp,
                    color = MyPrintColors.Error
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(horizontalAlignment = Alignment.End) {

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onRemove() },
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "O'chirish",
                    tint = MyPrintColors.IconSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (item.total > 0) item.total.asSom()
                else "So'rov bo'yicha",
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun MultiOrderNote(count: Int) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MyPrintColors.Surface)
            .padding(14.dp)
    ) {

        Text(
            text = "Savatda $count ta poligrafiya bor. Har biriga alohida " +
                    "buyurtma yuboriladi va ular alohida javob beradi.",
            fontSize = 13.sp,
            color = MyPrintColors.TextSecondary
        )
    }
}

@Composable
private fun EmptyCart(
    modifier: Modifier = Modifier,
    onBrowseProducts: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Savat bo'sh",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MyPrintColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Mahsulot tanlang, sozlang va poligrafiyani tanlang.",
            textAlign = TextAlign.Center,
            color = MyPrintColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onBrowseProducts,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MyPrintColors.Primary,
                contentColor = MyPrintColors.Surface
            )
        ) {
            Text("Mahsulotlarni ko'rish")
        }
    }
}

@Composable
private fun CheckoutBar(
    total: Long,
    orderCount: Int,
    onCheckout: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MyPrintColors.Surface)
            .padding(20.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = "Jami",
                color = MyPrintColors.TextSecondary
            )

            Text(
                text = total.asSom(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onCheckout,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MyPrintColors.Primary,
                contentColor = MyPrintColors.Surface
            )
        ) {

            Text(
                text = if (orderCount > 1) "$orderCount ta buyurtma yuborish"
                else "Buyurtma berish",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "To'lov poligrafiya buyurtmani qabul qilgandan keyin.",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            color = MyPrintColors.TextSecondary
        )
    }
}

/** "Banner · 1 × 3 m · Orakal · 2 dona" */
private fun CartItem.configSummary(): String =
    listOfNotNull(
        config.size?.title,
        config.material?.name,
        config.printType?.name,
        config.finishes
            .takeIf { it.isNotEmpty() }
            ?.joinToString(" + ") { it.name },
        "${config.quantity} dona"
    ).joinToString(" · ")

/** 324000 -> "324 000 so'm" */
private fun Long.asSom(): String {

    val text = this.toString()
        .reversed()
        .chunked(3)
        .joinToString(" ")
        .reversed()

    return "$text so'm"
}