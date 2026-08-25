package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Crop169
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.feature.feature.design.studio.domain.LayerTransform
import uz.myprint.feature.feature.design.studio.domain.ShapeLayer
import uz.myprint.feature.feature.design.studio.domain.TextLayer
import kotlin.math.roundToInt

@Composable
fun DesignStudioScreen(
    viewModel: DesignEditorViewModel,
    productName: String,
    onBackClick: () -> Unit = {},
    onDoneClick: () -> Unit = {}
) {

    val document = viewModel.document

    val outsideSafeArea = document.layersOutsideSafeArea()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MyPrintColors.Surface)
    ) {

        TopBar(
            productName = productName,
            sizeLabel = "${document.widthMm.clean()} × " +
                    "${document.heightMm.clean()} mm · 300 DPI",
            canUndo = viewModel.canUndo,
            canRedo = viewModel.canRedo,
            onBackClick = onBackClick,
            onUndo = viewModel::undo,
            onRedo = viewModel::redo,
            onDoneClick = onDoneClick
        )

        DesignCanvas(
            viewModel = viewModel,
            modifier = Modifier.weight(1f)
        )

        if (outsideSafeArea.isNotEmpty()) {

            SafeAreaWarning(count = outsideSafeArea.size)
        }

        // Tanlov paneli qo'shish panelini almashtirmaydi, ustiga
        // chiqadi — foydalanuvchi tanlovni bekor qilmasdan yangi
        // element qo'sha olishi kerak.
        if (viewModel.selectedLayer != null) {

            SelectionBar(
                onDuplicate = viewModel::duplicateSelected,
                onForward = viewModel::bringForward,
                onBackward = viewModel::sendBackward,
                onDelete = viewModel::deleteSelected
            )
        }

        AddBar(
            onAddText = {

                viewModel.addLayer(
                    TextLayer(
                        id = DesignEditorViewModel.newId(),
                        transform = centeredTransform(
                            document.widthMm,
                            document.heightMm,
                            widthMm = document.widthMm * 0.6f,
                            heightMm = 10f
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
                        transform = centeredTransform(
                            document.widthMm,
                            document.heightMm,
                            widthMm = document.widthMm * 0.4f,
                            heightMm = document.heightMm * 0.25f
                        ),
                        cornerRadiusMm = 1.5f
                    )
                )
            }
        )
    }
}

/** Yangi element maketning o'rtasida paydo bo'ladi. */
private fun centeredTransform(
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

@Composable
private fun TopBar(
    productName: String,
    sizeLabel: String,
    canUndo: Boolean,
    canRedo: Boolean,
    onBackClick: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onDoneClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MyPrintColors.Surface)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        BarIcon(
            icon = Icons.AutoMirrored.Rounded.ArrowBack,
            tint = MyPrintColors.TextPrimary,
            onClick = onBackClick
        )

        Spacer(modifier = Modifier.width(4.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = productName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary
            )

            Text(
                text = sizeLabel,
                fontSize = 12.sp,
                color = MyPrintColors.TextSecondary
            )
        }

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
            text = if (count == 1) "1 ta element xavfsiz maydondan chiqqan"
            else "$count ta element xavfsiz maydondan chiqqan",
            fontSize = 12.sp,
            color = Color(0xFF92400E)
        )
    }
}

@Composable
private fun SelectionBar(
    onDuplicate: () -> Unit,
    onForward: () -> Unit,
    onBackward: () -> Unit,
    onDelete: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MyPrintColors.Background)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        ToolItem(
            icon = Icons.Rounded.ContentCopy,
            label = "Nusxa",
            onClick = onDuplicate
        )

        ToolItem(
            icon = Icons.Rounded.ArrowForward,
            label = "Oldinga",
            onClick = onForward
        )

        ToolItem(
            icon = Icons.AutoMirrored.Rounded.ArrowBack,
            label = "Orqaga",
            onClick = onBackward
        )

        ToolItem(
            icon = Icons.Rounded.Delete,
            label = "O'chirish",
            tint = MyPrintColors.Error,
            onClick = onDelete
        )
    }
}

@Composable
private fun AddBar(
    onAddText: () -> Unit,
    onAddShape: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MyPrintColors.Surface)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        ToolItem(
            icon = Icons.Rounded.TextFields,
            label = "Matn",
            onClick = onAddText
        )

        ToolItem(
            icon = Icons.Rounded.Crop169,
            label = "Shakl",
            onClick = onAddShape
        )
    }
}

@Composable
private fun ToolItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tint: Color = MyPrintColors.TextPrimary
) {

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            color = tint
        )
    }
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
            .size(38.dp)
            .clickable { onClick() }
            .padding(8.dp)
    )
}

/** 90.0 -> "90",  1.5 -> "1.5" */
private fun Float.clean(): String =
    if (this == this.roundToInt().toFloat()) this.roundToInt().toString()
    else String.format("%.1f", this)
