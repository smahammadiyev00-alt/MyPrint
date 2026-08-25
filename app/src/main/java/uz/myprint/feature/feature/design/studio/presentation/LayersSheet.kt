package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.feature.feature.design.studio.domain.DesignLayer
import uz.myprint.feature.feature.design.studio.domain.ImageLayer
import uz.myprint.feature.feature.design.studio.domain.ShapeKind
import uz.myprint.feature.feature.design.studio.domain.ShapeLayer
import uz.myprint.feature.feature.design.studio.domain.TextLayer

/**
 * Sloylar paneli.
 *
 * Ro'yxat teskari tartibda ko'rsatiladi: modelda oxirgi qatlam eng
 * ustda chiziladi, panelda esa u birinchi qatorda turishi kerak.
 * Foydalanuvchi "ustki" deganda ro'yxatning tepasini kutadi.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayersSheet(
    viewModel: DesignEditorViewModel,
    onDismiss: () -> Unit
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MyPrintColors.Surface
    ) {

        val layers = viewModel.document.layers

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {

            Text(
                text = "Sloylar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tepadagisi eng ustda chiziladi",
                fontSize = 12.sp,
                color = MyPrintColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (layers.isEmpty()) {

                Text(
                    text = "Hali element qo'shilmagan",
                    fontSize = 13.sp,
                    color = MyPrintColors.TextSecondary,
                    modifier = Modifier.padding(vertical = 24.dp)
                )

            } else {

                LazyColumn(
                    modifier = Modifier.heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    // Teskari tartib: ro'yxatning tepasi = ustki qatlam.
                    itemsIndexed(
                        items = layers.reversed(),
                        key = { _, layer -> layer.id }
                    ) { index, layer ->

                        LayerRow(
                            layer = layer,
                            isSelected = layer.id == viewModel.selectedLayerId,
                            canMoveUp = index != 0,
                            canMoveDown = index != layers.lastIndex,
                            onSelect = { viewModel.selectFromPanel(layer.id) },
                            onUp = { viewModel.moveLayer(layer.id, up = true) },
                            onDown = { viewModel.moveLayer(layer.id, up = false) },
                            onToggleVisibility = {
                                viewModel.toggleVisibility(layer.id)
                            },
                            onToggleLock = { viewModel.toggleLock(layer.id) },
                            onDuplicate = { viewModel.duplicateLayer(layer.id) },
                            onDelete = { viewModel.deleteLayer(layer.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LayerRow(
    layer: DesignLayer,
    isSelected: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onSelect: () -> Unit,
    onUp: () -> Unit,
    onDown: () -> Unit,
    onToggleVisibility: () -> Unit,
    onToggleLock: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {

    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (isSelected) MyPrintColors.Primary.copy(alpha = 0.08f)
                else MyPrintColors.Background
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) MyPrintColors.Primary
                else MyPrintColors.Border,
                shape = shape
            )
            .clickable { onSelect() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            LayerSwatch(layer)

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = layer.previewLabel(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (layer.isVisible) MyPrintColors.TextPrimary
                    else MyPrintColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = layer.typeLabel(),
                    fontSize = 11.sp,
                    color = MyPrintColors.TextSecondary
                )
            }

            RowIcon(
                icon = if (layer.isVisible) Icons.Rounded.Visibility
                else Icons.Rounded.VisibilityOff,
                tint = if (layer.isVisible) MyPrintColors.TextPrimary
                else MyPrintColors.IconSecondary,
                onClick = onToggleVisibility
            )

            RowIcon(
                icon = if (layer.isLocked) Icons.Rounded.Lock
                else Icons.Rounded.LockOpen,
                tint = if (layer.isLocked) MyPrintColors.Primary
                else MyPrintColors.IconSecondary,
                onClick = onToggleLock
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            RowIcon(
                icon = Icons.Rounded.KeyboardArrowUp,
                tint = if (canMoveUp) MyPrintColors.TextPrimary
                else MyPrintColors.IconSecondary,
                onClick = { if (canMoveUp) onUp() }
            )

            RowIcon(
                icon = Icons.Rounded.KeyboardArrowDown,
                tint = if (canMoveDown) MyPrintColors.TextPrimary
                else MyPrintColors.IconSecondary,
                onClick = { if (canMoveDown) onDown() }
            )

            RowIcon(
                icon = Icons.Rounded.ContentCopy,
                tint = MyPrintColors.TextPrimary,
                onClick = onDuplicate
            )

            RowIcon(
                icon = Icons.Rounded.Delete,
                tint = MyPrintColors.Error,
                onClick = onDelete
            )
        }
    }
}

/** Qatlam turini bir qarashda ko'rsatadigan kichik kvadrat. */
@Composable
private fun LayerSwatch(layer: DesignLayer) {

    val color = when (layer) {
        is ShapeLayer -> layer.fill ?: layer.strokeColor ?: Color.Gray
        is TextLayer -> layer.color
        is ImageLayer -> Color(0xFFE5E7EB)
    }

    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .border(1.dp, MyPrintColors.Border, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {

        if (layer is TextLayer) {

            Text(
                text = "A",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (layer.color.luminanceIsDark()) Color.White
                else Color.Black
            )
        }
    }
}

@Composable
private fun RowIcon(
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(7.dp)
    )
}

/**
 * Ro'yxatdagi nom. Matn uchun matnning o'zi ko'rsatiladi —
 * "Matn 1", "Matn 2" degan nomlar hech narsa aytmaydi, mijoz esa
 * "Telefon raqami" qatorini darhol topishi kerak.
 */
private fun DesignLayer.previewLabel(): String = when (this) {

    is TextLayer -> text
        .replace("\n", " ")
        .trim()
        .ifBlank { "Bo'sh matn" }
        .take(28)

    is ShapeLayer -> when (kind) {
        ShapeKind.RECTANGLE -> "To'rtburchak"
        ShapeKind.ELLIPSE -> "Aylana"
        ShapeKind.TRIANGLE -> "Uchburchak"
        ShapeKind.LINE -> "Chiziq"
    }

    is ImageLayer -> name
}

private fun DesignLayer.typeLabel(): String {

    val size = "${transform.widthMm.toInt()} × " +
            "${transform.heightMm.toInt()} mm"

    return when (this) {
        is TextLayer -> "Matn · $size"
        is ShapeLayer -> "Shakl · $size"
        is ImageLayer -> "Rasm · $size"
    }
}

/** Oq yoki qora harf qo'yishni hal qilish uchun. */
private fun Color.luminanceIsDark(): Boolean =
    (0.299f * red + 0.587f * green + 0.114f * blue) < 0.6f
