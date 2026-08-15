package uz.myprint.feature.feature.project.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size

/**
 * Circular menu button displayed on the top-right
 * corner of the project cover.
 */

@Composable
fun ProjectMenuButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.95f),
        shadowElevation = 2.dp
    ) {

        IconButton(
            onClick = onClick,
            modifier = Modifier.size(40.dp)
        ) {

            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = "More options",
                modifier = Modifier.size(18.dp),
                tint = Color(0xFF1F2937)
            )

        }
    }
}