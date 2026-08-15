package uz.myprint.feature.feature.designer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DesignerBadge() {

    Row(
        modifier = Modifier
            .background(
                color = Color(0xFFEAF3FF),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = null,
            tint = Color(0xFF2196F3)
        )

        Text(
            text = "Verified",
            color = Color(0xFF2196F3),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )

    }

}