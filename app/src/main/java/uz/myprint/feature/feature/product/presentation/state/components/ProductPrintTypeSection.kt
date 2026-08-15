package uz.myprint.feature.feature.product.presentation.state.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.myprint.core.designsystem.component.MyPrintSelectableCard

import uz.myprint.feature.feature.product.domain.model.ProductPrintType
@Composable
fun ProductPrintTypeSection(
    printTypes: List<ProductPrintType>,
    selectedPrintType: ProductPrintType?,
    onPrintTypeSelected: (ProductPrintType) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Bosma turi",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        printTypes.forEach { printType ->

            MyPrintSelectableCard(
                modifier = Modifier.fillMaxWidth(),
                title = printType.name,
                subtitle = printType.description,
                selected = printType.id == selectedPrintType?.id,
                leadingIcon = Icons.Outlined.Print,
                onClick = {
                    onPrintTypeSelected(printType)
                }
            )

        }

    }

}