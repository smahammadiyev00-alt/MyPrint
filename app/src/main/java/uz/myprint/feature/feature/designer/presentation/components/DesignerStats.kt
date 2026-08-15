package uz.myprint.feature.feature.designer.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.myprint.feature.feature.designer.domain.model.PortfolioItem

@Composable
fun DesignerStats(
    portfolio: PortfolioItem
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        StatItem(
            icon = Icons.Default.Favorite,
            iconTint = Color(0xFFFF5A7A),
            value = formatCount(portfolio.likes),
            label = "Likes"
        )

        VerticalDivider(
            color = Color.White.copy(alpha = 0.20f)
        )

        StatItem(
            icon = Icons.Default.Visibility,
            iconTint = Color.White,
            value = formatCount(portfolio.views),
            label = "Views"
        )

    }

}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    value: String,
    label: String
) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = value,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.75f),
            style = MaterialTheme.typography.bodySmall
        )

    }

}

private fun formatCount(value: Int): String {

    return when {

        value >= 1_000_000 ->
            String.format("%.1fM", value / 1_000_000f)

        value >= 1_000 ->
            String.format("%.1fK", value / 1_000f)

        else ->
            value.toString()

    }

}