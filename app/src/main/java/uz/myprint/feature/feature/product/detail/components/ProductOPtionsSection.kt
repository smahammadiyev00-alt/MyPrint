package uz.myprint.feature.feature.product.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.domain.model.ProductMaterial
import uz.myprint.feature.feature.product.domain.model.ProductPrintType
import uz.myprint.feature.feature.product.domain.model.ProductSize

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductOptionsSection(
    product: Product,
    selectedMaterial: ProductMaterial?,
    selectedPrintType: ProductPrintType?,
    selectedSize: ProductSize?,
    onMaterialSelected: (ProductMaterial) -> Unit,
    onPrintTypeSelected: (ProductPrintType) -> Unit,
    onSizeSelected: (ProductSize) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier.padding(horizontal = 20.dp)) {

        if (product.materials.isNotEmpty()) {

            GroupTitle("Material")

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                product.materials.forEach { material ->

                    OptionChip(
                        // Zichlik bilan birga: "Art Paper 300g" va "Art Paper 350g".
                        // Aks holda ikkalasi bir xil ko'rinadi.
                        label = listOfNotNull(material.name, material.thickness)
                            .joinToString(" "),
                        isSelected = material.id == selectedMaterial?.id,
                        onClick = { onMaterialSelected(material) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        if (product.printTypes.isNotEmpty()) {

            GroupTitle("Bosma turi")

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                product.printTypes.forEach { printType ->

                    OptionChip(
                        label = printType.name,
                        isSelected = printType.id == selectedPrintType?.id,
                        onClick = { onPrintTypeSelected(printType) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        if (product.sizes.isNotEmpty()) {

            GroupTitle("O'lcham")

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                product.sizes.forEach { size ->

                    OptionChip(
                        // title allaqachon "90 × 50 mm" — takrorlash shart emas.
                        label = size.title,
                        isSelected = size.id == selectedSize?.id,
                        onClick = { onSizeSelected(size) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupTitle(text: String) {

    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MyPrintColors.TextPrimary
    )

    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
private fun OptionChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) MyPrintColors.Primary
                else MyPrintColors.Surface
            )
            .border(
                width = 1.dp,
                color = if (isSelected) MyPrintColors.Primary
                else MyPrintColors.Border,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {

        Text(
            text = label,
            color = if (isSelected) MyPrintColors.Surface
            else MyPrintColors.TextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}