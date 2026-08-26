package uz.myprint.feature.feature.project.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DriveFileRenameOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import uz.myprint.feature.feature.project.model.Project

/**
 * Loyiha kartasidagi uch nuqta menyusi.
 *
 * O'chirish alohida tasdiqlash so'raydi va bu ataylab shunday:
 * loyiha — foydalanuvchining bir necha soatlik mehnati, uni
 * tasodifan bosib yo'qotish og'ir yo'qotish bo'ladi. Undo esa
 * yo'q, chunki fayl diskdan o'chiriladi.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectActionsSheet(
    project: Project?,
    onDismiss: () -> Unit,
    onOpen: (Project) -> Unit,
    onRename: (Project, String) -> Unit,
    onDuplicate: (Project) -> Unit,
    onDelete: (Project) -> Unit
) {

    if (project == null) return

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var renaming by remember(project.id) { mutableStateOf(false) }

    var confirmingDelete by remember(project.id) { mutableStateOf(false) }

    // Nomni o'zgartirish va o'chirish tasdig'i menyudan
    // TASHQARIDA turadi: menyu yopilib, dialog ochiladi. Sheet
    // ichida dialog ochish Compose'da ba'zi qurilmalarda
    // ko'rinmay qoladi.
    if (renaming) {

        RenameDialog(
            initial = project.title,
            onDismiss = {
                renaming = false
                onDismiss()
            },
            onConfirm = { name ->
                renaming = false
                onRename(project, name)
                onDismiss()
            }
        )

        return
    }

    if (confirmingDelete) {

        DeleteConfirmDialog(
            title = project.title,
            onDismiss = {
                confirmingDelete = false
                onDismiss()
            },
            onConfirm = {
                confirmingDelete = false
                onDelete(project)
                onDismiss()
            }
        )

        return
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MyPrintColors.Surface
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 28.dp)
        ) {

            Text(
                text = project.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = project.updatedAt,
                fontSize = 12.sp,
                color = MyPrintColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            ActionRow(
                icon = Icons.Rounded.Edit,
                title = "Tahrirlashni davom ettirish",
                onClick = {
                    onOpen(project)
                    onDismiss()
                }
            )

            ActionRow(
                icon = Icons.Rounded.DriveFileRenameOutline,
                title = "Nomini o'zgartirish",
                onClick = { renaming = true }
            )

            ActionRow(
                icon = Icons.Rounded.ContentCopy,
                title = "Nusxa olish",
                subtitle = "Asl maket saqlanadi, nusxasi tahrirlanadi",
                onClick = {
                    onDuplicate(project)
                    onDismiss()
                }
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MyPrintColors.Border)
            )

            ActionRow(
                icon = Icons.Rounded.Delete,
                title = "O'chirish",
                tint = MyPrintColors.Error,
                onClick = { confirmingDelete = true }
            )
        }
    }
}

@Composable
private fun RenameDialog(
    initial: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {

    var value by remember { mutableStateOf(initial) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Loyiha nomi") },
        text = {

            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {

            TextButton(
                onClick = { onConfirm(value.trim().ifBlank { initial }) }
            ) {
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

/**
 * O'chirishni tasdiqlash.
 *
 * Tugma matni "Ha" emas, "O'chirish" — foydalanuvchi nimani
 * tasdiqlayotganini tugmaning o'zidan ko'rib tursin. Shoshib
 * bosilganda ham nima bo'lishi aniq bo'ladi.
 */
@Composable
private fun DeleteConfirmDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Loyiha o'chirilsinmi?") },
        text = {
            Text(
                "\"$title\" butunlay o'chiriladi. " +
                        "Bu amalni ortga qaytarib bo'lmaydi."
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("O'chirish", color = MyPrintColors.Error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Bekor qilish", color = MyPrintColors.TextSecondary)
            }
        }
    )
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    subtitle: String? = null,
    tint: Color = MyPrintColors.TextPrimary
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(text = title, fontSize = 15.sp, color = tint)

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
