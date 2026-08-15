package uz.myprint.feature.feature.project.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.MaterialTheme

/**
 * Small category badge displayed on the project cover.
 */
@Composable
fun ProjectBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = RoundedCornerShape(12),
        shadowElevation = 0.dp
    ) {
        Text(
            text = text,
            modifier = Modifier
                .heightIn(min = 26.dp)
                .padding(
                    horizontal = 10.dp,
                    vertical = 5.dp
                ),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.sp,
            maxLines = 1
        )
    }
}