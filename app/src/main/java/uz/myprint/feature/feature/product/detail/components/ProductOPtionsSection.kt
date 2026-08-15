package uz.myprint.feature.feature.product.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.feature.feature.product.domain.model.Product

@Composable
fun ProductOptionsSection(
    product: Product
) {

    if (product.materials.isNotEmpty()) {

        OptionSection(
            title = "Material",
            items = product.materials.map { it.name }
        )

        Spacer(modifier = Modifier.height(20.dp))
    }

    if (product.printTypes.isNotEmpty()) {

        OptionSection(
            title = "Bosma turi",
            items = product.printTypes.map { it.name }
        )

        Spacer(modifier = Modifier.height(20.dp))
    }

    if (product.sizes.isNotEmpty()) {

        OptionSection(
            title = "O'lcham",
            items = product.sizes.map {
                "${it.title} (${it.width} × ${it.height} ${it.unit})"
            }
        )
    }
}

@Composable
private fun OptionSection(
    title: String,
    items: List<String>
) {

    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(items) { item ->

                AssistChip(
                    onClick = { },
                    label = {
                        Text(item)
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MyPrintColors.Background
                    )
                )
            }
        }
    }
}