package uz.myprint.feature.feature.product.detail.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomOrderBar(
    onOrderClick: () -> Unit
) {

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        onClick = onOrderClick
    ) {

        Text(
            text = "Buyurtma berish",
            style = MaterialTheme.typography.titleMedium
        )

    }

}