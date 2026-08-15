package uz.myprint.feature.feature.product.presentation.state.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.myprint.core.designsystem.component.MyPrintSelectableCard
import uz.myprint.feature.feature.product.domain.model.ProductMaterial
@Composable
fun ProductMaterialSection(
    materials: List<ProductMaterial>,
    selectedMaterial: ProductMaterial?,
    onMaterialSelected: (ProductMaterial) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Material",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        materials.forEach { material ->

            MyPrintSelectableCard(
                modifier = Modifier.fillMaxWidth(),
                title = material.name,
                subtitle = material.description,
                selected = material.id == selectedMaterial?.id,
                leadingIcon = Icons.Outlined.Description,
                onClick = {
                    onMaterialSelected(material)
                }
            )

        }

    }

}