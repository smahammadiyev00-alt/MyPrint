package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.ChangeHistory
import androidx.compose.material.icons.rounded.Crop169
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.HorizontalRule
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Wallpaper
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.feature.feature.design.studio.domain.ShapeKind

/** Menyudan tanlanadigan narsalar. */
sealed interface AddAction {

    data object Text : AddAction

    /** Galereya — tez, faqat rasm va fotolar. */
    data object ImageFromGallery : AddAction

    /**
     * Fayl tizimi — Yuklamalar, Telegram papkasi, Google Drive.
     *
     * Galereya faqat "media" deb belgilangan rasmlarni ko'rsatadi.
     * Telegramdan yuklab olingan logotip yoki Drive'dagi fayl esa
     * u yerda ko'rinmaydi — foydalanuvchi uchun bu tushunarsiz
     * holat, chunki fayl telefonida turibdi.
     */
    data object ImageFromFiles : AddAction

    data class Shape(val kind: ShapeKind) : AddAction

    data object Background : AddAction

    /** Tayyor maket — mavjud qatlamlarni almashtiradi. */
    data object Template : AddAction
}

/**
 * QO'SHISH MENYUSI.
 *
 * Bitta "+" tugmasi ostiga yig'ilgan. Avval pastki qatorda to'rtta
 * alohida tugma turardi va har yangi imkoniyat qo'shilganda joy
 * yetmay borardi — telefonda gorizontal joy cheklangan.
 *
 * Menyu esa cheksiz kengayadi: stikerlar, QR kod, shablonlar
 * keyinchalik shu yerga qo'shiladi va pastki qator o'zgarishsiz
 * qoladi.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMenuSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onPick: (AddAction) -> Unit
) {

    if (!visible) return

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

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
                text = "Nima qo'shamiz?",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Birinchi o'rinda: telefonda noldan dizayn qilish
            // qiyin, ko'pchilik uchun eng to'g'ri boshlanish shu.
            MenuRow(
                icon = Icons.Rounded.Dashboard,
                title = "Tayyor maket",
                subtitle = "Tanlang va matnini almashtiring",
                onClick = { onPick(AddAction.Template) }
            )

            MenuRow(
                icon = Icons.Rounded.TextFields,
                title = "Matn",
                subtitle = "Ism, telefon, manzil",
                onClick = { onPick(AddAction.Text) }
            )

            MenuRow(
                icon = Icons.Rounded.PhotoLibrary,
                title = "Galereyadan rasm",
                subtitle = "Fotolar va suratlar",
                onClick = { onPick(AddAction.ImageFromGallery) }
            )

            MenuRow(
                icon = Icons.Rounded.Folder,
                title = "Fayldan tanlash",
                subtitle = "Yuklamalar, Telegram, Drive",
                onClick = { onPick(AddAction.ImageFromFiles) }
            )

            MenuRow(
                icon = Icons.Rounded.Wallpaper,
                title = "Orqa fon rangi",
                onClick = { onPick(AddAction.Background) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Shakllar",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MyPrintColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                ShapeTile(
                    icon = Icons.Rounded.Crop169,
                    label = "To'rtburchak",
                    onClick = { onPick(AddAction.Shape(ShapeKind.RECTANGLE)) }
                )

                ShapeTile(
                    icon = Icons.Rounded.Circle,
                    label = "Aylana",
                    onClick = { onPick(AddAction.Shape(ShapeKind.ELLIPSE)) }
                )

                ShapeTile(
                    icon = Icons.Rounded.ChangeHistory,
                    label = "Uchburchak",
                    onClick = { onPick(AddAction.Shape(ShapeKind.TRIANGLE)) }
                )

                ShapeTile(
                    icon = Icons.Rounded.HorizontalRule,
                    label = "Chiziq",
                    onClick = { onPick(AddAction.Shape(ShapeKind.LINE)) }
                )
            }
        }
    }
}

@Composable
private fun MenuRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    subtitle: String? = null
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(Color(0xFFF1EEFF)),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MyPrintColors.Primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(13.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MyPrintColors.TextPrimary
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
private fun ShapeTile(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MyPrintColors.Background)
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 10.dp)
            .width(58.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MyPrintColors.TextPrimary,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            fontSize = 10.sp,
            color = MyPrintColors.TextSecondary,
            maxLines = 1
        )
    }
}