package uz.myprint.feature.feature.product.detail.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.runtime.Composable

@Composable
fun DesignStudioCard(
    onClick: () -> Unit
) {

    ActionCard(
        icon = Icons.Outlined.Brush,
        title = "Dizayn Studio",
        description = "O'zingiz professional dizayn yarating.",
        onClick = onClick
    )

}