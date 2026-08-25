package uz.myprint.feature.feature.product.detail.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.runtime.Composable

@Composable
fun AiDesignerCard(
    onClick: () -> Unit
) {

    ActionCard(
        icon = Icons.Rounded.AutoAwesome,
        title = "AI Designer",
        description = "Bir necha so'z yozing — maket o'zi tayyorlanadi.",
        badge = "1 daqiqada",
        highlighted = true,
        onClick = onClick
    )
}