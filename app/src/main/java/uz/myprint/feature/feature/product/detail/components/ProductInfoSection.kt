package uz.myprint.feature.feature.product.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.feature.feature.product.domain.model.Product

@Composable
fun ProductInfoSection(
    product: Product
) {

    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {

        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            repeat(5) {

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFB800),
                    modifier = Modifier.size(18.dp)
                )

            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "4.9",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "(1.2K buyurtma)",
                color = MyPrintColors.TextSecondary
            )

        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MyPrintColors.TextSecondary
        )

    }

}