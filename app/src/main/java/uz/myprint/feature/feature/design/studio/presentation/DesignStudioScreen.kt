package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Crop169
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FormatAlignCenter
import androidx.compose.material.icons.rounded.FormatColorFill
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.Height
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Opacity
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Straighten
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.feature.feature.design.studio.data.ShareFormat
import uz.myprint.feature.feature.design.studio.domain.LayerTransform
import uz.myprint.feature.feature.design.studio.domain.ShapeKind
import uz.myprint.feature.feature.design.studio.domain.ShapeLayer
import uz.myprint.feature.feature.design.studio.domain.TextAlign
import uz.myprint.feature.feature.design.studio.domain.TextCase
import uz.myprint.feature.feature.design.studio.domain.TextLayer
import kotlin.math.roundToInt

/** Kanvas ostida ochiladigan sozlash panellari. */
private enum class Panel {
    FONT,
    SIZE,
    COLOR,
    ALIGN,
    SPACING,
    OPACITY,
    SHAPE,
    BACKGROUND,

    /**
     * Aniq o'lcham va burilish.
     *
     * Barmoq bilan 61.0 mm ni qo'yib bo'lmaydi, poligrafiyada esa
     * mijoz ko'pincha aniq o'lcham talab qiladi. Shuning uchun
     * cho'zishdan tashqari raqamli yo'l ham kerak.
     */
    DIMENSIONS
}

@Composable
fun DesignStudioScreen(
    viewModel: DesignEditorViewModel,
    productName: String,
    onBackClick: () -> Unit = {},
    onDoneClick: () -> Unit = {},
    onShare: (ShareFormat) -> Unit = {}
) {

    val document = viewModel.document

    val selected = viewModel.selectedLayer

    val text = viewModel.selectedText

    val shape = viewModel.selectedShape

    var panel by remember { mutableStateOf<Panel?>(null) }

    var editingText by remember { mutableStateOf<String?>(null) }

    var choosingShareFormat by remember { mutableStateOf(false) }

    // Tanlov o'zgarganda ochiq panel boshqa turdagi element uchun
    // ma'nosiz bo'lib qolishi mumkin, shuning uchun yopiladi.
    val selectedId = viewModel.selectedLayerId

    remember(selectedId) {
        panel = null
        selectedId
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MyPrintColors.Surface)
    ) {

        TopBar(
            productName = productName,
            sizeLabel = buildString {

                append(document.widthMm.clean())
                append(" × ")
                append(document.heightMm.clean())
                append(" mm · 300 DPI")

                // Bakal 82 mm deb tanlangan-u studioda 210 mm
                // chiqsa, izohsiz bu xatodek ko'rinadi.
                document.note?.let {
                    append(" · ")
                    append(it)
                }
            },
            canUndo = viewModel.canUndo,
            canRedo = viewModel.canRedo,
            aspectLocked = viewModel.aspectLocked,
            snapEnabled = viewModel.snapEnabled,
            onToggleAspect = viewModel::toggleAspectLock,
            onToggleSnap = viewModel::toggleSnap,
            onShareClick = { choosingShareFormat = true },
            onBackClick = onBackClick,
            onUndo = viewModel::undo,
            onRedo = viewModel::redo,
            onDoneClick = onDoneClick
        )

        // weight(1f) — kanvas qolgan bo'sh joyni oladi. Sloylar doki
        // ochilganda kanvas O'ZI kichrayadi, panel uning ustiga
        // tushmaydi. Modal sheet'dagi asosiy muammo shu edi.
        DesignCanvas(
            viewModel = viewModel,
            modifier = Modifier.weight(1f),
            onLongPress = { layer -> viewModel.openContextMenu(layer.id) }
        )

        if (viewModel.layersPanelOpen) {
            LayersDock(viewModel = viewModel)
        }

        val outside = document.layersOutsideSafeArea()

        if (outside.isNotEmpty()) {
            SafeAreaWarning(count = outside.size)
        }

        PanelContent(
            panel = panel,
            viewModel = viewModel
        )

        if (selected != null) {

            SelectionBar(
                isText = text != null,
                isShape = shape != null,
                activePanel = panel,
                onPanel = { panel = if (panel == it) null else it },
                onEditText = { editingText = text?.text.orEmpty() },
                onDuplicate = viewModel::duplicateSelected,
                onForward = viewModel::bringForward,
                onBackward = viewModel::sendBackward,
                onDelete = viewModel::deleteSelected
            )
        }

        AddBar(
            activePanel = panel,
            onAddText = {

                viewModel.addLayer(
                    TextLayer(
                        id = DesignEditorViewModel.newId(),
                        transform = centered(
                            document.widthMm,
                            document.heightMm,
                            document.widthMm * 0.6f,
                            12f
                        ),
                        text = "Matn",
                        fontSizeMm = 5f
                    )
                )
            },
            onAddShape = {

                viewModel.addLayer(
                    ShapeLayer(
                        id = DesignEditorViewModel.newId(),
                        transform = centered(
                            document.widthMm,
                            document.heightMm,
                            document.widthMm * 0.4f,
                            document.heightMm * 0.25f
                        ),
                        cornerRadiusMm = 1.5f
                    )
                )
            },
            onBackground = {
                viewModel.select(null)
                panel = if (panel == Panel.BACKGROUND) null else Panel.BACKGROUND
            },
            layerCount = document.layers.size,
            isLayersOpen = viewModel.layersPanelOpen,
            onLayers = {
                viewModel.showLayersPanel(!viewModel.layersPanelOpen)
            }
        )
    }

    if (choosingShareFormat) {

        ShareFormatDialog(
            onDismiss = { choosingShareFormat = false },
            onPick = { format ->
                choosingShareFormat = false
                onShare(format)
            }
        )
    }

    LayerContextMenu(
        viewModel = viewModel,
        onEditText = { editingText = viewModel.selectedText?.text.orEmpty() }
    )

    editingText?.let { current ->

        TextEditDialog(
            initial = current,
            onDismiss = { editingText = null },
            onConfirm = { value ->
                viewModel.updateText { it.copy(text = value) }
                editingText = null
            }
        )
    }
}

@Composable
private fun PanelContent(
    panel: Panel?,
    viewModel: DesignEditorViewModel
) {

    val text = viewModel.selectedText

    val shape = viewModel.selectedShape

    when (panel) {

        Panel.FONT -> if (text != null) {

            PanelSurface(title = "Shrift") {

                Column {

                    FontPanel(
                        selected = text.font,
                        onPick = { font ->
                            viewModel.updateText { it.copy(font = font) }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        ToggleChip(
                            label = "Qalin",
                            isActive = text.isBold,
                            onClick = {
                                viewModel.updateText {
                                    it.copy(isBold = !it.isBold)
                                }
                            }
                        )

                        ToggleChip(
                            label = "Qiya",
                            isActive = text.isItalic,
                            onClick = {
                                viewModel.updateText {
                                    it.copy(isItalic = !it.isItalic)
                                }
                            }
                        )

                        ToggleChip(
                            label = "Tagchiziq",
                            isActive = text.isUnderline,
                            onClick = {
                                viewModel.updateText {
                                    it.copy(isUnderline = !it.isUnderline)
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    SegmentedRow(
                        options = listOf("Asl", "KATTA", "kichik"),
                        selectedIndex = when (text.textCase) {
                            TextCase.NORMAL -> 0
                            TextCase.UPPER -> 1
                            TextCase.LOWER -> 2
                        },
                        onSelect = { index ->
                            viewModel.updateText {
                                it.copy(
                                    textCase = when (index) {
                                        1 -> TextCase.UPPER
                                        2 -> TextCase.LOWER
                                        else -> TextCase.NORMAL
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }

        Panel.SIZE -> if (text != null) {

            PanelSurface(title = "Shrift o'lchami") {

                ValueSlider(
                    label = "Balandlik",
                    value = text.fontSizeMm,
                    range = 1.5f..40f,
                    format = { it.mm() },
                    onChange = { value ->
                        viewModel.updateText { it.copy(fontSizeMm = value) }
                    }
                )
            }
        }

        // ---- YANGI: elementning o'zining o'lchami ----
        Panel.DIMENSIONS -> {

            // Guruh tanlangan bo'lsa uning umumiy chegarasi
            // ko'rsatiladi va o'zgartirilganda a'zolar mutanosib
            // ergashadi.
            val t = viewModel.selectionTransform

            if (t != null) {

                PanelSurface(
                    title = buildString {

                        append("O'lcham")

                        if (viewModel.isGroupSelected) {
                            append(" · guruh (")
                            append(viewModel.selectionIds.size)
                            append(" ta)")
                        }

                        if (viewModel.aspectLocked) {
                            append(" · proporsiya qulflangan")
                        }
                    }
                ) {

                    Column {

                        ValueSlider(
                            label = "Kenglik",
                            value = t.widthMm,
                            range = 2f..viewModel.document.widthMm,
                            format = { it.mm() },
                            onChange = { value ->
                                viewModel.setSelectedSize(widthMm = value)
                            }
                        )

                        ValueSlider(
                            label = "Balandlik",
                            value = t.heightMm,
                            range = 2f..viewModel.document.heightMm,
                            format = { it.mm() },
                            onChange = { value ->
                                viewModel.setSelectedSize(heightMm = value)
                            }
                        )

                        ValueSlider(
                            label = "Burilish",
                            value = t.rotationDeg.coerceIn(-180f, 180f),
                            range = -180f..180f,
                            format = { "${it.roundToInt()}°" },
                            onChange = { value ->
                                viewModel.updateSelected { current ->
                                    current.withTransform(
                                        current.transform.copy(
                                            rotationDeg = value
                                        )
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        AlignRow(
                            onAlign = viewModel::alignSelected
                        )
                    }
                }
            }
        }

        Panel.COLOR -> {

            PanelSurface(title = "Rang") {

                ColorPanel(
                    selected = text?.color ?: shape?.fill,
                    onPick = viewModel::applyColor
                )
            }
        }

        Panel.BACKGROUND -> {

            PanelSurface(title = "Orqa fon") {

                ColorPanel(
                    selected = viewModel.document.background,
                    onPick = viewModel::setBackground
                )
            }
        }

        Panel.ALIGN -> if (text != null) {

            PanelSurface(title = "Tekislash") {

                SegmentedRow(
                    options = listOf("Chapga", "Markazga", "O'ngga"),
                    selectedIndex = when (text.align) {
                        TextAlign.START -> 0
                        TextAlign.CENTER -> 1
                        TextAlign.END -> 2
                    },
                    onSelect = { index ->
                        viewModel.updateText {
                            it.copy(
                                align = when (index) {
                                    1 -> TextAlign.CENTER
                                    2 -> TextAlign.END
                                    else -> TextAlign.START
                                }
                            )
                        }
                    }
                )
            }
        }

        Panel.SPACING -> if (text != null) {

            PanelSurface(title = "Oraliqlar") {

                Column {

                    ValueSlider(
                        label = "Qatorlar oralig'i",
                        value = text.lineHeightMultiplier,
                        range = 0.8f..3f,
                        format = { String.format("%.2f×", it) },
                        onChange = { value ->
                            viewModel.updateText {
                                it.copy(lineHeightMultiplier = value)
                            }
                        }
                    )

                    ValueSlider(
                        label = "Harflar oralig'i",
                        value = text.letterSpacingMm,
                        range = -0.5f..3f,
                        format = { String.format("%.2f mm", it) },
                        onChange = { value ->
                            viewModel.updateText {
                                it.copy(letterSpacingMm = value)
                            }
                        }
                    )
                }
            }
        }

        Panel.OPACITY -> {

            val layer = viewModel.selectedLayer

            if (layer != null) {

                PanelSurface(title = "Shaffoflik") {

                    ValueSlider(
                        label = "Ko'rinish",
                        value = layer.transform.opacity,
                        range = 0.05f..1f,
                        format = { "${(it * 100).roundToInt()}%" },
                        onChange = viewModel::setOpacity
                    )
                }
            }
        }

        Panel.SHAPE -> if (shape != null) {

            PanelSurface(title = "Shakl turi") {

                Column {

                    SegmentedRow(
                        options = listOf(
                            "To'rtburchak", "Aylana", "Uchburchak", "Chiziq"
                        ),
                        selectedIndex = ShapeKind.entries.indexOf(shape.kind),
                        onSelect = { index ->
                            viewModel.updateShape {
                                it.copy(kind = ShapeKind.entries[index])
                            }
                        },
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    )

                    if (shape.kind == ShapeKind.RECTANGLE) {

                        Spacer(modifier = Modifier.height(8.dp))

                        ValueSlider(
                            label = "Burchak yumaloqligi",
                            value = shape.cornerRadiusMm,
                            range = 0f..15f,
                            format = { it.mm() },
                            onChange = { value ->
                                viewModel.updateShape {
                                    it.copy(cornerRadiusMm = value)
                                }
                            }
                        )
                    }

                    if (shape.kind == ShapeKind.LINE) {

                        Spacer(modifier = Modifier.height(8.dp))

                        ValueSlider(
                            label = "Qalinlik",
                            value = shape.strokeWidthMm,
                            range = 0.2f..10f,
                            format = { it.mm() },
                            onChange = { value ->
                                viewModel.updateShape {
                                    it.copy(
                                        strokeWidthMm = value,
                                        strokeColor = it.strokeColor
                                            ?: it.fill
                                            ?: Color.Black
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        null -> Unit
    }
}

/**
 * Maket ichida tekislash.
 *
 * Magnit qo'lda surganda yordam beradi, lekin "aniq markazga qo'y"
 * degan buyruqni bitta bosishda bajarish kerak — ayniqsa vizitkada,
 * u yerda markaz eng ko'p ishlatiladigan joy.
 */
@Composable
private fun AlignRow(
    onAlign: (LayerAlignment) -> Unit
) {

    Column {

        Text(
            text = "Maket bo'yicha tekislash",
            fontSize = 12.sp,
            color = MyPrintColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {

            AlignChip("Chap") { onAlign(LayerAlignment.LEFT) }

            AlignChip("Markaz") { onAlign(LayerAlignment.CENTER_X) }

            AlignChip("O'ng") { onAlign(LayerAlignment.RIGHT) }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {

            AlignChip("Tepa") { onAlign(LayerAlignment.TOP) }

            AlignChip("O'rta") { onAlign(LayerAlignment.CENTER_Y) }

            AlignChip("Past") { onAlign(LayerAlignment.BOTTOM) }
        }
    }
}

@Composable
private fun AlignChip(
    label: String,
    onClick: () -> Unit
) {

    Text(
        text = label,
        fontSize = 12.sp,
        color = MyPrintColors.TextPrimary,
        modifier = Modifier
            .background(
                color = MyPrintColors.Surface,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(9.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    )
}

/**
 * Format tanlash.
 *
 * Ikkalasi ham kerak va ular turli maqsad uchun: PDF bosmaxonaga
 * ketadi, PNG esa mijozga ko'rsatish uchun. Foydalanuvchi
 * farqini bilmasligi mumkin, shuning uchun tushuntirish
 * tugmaning yonida turadi, alohida yordam bo'limida emas.
 */
@Composable
private fun ShareFormatDialog(
    onDismiss: () -> Unit,
    onPick: (ShareFormat) -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Maketni yuborish") },
        text = {

            Column {

                ShareOption(
                    title = "PDF — bosmaxona uchun",
                    subtitle = "Matn tiniq chiqadi, o'lcham aniq saqlanadi",
                    onClick = { onPick(ShareFormat.PDF) }
                )

                Spacer(modifier = Modifier.height(6.dp))

                ShareOption(
                    title = "PNG — ko'rsatish uchun",
                    subtitle = "Telegramda darhol ochiladi",
                    onClick = { onPick(ShareFormat.PNG) }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Bekor qilish", color = MyPrintColors.TextSecondary)
            }
        }
    )
}

@Composable
private fun ShareOption(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MyPrintColors.Background,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {

        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = MyPrintColors.TextPrimary
        )

        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = MyPrintColors.TextSecondary
        )
    }
}

@Composable
private fun TextEditDialog(
    initial: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {

    var value by remember { mutableStateOf(initial) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Matnni tahrirlash") },
        text = {

            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.fillMaxWidth(),

                // Bir qatorli emas: vizitkada manzil va telefon
                // ko'pincha bir necha qatorda yoziladi.
                singleLine = false,
                minLines = 2,
                maxLines = 6
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(value) }) {
                Text("Saqlash", color = MyPrintColors.Primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Bekor qilish", color = MyPrintColors.TextSecondary)
            }
        }
    )
}

private fun centered(
    documentWidthMm: Float,
    documentHeightMm: Float,
    widthMm: Float,
    heightMm: Float
) = LayerTransform(
    xMm = (documentWidthMm - widthMm) / 2f,
    yMm = (documentHeightMm - heightMm) / 2f,
    widthMm = widthMm,
    heightMm = heightMm
)

/**
 * Ustki panel.
 *
 * Qulf va magnit aynan shu yerda turibdi, chunki ular butun
 * tahrirlash rejimini o'zgartiradi — bitta elementning uslubi
 * emas. Photoshop'da ham bu tugmalar tepada, doim ko'z oldida.
 */
@Composable
private fun TopBar(
    productName: String,
    sizeLabel: String,
    canUndo: Boolean,
    canRedo: Boolean,
    aspectLocked: Boolean,
    snapEnabled: Boolean,
    onToggleAspect: () -> Unit,
    onToggleSnap: () -> Unit,
    onShareClick: () -> Unit,
    onBackClick: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onDoneClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MyPrintColors.Surface)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        BarIcon(
            icon = Icons.AutoMirrored.Rounded.ArrowBack,
            tint = MyPrintColors.TextPrimary,
            onClick = onBackClick
        )

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = productName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary,
                maxLines = 1
            )

            Text(
                text = sizeLabel,
                fontSize = 11.sp,
                color = MyPrintColors.TextSecondary,
                maxLines = 1
            )
        }

        // Proporsiya qulfi. O'CHIQ holatda har bir nuqta o'z
        // o'lchamini mustaqil o'zgartiradi.
        BarIcon(
            icon = if (aspectLocked) Icons.Rounded.Lock
            else Icons.Rounded.LockOpen,
            tint = if (aspectLocked) MyPrintColors.Primary
            else MyPrintColors.IconSecondary,
            onClick = onToggleAspect
        )

        BarIcon(
            icon = Icons.Rounded.GridOn,
            tint = if (snapEnabled) MyPrintColors.Primary
            else MyPrintColors.IconSecondary,
            onClick = onToggleSnap
        )

        BarIcon(
            icon = Icons.Rounded.IosShare,
            tint = MyPrintColors.TextPrimary,
            onClick = onShareClick
        )

        BarIcon(
            icon = Icons.Rounded.Undo,
            tint = if (canUndo) MyPrintColors.TextPrimary
            else MyPrintColors.IconSecondary,
            onClick = onUndo
        )

        BarIcon(
            icon = Icons.Rounded.Redo,
            tint = if (canRedo) MyPrintColors.TextPrimary
            else MyPrintColors.IconSecondary,
            onClick = onRedo
        )

        BarIcon(
            icon = Icons.Rounded.Check,
            tint = MyPrintColors.Primary,
            onClick = onDoneClick
        )
    }
}

@Composable
private fun SafeAreaWarning(count: Int) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFEF3C7))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Rounded.WarningAmber,
            contentDescription = null,
            tint = Color(0xFF92400E),
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "$count ta element xavfsiz maydondan chiqqan",
            fontSize = 12.sp,
            color = Color(0xFF92400E)
        )
    }
}

@Composable
private fun SelectionBar(
    isText: Boolean,
    isShape: Boolean,
    activePanel: Panel?,
    onPanel: (Panel) -> Unit,
    onEditText: () -> Unit,
    onDuplicate: () -> Unit,
    onForward: () -> Unit,
    onBackward: () -> Unit,
    onDelete: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MyPrintColors.Background)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 6.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (isText) {

            ToolItem(Icons.Rounded.Edit, "Tahrir", onClick = onEditText)

            ToolItem(
                Icons.Rounded.TextFields, "Shrift",
                isActive = activePanel == Panel.FONT,
                onClick = { onPanel(Panel.FONT) }
            )

            ToolItem(
                Icons.Rounded.FormatSize, "Hajm",
                isActive = activePanel == Panel.SIZE,
                onClick = { onPanel(Panel.SIZE) }
            )

            ToolItem(
                Icons.Rounded.FormatAlignCenter, "Tekislash",
                isActive = activePanel == Panel.ALIGN,
                onClick = { onPanel(Panel.ALIGN) }
            )

            ToolItem(
                Icons.Rounded.Height, "Oraliq",
                isActive = activePanel == Panel.SPACING,
                onClick = { onPanel(Panel.SPACING) }
            )
        }

        if (isShape) {

            ToolItem(
                Icons.Rounded.Circle, "Shakl",
                isActive = activePanel == Panel.SHAPE,
                onClick = { onPanel(Panel.SHAPE) }
            )
        }

        // Har qanday element uchun ishlaydi — matn ham, shakl ham.
        ToolItem(
            Icons.Rounded.Straighten, "O'lcham",
            isActive = activePanel == Panel.DIMENSIONS,
            onClick = { onPanel(Panel.DIMENSIONS) }
        )

        ToolItem(
            Icons.Rounded.FormatColorFill, "Rang",
            isActive = activePanel == Panel.COLOR,
            onClick = { onPanel(Panel.COLOR) }
        )

        ToolItem(
            Icons.Rounded.Opacity, "Shaffof",
            isActive = activePanel == Panel.OPACITY,
            onClick = { onPanel(Panel.OPACITY) }
        )

        ToolItem(Icons.Rounded.ContentCopy, "Nusxa", onClick = onDuplicate)

        ToolItem(Icons.Rounded.ArrowForward, "Oldinga", onClick = onForward)

        ToolItem(
            Icons.AutoMirrored.Rounded.ArrowBack, "Orqaga",
            onClick = onBackward
        )

        ToolItem(
            Icons.Rounded.Delete, "O'chirish",
            tint = MyPrintColors.Error,
            onClick = onDelete
        )
    }
}

@Composable
private fun AddBar(
    activePanel: Panel?,
    onAddText: () -> Unit,
    onAddShape: () -> Unit,
    onBackground: () -> Unit,
    layerCount: Int,
    isLayersOpen: Boolean,
    onLayers: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MyPrintColors.Surface)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        ToolItem(Icons.Rounded.TextFields, "Matn", onClick = onAddText)

        ToolItem(Icons.Rounded.Crop169, "Shakl", onClick = onAddShape)

        ToolItem(
            Icons.Rounded.Wallpaper, "Fon",
            isActive = activePanel == Panel.BACKGROUND,
            onClick = onBackground
        )

        ToolItem(
            Icons.Rounded.Layers,
            if (layerCount > 0) "Sloylar ($layerCount)" else "Sloylar",
            isActive = isLayersOpen,
            onClick = onLayers
        )
    }
}

@Composable
private fun ToolItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isActive: Boolean = false,
    tint: Color = MyPrintColors.TextPrimary
) {

    val color = if (isActive) MyPrintColors.Primary else tint

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = label, fontSize = 11.sp, color = color)
    }
}

@Composable
private fun ToggleChip(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {

    Text(
        text = label,
        fontSize = 13.sp,
        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
        color = if (isActive) Color.White else MyPrintColors.TextPrimary,
        modifier = Modifier
            .background(
                color = if (isActive) MyPrintColors.Primary
                else MyPrintColors.Surface,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 9.dp)
    )
}

@Composable
private fun BarIcon(
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
        modifier = Modifier
            .size(36.dp)
            .clickable { onClick() }
            .padding(7.dp)
    )
}

/** 90.0 -> "90",  1.5 -> "1.5" */
private fun Float.clean(): String =
    if (this == this.roundToInt().toFloat()) this.roundToInt().toString()
    else String.format("%.1f", this)