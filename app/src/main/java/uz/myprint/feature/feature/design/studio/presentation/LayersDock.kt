package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.myprint.core.di.AppContainer
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.feature.feature.design.studio.domain.DesignLayer
import uz.myprint.feature.feature.design.studio.domain.ImageLayer
import uz.myprint.feature.feature.design.studio.domain.ShapeKind
import uz.myprint.feature.feature.design.studio.domain.ShapeLayer
import uz.myprint.feature.feature.design.studio.domain.TextLayer
import kotlin.math.roundToInt

/**
 * SLOYLAR DOKI.
 *
 * ModalBottomSheet'dan voz kechildi. Sababi oddiy: modal oyna
 * ostidagi hamma narsani bloklaydi, shuning uchun sloyni tanlab
 * kanvasda natijani ko'rish mumkin emas edi — panelni yopish,
 * qarash, yana ochish kerak bo'lardi. Photoshop'da panel doim
 * ochiq turadi va ish shundan tezlashadi.
 *
 * Bu Composable oddiy Column bo'lagi: DesignStudioScreen'dagi
 * ustunga qo'yiladi va kanvasning balandligini kamaytiradi,
 * ustiga tushmaydi. Kanvas kichrayadi, lekin ISHLAYDI.
 *
 * Balandligi tutqichdan suriladi — ro'yxat uzun bo'lsa kattalashtirib,
 * kanvasga qarash kerak bo'lsa kichraytirib olish mumkin.
 */
@Composable
fun LayersDock(
    viewModel: DesignEditorViewModel,
    modifier: Modifier = Modifier
) {

    val density = LocalDensity.current

    val minHeightPx = with(density) { 132.dp.toPx() }

    val maxHeightPx = with(density) { 420.dp.toPx() }

    var heightPx by remember {
        mutableFloatStateOf(with(density) { 220.dp.toPx() })
    }

    val layers = viewModel.document.layers

    val selectedId = viewModel.selectedLayerId

    val listState = rememberLazyListState()

    // Kanvasda element tanlansa, ro'yxat o'sha joyga suriladi.
    // Ikki yo'nalishli bog'lanish — panelning asosiy foydasi shu.
    LaunchedEffect(selectedId, layers.size) {

        val id = selectedId ?: return@LaunchedEffect

        val index = layers.reversed().indexOfFirst { it.id == id }

        if (index >= 0) listState.animateScrollToItem(index)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(with(density) { heightPx.toDp() })
            .background(
                color = MyPrintColors.Surface,
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
            )
            .border(
                width = 1.dp,
                color = MyPrintColors.Border,
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
            )
    ) {

        // ---- tutqich: sudrab balandlikni o'zgartirish ----
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        heightPx = (heightPx - dragAmount)
                            .coerceIn(minHeightPx, maxHeightPx)
                    }
                },
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .width(38.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MyPrintColors.Border)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Sloylar",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${layers.size} ta",
                fontSize = 12.sp,
                color = MyPrintColors.TextSecondary
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Yopish",
                tint = MyPrintColors.TextSecondary,
                modifier = Modifier
                    .size(34.dp)
                    .clickable { viewModel.showLayersPanel(false) }
                    .padding(7.dp)
            )
        }

        if (layers.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Hali element qo'shilmagan",
                    fontSize = 13.sp,
                    color = MyPrintColors.TextSecondary
                )
            }

            return@Column
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 10.dp, end = 10.dp, bottom = 10.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            // Modelda oxirgi element eng ustda chiziladi, panelda esa
            // u birinchi qatorda turishi kerak.
            val ordered = layers.reversed()

            items(items = ordered, key = { it.id }) { layer ->

                val modelIndex = layers.indexOfFirst { it.id == layer.id }

                LayerRow(
                    layer = layer,
                    isClipped = layer.clipToId != null,
                    groupTint = layer.groupId?.let { groupColor(it) },
                    isSelected = layer.id == selectedId,
                    canMoveUp = modelIndex < layers.lastIndex,
                    canMoveDown = modelIndex > 0,
                    onSelect = { viewModel.selectFromPanel(layer.id) },
                    onToggleVisibility = {
                        viewModel.toggleVisibility(layer.id)
                    },
                    onToggleLock = { viewModel.toggleLock(layer.id) },
                    onMoveUp = { viewModel.moveLayer(layer.id, up = true) },
                    onMoveDown = { viewModel.moveLayer(layer.id, up = false) },
                    onDuplicate = { viewModel.duplicateLayer(layer.id) },
                    onDelete = { viewModel.deleteLayer(layer.id) }
                )
            }
        }
    }
}

/**
 * Bitta qator.
 *
 * Hamma tugma BITTA qatorda. Avvalgi variantda tanlangan element
 * ostida ikkinchi qator ochilardi va ro'yxat sakrab ketardi —
 * ko'z bilan kuzatish qiyin edi. Endi baland 56dp qator va ixcham
 * 30dp tugmalar: barmoq uchun yetarli, joyni tejaydi.
 */
@Composable
private fun LayerRow(
    layer: DesignLayer,
    isClipped: Boolean,
    groupTint: Color?,
    isSelected: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onSelect: () -> Unit,
    onToggleVisibility: () -> Unit,
    onToggleLock: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {

    val background = if (isSelected) Color(0xFFF2EDFF)
    else MyPrintColors.Background

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .then(
                if (isSelected) Modifier.border(
                    width = 1.5.dp,
                    color = MyPrintColors.Primary,
                    shape = RoundedCornerShape(12.dp)
                ) else Modifier
            )
            .clickable { onSelect() }
            .padding(
                // Ichiga qirqilgan qatlam ichkariga surilib
                // ko'rsatiladi — Photoshop'dagidek, bir qarashda
                // ierarxiya ko'rinib turadi.
                start = if (isClipped) 22.dp else 8.dp,
                end = 8.dp,
                top = 6.dp,
                bottom = 6.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Guruh rangli chiziq bilan belgilanadi: bir xil rangdagi
        // qatorlar birga harakatlanadi. Matn bilan yozishdan ko'ra
        // rang tezroq o'qiladi va joy egallamaydi.
        if (groupTint != null) {

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(34.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(groupTint)
            )

            Spacer(modifier = Modifier.width(7.dp))
        }

        if (isClipped) {

            Text(
                text = "\u21B3",
                fontSize = 15.sp,
                color = MyPrintColors.Primary,
                modifier = Modifier.padding(end = 5.dp)
            )
        }

        LayerThumb(layer)

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = layer.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (layer.isVisible) MyPrintColors.TextPrimary
                else MyPrintColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = layerSubtitle(layer),
                fontSize = 11.sp,
                color = MyPrintColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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

        RowIcon(
            icon = Icons.Rounded.KeyboardArrowUp,
            tint = if (canMoveUp) MyPrintColors.TextPrimary
            else MyPrintColors.Border,
            onClick = { if (canMoveUp) onMoveUp() }
        )

        RowIcon(
            icon = Icons.Rounded.KeyboardArrowDown,
            tint = if (canMoveDown) MyPrintColors.TextPrimary
            else MyPrintColors.Border,
            onClick = { if (canMoveDown) onMoveDown() }
        )

        RowIcon(
            icon = Icons.Rounded.ContentCopy,
            tint = MyPrintColors.TextSecondary,
            onClick = onDuplicate
        )

        RowIcon(
            icon = Icons.Rounded.Delete,
            tint = MyPrintColors.Error,
            onClick = onDelete
        )
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
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(6.dp)
    )
}

/**
 * Kichik oldindan ko'rsatkich.
 *
 * Haqiqiy bitmap render qilinmaydi — har kadrda o'nlab kichik
 * bitmap yasash ro'yxatni sekinlashtiradi. Shakl uchun rangi va
 * shakli, matn uchun birinchi harfi ko'rsatiladi: sloyni tanish
 * uchun shuning o'zi yetarli.
 */
@Composable
private fun LayerThumb(layer: DesignLayer) {

    val shape = when {
        layer is ShapeLayer && layer.kind == ShapeKind.ELLIPSE ->
            RoundedCornerShape(50)

        else -> RoundedCornerShape(7.dp)
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(shape)
            .background(
                when (layer) {
                    is ShapeLayer -> layer.fill ?: MyPrintColors.Border
                    is TextLayer -> Color(0xFF1F2937)
                    is ImageLayer -> MyPrintColors.Border
                }
            ),
        contentAlignment = Alignment.Center
    ) {

        when (layer) {

            is TextLayer -> Text(
                text = layer.displayText.take(1).ifBlank { "A" },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            is ImageLayer -> {

                // Rasm qatlamida haqiqiy ko'rinish beriladi:
                // maketda bir necha rasm bo'lsa, "IMG" yozuvi
                // ularni bir-biridan ajratmaydi.
                val thumb = remember(layer.sourceUri) {
                    AppContainer.imageStore.load(layer.sourceUri, maxPx = 96)
                }

                if (thumb != null) {

                    Image(
                        bitmap = thumb,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                } else {

                    Text(
                        text = "IMG",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MyPrintColors.TextSecondary
                    )
                }
            }

            is ShapeLayer -> Unit
        }
    }
}

/**
 * Guruh identifikatoridan barqaror rang.
 *
 * Tasodifiy emas, hash'dan olinadi — shunda qayta chizilganda
 * yoki ilova qayta ishga tushganda rang o'zgarmaydi.
 */
private fun groupColor(groupId: String): Color {

    val palette = listOf(
        Color(0xFF7B4DFF),
        Color(0xFF22C55E),
        Color(0xFFF59E0B),
        Color(0xFF06B6D4),
        Color(0xFFEC4899)
    )

    val index = (groupId.hashCode().toUInt() % palette.size.toUInt()).toInt()

    return palette[index]
}

private fun layerSubtitle(layer: DesignLayer): String {

    val t = layer.transform

    val size = "${t.widthMm.roundToInt()} × ${t.heightMm.roundToInt()} mm"

    val kind = when (layer) {
        is TextLayer -> layer.displayText.take(18)
        is ShapeLayer -> when (layer.kind) {
            ShapeKind.RECTANGLE -> "To'rtburchak"
            ShapeKind.ELLIPSE -> "Aylana"
            ShapeKind.TRIANGLE -> "Uchburchak"
            ShapeKind.LINE -> "Chiziq"
        }

        is ImageLayer -> "Rasm"
    }

    val rotation = if (kotlin.math.abs(t.rotationDeg % 360f) > 0.5f) {
        " · ${t.rotationDeg.roundToInt()}°"
    } else {
        ""
    }

    return "$kind · $size$rotation"
}