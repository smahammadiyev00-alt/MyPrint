package uz.myprint.feature.feature.product.presentation.state.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.myprint.core.designsystem.component.MyPrintSelectableCard
import uz.myprint.feature.feature.product.domain.model.ProductSize
@Composable
fun ProductSizeSection(
    sizes: List<ProductSize>,
    selectedSize: ProductSize?,
    onSizeSelected: (ProductSize) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "O'lcham",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        sizes.forEach { size ->

            MyPrintSelectableCard(
                modifier = Modifier.fillMaxWidth(),
                title = size.title,
                subtitle = "${size.width} × ${size.height} ${size.unit}",
                selected = size.id == selectedSize?.id,
                leadingIcon = Icons.Outlined.Straighten,
                onClick = {
                    onSizeSelected(size)
                }
            )

        }

    }

}

