package uz.myprint.feature.feature.product.detail.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.runtime.Composable

@Composable
fun AiDesignerCard(
    onClick: () -> Unit
) {

    ActionCard(
        icon = Icons.Outlined.AutoAwesome,
        title = "AI Designer",
        description = "AI yordamida professional dizayn yarating.",
        onClick = onClick
    )

}