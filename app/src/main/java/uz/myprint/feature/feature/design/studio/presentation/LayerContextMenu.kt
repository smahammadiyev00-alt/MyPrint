package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.automirrored.rounded.CallMerge
import androidx.compose.material.icons.rounded.FilterFrames
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.OpenInFull
import androidx.compose.material.icons.rounded.Workspaces
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import uz.myprint.feature.feature.design.studio.domain.TextLayer
import kotlin.math.roundToInt

/**
 * Uzun bosishda chiqadigan menyu.
 *
 * Bu MODAL bo'lishi to'g'ri — sloylar panelidan farqli o'laroq,
 * bu yerda foydalanuvchi bitta qaror qabul qilib, darhol yopadi.
 * Fon bloklangani halaqit bermaydi, aksincha tasodifiy bosishning
 * oldini oladi.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayerContextMenu(
    viewModel: DesignEditorViewModel,
    onEditText: () -> Unit
) {

    val layerId = viewModel.contextMenuLayerId ?: return

    val layer = viewModel.document.layerById(layerId) ?: return

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // Ichiga joylash nishonini tanlash rejimi.
    var pickingTarget by remember(layerId) { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = viewModel::closeContextMenu,
        sheetState = sheetState,
        containerColor = MyPrintColors.Surface
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {

            Text(
                text = layer.name,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary
            )

            Text(
                text = "${layer.transform.widthMm.roundToInt()} × " +
                        "${layer.transform.heightMm.roundToInt()} mm",
                fontSize = 12.sp,
                color = MyPrintColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (pickingTarget) {

                TargetPicker(
                    targets = viewModel.clipTargetsFor(layerId),
                    onPick = { targetId ->
                        viewModel.clipTo(layerId, targetId)
                        viewModel.closeContextMenu()
                    },
                    onCancel = { pickingTarget = false }
                )

                return@Column
            }

            // ---- ichiga joylash ----

            if (layer.clipToId != null) {

                val targetName = viewModel.document
                    .layerById(layer.clipToId!!)
                    ?.name
                    ?: "element"

                MenuRow(
                    icon = Icons.Rounded.OpenInFull,
                    title = "Ichidan chiqarish",
                    subtitle = "Hozir \"$targetName\" ichida",
                    onClick = {
                        viewModel.clipTo(layerId, null)
                        viewModel.closeContextMenu()
                    }
                )

            } else {

                val targets = viewModel.clipTargetsFor(layerId)

                MenuRow(
                    icon = Icons.Rounded.FilterFrames,
                    title = "Ichiga joylash",
                    subtitle = if (targets.isEmpty()) {
                        "Pastda element yo'q — avval orqaga suring"
                    } else {
                        "${targets.size} ta element mavjud"
                    },
                    enabled = targets.isNotEmpty(),
                    onClick = { pickingTarget = true }
                )
            }

            MenuDivider()

            // ---- guruhlash (Photoshop'da Ctrl+E) ----

            if (layer.groupId != null) {

                val count = viewModel.document
                    .groupMembers(layer.groupId!!)
                    .size

                MenuRow(
                    icon = Icons.Rounded.Workspaces,
                    title = "Guruhni ajratish",
                    subtitle = "$count ta element birga harakatlanmoqda",
                    onClick = {
                        viewModel.ungroup(layerId)
                        viewModel.closeContextMenu()
                    }
                )

            } else {

                val canMerge = viewModel.canMergeDown(layerId)

                MenuRow(
                    icon = Icons.AutoMirrored.Rounded.CallMerge,
                    title = "Pastdagisi bilan birlashtirish",
                    subtitle = if (canMerge) {
                        "Ikkalasi birga suriladi va cho'ziladi"
                    } else {
                        "Bu eng pastdagi element"
                    },
                    enabled = canMerge,
                    onClick = {
                        viewModel.mergeDown(layerId)
                        viewModel.closeContextMenu()
                    }
                )
            }

            MenuDivider()

            // Matn qatlami uchun tahrirlash — uzun bosishdan
            // to'g'ridan-to'g'ri kirish eng qisqa yo'l.
            if (layer is TextLayer) {

                MenuRow(
                    icon = Icons.Rounded.Edit,
                    title = "Matnni tahrirlash",
                    onClick = {
                        viewModel.closeContextMenu()
                        onEditText()
                    }
                )
            }

            MenuRow(
                icon = Icons.Rounded.ContentCopy,
                title = "Nusxa olish",
                onClick = {
                    viewModel.duplicateLayer(layerId)
                    viewModel.closeContextMenu()
                }
            )

            MenuRow(
                icon = if (layer.isLocked) Icons.Rounded.LockOpen
                else Icons.Rounded.Lock,
                title = if (layer.isLocked) "Qulfni ochish" else "Qulflash",
                subtitle = if (layer.isLocked) null
                else "Tasodifan surib yuborishdan saqlaydi",
                onClick = {
                    viewModel.toggleLock(layerId)
                    viewModel.closeContextMenu()
                }
            )

            MenuDivider()

            MenuRow(
                icon = Icons.Rounded.Delete,
                title = "O'chirish",
                tint = MyPrintColors.Error,
                onClick = {
                    viewModel.deleteLayer(layerId)
                    viewModel.closeContextMenu()
                }
            )
        }
    }
}

/**
 * Nishonni tanlash.
 *
 * Ro'yxatda faqat PASTDA turgan qatlamlar bo'ladi. Yuqoridagilar
 * ko'rsatilmaydi, chunki ular allaqachon ustiga chizilgan bo'lardi
 * va "ichiga solish" ko'zga ko'rinmasdi. Buni tushuntirishdan
 * ko'ra, imkoniyatni umuman bermaslik tushunarliroq.
 */
@Composable
private fun TargetPicker(
    targets: List<DesignLayer>,
    onPick: (String) -> Unit,
    onCancel: () -> Unit
) {

    Column(modifier = Modifier.fillMaxWidth()) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = "Qaysi element ichiga?",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MyPrintColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "Bekor",
                fontSize = 13.sp,
                color = MyPrintColors.TextSecondary,
                modifier = Modifier
                    .clickable { onCancel() }
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.heightIn(max = 280.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            items(items = targets, key = { it.id }) { target ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MyPrintColors.Background)
                        .clickable { onPick(target.id) }
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = target.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MyPrintColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "${target.transform.widthMm.roundToInt()} × " +
                                "${target.transform.heightMm.roundToInt()} mm",
                        fontSize = 12.sp,
                        color = MyPrintColors.TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    subtitle: String? = null,
    enabled: Boolean = true,
    tint: Color = MyPrintColors.TextPrimary
) {

    val color = if (enabled) tint else MyPrintColors.IconSecondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = title,
                fontSize = 15.sp,
                color = color
            )

            if (subtitle != null) {

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MyPrintColors.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun MenuDivider() {

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MyPrintColors.Border)
    )
}