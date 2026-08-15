package uz.myprint.feature.feature.product.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProductBottomActionBar(
    modifier: Modifier = Modifier,
    onAiClick: () -> Unit = {},
    onOrderClick: () -> Unit = {}
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        OutlinedButton(
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            onClick = onAiClick
        ) {

            Text(
                text = "AI dizayn",
                style = MaterialTheme.typography.labelLarge
            )

        }

        Button(
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            onClick = onOrderClick
        ) {

            Text(
                text = "Buyurtma berish",
                style = MaterialTheme.typography.labelLarge
            )

        }

    }

}