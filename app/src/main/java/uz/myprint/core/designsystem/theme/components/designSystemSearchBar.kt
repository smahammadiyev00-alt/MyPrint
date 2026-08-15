package uz.myprint.feature.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.myprint.core.designsystem.theme.MyPrintColors

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable { onClick() },

        shape = RoundedCornerShape(18.dp),

        color = Color.White,

        shadowElevation = 5.dp,

        tonalElevation = 1.dp
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Outlined.Search,
                    modifier = Modifier.size(22.dp),
                    contentDescription = null,
                    tint = Color.Gray
                )

                Text(
                    text = "Nima chop ettirmoqchisiz?",
                    modifier = Modifier.padding(start = 12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        MyPrintColors.Secondary,
                        CircleShape
                    ),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Outlined.Mic,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )

            }

        }

    }

}