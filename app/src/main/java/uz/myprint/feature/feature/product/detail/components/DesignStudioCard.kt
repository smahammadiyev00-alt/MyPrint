package uz.myprint.feature.feature.product.detail.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.runtime.Composable

@Composable
fun DesignStudioCard(
    onClick: () -> Unit
) {

    ActionCard(
        icon = Icons.Rounded.Brush,
        title = "Dizayn Studio",
        description = "Matn, rasm va logotipni o'zingiz joylashtiring.",
        highlighted = false,
        onClick = onClick
    )
}