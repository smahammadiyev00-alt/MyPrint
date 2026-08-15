package uz.myprint.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import uz.myprint.core.designsystem.theme.MyPrintColors

private enum class BottomNavItem(
    val label: String,
    val icon: ImageVector
) {
    HOME(
        label = "Bosh sahifa",
        icon = Icons.Outlined.Home
    ),
    ORDERS(
        label = "Buyurtmalar",
        icon = Icons.Outlined.ReceiptLong
    ),
    FAVORITES(
        label = "Sevimlilar",
        icon = Icons.Outlined.FavoriteBorder
    ),
    PROFILE(
        label = "Profil",
        icon = Icons.Outlined.PersonOutline
    )
}

@Composable
fun MyPrintBottomBar(
    selectedItem: String = "home",
    onHomeClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onCreateClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                BottomNavItemView(
                    item = BottomNavItem.HOME,
                    selected = selectedItem == "home",
                    onClick = onHomeClick
                )

                BottomNavItemView(
                    item = BottomNavItem.ORDERS,
                    selected = selectedItem == "orders",
                    onClick = onOrdersClick
                )

                CreateButton(
                    onClick = onCreateClick
                )

                BottomNavItemView(
                    item = BottomNavItem.FAVORITES,
                    selected = selectedItem == "favorites",
                    onClick = onFavoritesClick
                )

                BottomNavItemView(
                    item = BottomNavItem.PROFILE,
                    selected = selectedItem == "profile",
                    onClick = onProfileClick
                )
            }

            Spacer(
                modifier = Modifier.windowInsetsBottomHeight(
                    WindowInsets.navigationBars
                )
            )
        }
    }
}

@Composable
private fun RowScope.BottomNavItemView(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val iconColor = if (selected) {
        MyPrintColors.Primary
    } else {
        MyPrintColors.TextSecondary
    }

    val textColor = if (selected) {
        MyPrintColors.Primary
    } else {
        MyPrintColors.TextSecondary
    }

    Column(
        modifier = Modifier
            .weight(1f)
            .height(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        IconButton(
            onClick = onClick,
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = iconColor,
                modifier = Modifier.size(21.dp)
            )
        }

        Text(
            text = item.label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun RowScope.CreateButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(68.dp),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = MyPrintColors.Primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Yaratish",
                    tint = Color.White,
                    modifier = Modifier.size(27.dp)
                )
            }
        }
    }
}