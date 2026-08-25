package uz.myprint.feature.feature.product.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.feature.feature.product.domain.model.PrintOptionKind
import uz.myprint.feature.feature.product.domain.model.Product
import uz.myprint.feature.feature.product.domain.model.ProductCategory
import uz.myprint.feature.feature.product.domain.model.ProductMaterial
import uz.myprint.feature.feature.product.domain.model.ProductPrintType
import uz.myprint.feature.feature.product.domain.model.ProductSize
import uz.myprint.feature.feature.product.domain.model.SizeUnit
import uz.myprint.feature.feature.product.domain.model.allowsCustomSize
import uz.myprint.feature.feature.product.domain.model.areaSquareMeters
import uz.myprint.feature.feature.product.domain.model.customProductSize
import uz.myprint.feature.feature.product.domain.model.isAvailableFor
import uz.myprint.feature.feature.product.domain.model.isCustom

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductOptionsSection(
    product: Product,
    selectedMaterial: ProductMaterial?,
    selectedPrintType: ProductPrintType?,
    selectedFinishIds: Set<String>,
    selectedSize: ProductSize?,
    onMaterialSelected: (ProductMaterial) -> Unit,
    onPrintTypeSelected: (ProductPrintType) -> Unit,
    onFinishToggled: (ProductPrintType) -> Unit,
    onSizeSelected: (ProductSize) -> Unit,
    modifier: Modifier = Modifier,

    /** Futbolkada o'lcham taqsimot bloki ichida beriladi. */
    showSizes: Boolean = true
) {

    Column(modifier = modifier.padding(horizontal = 20.dp)) {

        if (product.materials.isNotEmpty()) {

            MaterialSection(
                materials = product.materials,
                selectedMaterial = selectedMaterial,
                onMaterialSelected = onMaterialSelected
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        val sides = product.printTypes.filter { it.kind == PrintOptionKind.SIDE }

        val finishes = product.printTypes.filter { it.kind == PrintOptionKind.FINISH }

        if (sides.isNotEmpty()) {

            GroupTitle("Bosma turi")

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                sides.forEach { side ->

                    OptionChip(
                        label = side.name,
                        isSelected = side.id == selectedPrintType?.id,
                        onClick = { onPrintTypeSelected(side) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        if (finishes.isNotEmpty()) {

            FinishSection(
                finishes = finishes,
                selectedMaterial = selectedMaterial,
                selectedFinishIds = selectedFinishIds,
                onFinishToggled = onFinishToggled
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        if (showSizes && product.sizes.isNotEmpty()) {

            SizeSection(
                product = product,
                selectedSize = selectedSize,
                onSizeSelected = onSizeSelected
            )
        }
    }
}

/**
 * Laminatsiya va UV lak — tarafdan mustaqil qo'shimchalar, shuning
 * uchun bir nechtasi birga tanlanadi.
 *
 * Mos kelmaydigan variant yashirilmaydi, o'chirilgan holda qoladi:
 * shunda mijoz bunday imkoniyat borligini va uni qanday olishni
 * ko'radi.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FinishSection(
    finishes: List<ProductPrintType>,
    selectedMaterial: ProductMaterial?,
    selectedFinishIds: Set<String>,
    onFinishToggled: (ProductPrintType) -> Unit
) {

    GroupTitle("Qo'shimcha")

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        finishes.forEach { finish ->

            OptionChip(
                label = finish.name,
                isSelected = finish.id in selectedFinishIds,
                enabled = finish.isAvailableFor(selectedMaterial),
                onClick = { onFinishToggled(finish) }
            )
        }
    }

    // O'chirilgan variantlarning sababi. Bir nechta bo'lsa ham
    // har biri alohida qatorda chiqadi.
    val blocked = finishes.filter {
        !it.isAvailableFor(selectedMaterial) && it.unavailableHint.isNotBlank()
    }

    if (blocked.isNotEmpty()) {

        Spacer(modifier = Modifier.height(8.dp))

        blocked.forEach { finish ->

            Text(
                text = "${finish.name} — ${finish.unavailableHint}",
                fontSize = 12.sp,
                color = MyPrintColors.TextSecondary
            )
        }
    }
}

/**
 * Materiallar ikki bosqichda ko'rsatiladi: avval sirt (glyansli /
 * matoviy), keyin zichlik (160g ... 350g). Aks holda vizitkada
 * o'nta chip bir qatorga tushib ketadi.
 *
 * Ma'lumot modeli o'zgarmaydi — bu faqat ko'rsatish usuli.
 * Zichligi yo'q materiallar (banner, orakal) oddiy qatorda chiqadi.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MaterialSection(
    materials: List<ProductMaterial>,
    selectedMaterial: ProductMaterial?,
    onMaterialSelected: (ProductMaterial) -> Unit
) {

    // Nom bo'yicha guruhlash. Tartib ma'lumotdagidek qoladi.
    val byName = remember(materials) {
        materials.groupBy { it.name }
    }

    val hasThickness = remember(materials) {
        materials.any { !it.thickness.isNullOrBlank() }
    }

    val useTwoLevel = byName.size > 1 && hasThickness

    if (!useTwoLevel) {

        GroupTitle("Material")

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            materials.forEach { material ->

                OptionChip(
                    label = listOfNotNull(material.name, material.thickness)
                        .joinToString(" "),
                    isSelected = material.id == selectedMaterial?.id,
                    onClick = { onMaterialSelected(material) }
                )
            }
        }

        return
    }

    val selectedName = selectedMaterial?.name ?: byName.keys.first()

    val thicknesses = byName[selectedName].orEmpty()

    GroupTitle("Qog'oz turi")

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        byName.forEach { (name, group) ->

            OptionChip(
                label = name,
                isSelected = name == selectedName,
                onClick = {

                    // Sirt almashganda zichlikni saqlab qolamiz.
                    // Bu sirtda o'sha zichlik bo'lmasa, birinchisi olinadi.
                    val keepThickness = group
                        .firstOrNull { it.thickness == selectedMaterial?.thickness }

                    val next = keepThickness
                        ?: group.firstOrNull { it.isDefault }
                        ?: group.first()

                    onMaterialSelected(next)
                }
            )
        }
    }

    // Tanlangan sirtda bitta variant bo'lsa (masalan Kraft),
    // zichlik qatori ko'rsatilmaydi.
    if (thicknesses.size > 1) {

        Spacer(modifier = Modifier.height(14.dp))

        GroupTitle("Zichlik")

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            thicknesses.forEach { material ->

                OptionChip(
                    label = material.thickness.orEmpty(),
                    isSelected = material.id == selectedMaterial?.id,
                    onClick = { onMaterialSelected(material) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SizeSection(
    product: Product,
    selectedSize: ProductSize?,
    onSizeSelected: (ProductSize) -> Unit
) {

    GroupTitle("O'lcham")

    val allowsCustom = product.category.allowsCustomSize

    val customUnit = customUnitFor(product.category)

    var customOpen by remember {
        mutableStateOf(selectedSize?.isCustom == true)
    }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        product.sizes.forEach { size ->

            OptionChip(
                label = size.title,
                isSelected = size.id == selectedSize?.id,
                onClick = {
                    customOpen = false
                    onSizeSelected(size)
                }
            )
        }

        if (allowsCustom) {

            OptionChip(
                label = "Boshqa o'lcham",
                isSelected = customOpen || selectedSize?.isCustom == true,
                onClick = { customOpen = true }
            )
        }
    }

    if (allowsCustom && customOpen) {

        Spacer(modifier = Modifier.height(14.dp))

        CustomSizeInput(
            unit = customUnit,
            selectedSize = selectedSize,
            onSizeSelected = onSizeSelected
        )
    }
}

/**
 * Banner metrda o'lchanadi (pogon metr), sticker esa santimetrda.
 */
private fun customUnitFor(category: ProductCategory): SizeUnit =
    when (category) {

        ProductCategory.BANNER,
        ProductCategory.ROLL_UP -> SizeUnit.M

        else -> SizeUnit.CM
    }

@Composable
private fun CustomSizeInput(
    unit: SizeUnit,
    selectedSize: ProductSize?,
    onSizeSelected: (ProductSize) -> Unit
) {

    val isSameUnit = selectedSize?.isCustom == true && selectedSize.unit == unit

    var widthText by remember(unit) {
        mutableStateOf(if (isSameUnit) selectedSize.width.clean() else "")
    }

    var heightText by remember(unit) {
        mutableStateOf(if (isSameUnit) selectedSize.height.clean() else "")
    }

    fun push() {

        val w = widthText.toFloatOrNull()
        val h = heightText.toFloatOrNull()

        if (w != null && h != null && w > 0f && h > 0f) {
            onSizeSelected(customProductSize(w, h, unit))
        }
    }

    val unitLabel = when (unit) {
        SizeUnit.M -> "m"
        SizeUnit.CM -> "cm"
        SizeUnit.MM -> "mm"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MyPrintColors.Background)
            .padding(14.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            DimensionField(
                value = widthText,
                label = "Eni",
                allowDecimal = unit == SizeUnit.M,
                onValueChange = {
                    widthText = it
                    push()
                }
            )

            Text(
                text = "×",
                modifier = Modifier.padding(horizontal = 12.dp),
                color = MyPrintColors.TextSecondary,
                fontWeight = FontWeight.Bold
            )

            DimensionField(
                value = heightText,
                label = "Bo'yi",
                allowDecimal = unit == SizeUnit.M,
                onValueChange = {
                    heightText = it
                    push()
                }
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = unitLabel,
                color = MyPrintColors.TextSecondary
            )
        }

        val area = selectedSize
            ?.takeIf { it.isCustom }
            ?.areaSquareMeters

        if (area != null) {

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Maydon: %.2f m²".format(area),
                fontSize = 13.sp,
                color = MyPrintColors.TextSecondary
            )
        }
    }
}

@Composable
private fun DimensionField(
    value: String,
    label: String,
    allowDecimal: Boolean,
    onValueChange: (String) -> Unit
) {

    val keyboard = LocalSoftwareKeyboardController.current

    Column {

        Text(
            text = label,
            fontSize = 12.sp,
            color = MyPrintColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .width(96.dp)
                .height(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MyPrintColors.Surface)
                .border(1.dp, MyPrintColors.Border, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {

            BasicTextField(
                value = value,
                onValueChange = { input ->

                    val filtered = if (allowDecimal) {

                        // Bitta nuqtaga ruxsat: "1.5"
                        input
                            .filter { it.isDigit() || it == '.' }
                            .let { text ->
                                val firstDot = text.indexOf('.')
                                if (firstDot == -1) text
                                else text.substring(0, firstDot + 1) +
                                        text.substring(firstDot + 1).filter { it.isDigit() }
                            }
                            .take(6)

                    } else {
                        input.filter { it.isDigit() }.take(5)
                    }

                    onValueChange(filtered)
                },
                singleLine = true,
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MyPrintColors.TextPrimary
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (allowDecimal) KeyboardType.Decimal
                    else KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboard?.hide() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
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
    onClick: () -> Unit,
    enabled: Boolean = true
) {

    val background = when {
        !enabled -> MyPrintColors.Background
        isSelected -> MyPrintColors.Primary
        else -> MyPrintColors.Surface
    }

    val borderColor = when {
        !enabled -> MyPrintColors.Border
        isSelected -> MyPrintColors.Primary
        else -> MyPrintColors.Border
    }

    val textColor = when {
        !enabled -> MyPrintColors.IconSecondary
        isSelected -> MyPrintColors.Surface
        else -> MyPrintColors.TextSecondary
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {

        Text(
            text = label,
            color = textColor,
            fontWeight = if (isSelected && enabled) FontWeight.Bold
            else FontWeight.Normal
        )
    }
}

/** 3.0 -> "3",  1.5 -> "1.5" */
private fun Float.clean(): String =
    if (this == this.toInt().toFloat()) this.toInt().toString()
    else this.toString()