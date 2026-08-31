package uz.myprint.feature.feature.design.studio.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uz.myprint.core.designsystem.theme.MyPrintColors
import uz.myprint.feature.feature.design.studio.domain.DesignDocument
import uz.myprint.feature.feature.design.studio.domain.DesignTemplate

/**
 * SHABLON TANLASH.
 *
 * Namunalar RASM emas, jonli chiziladi.
 *
 * Sabab: har shablon uchun oldindan rasm tayyorlash kerak bo'lardi,
 * va shablon o'zgarganda rasmni yangilash unutilib, ro'yxatda bir
 * narsa, kanvasda boshqasi ko'rinardi. Jonli chizishda bunday
 * farq bo'lishi mumkin emas — ikkalasi ham bitta drawDocument
 * funksiyasidan o'tadi.
 *
 * Yon foydasi: namuna foydalanuvchining O'Z o'lchamida chiziladi.
 * 90×50 vizitka tanlagan odam 90×50 namunani ko'radi, 85×55 emas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateSheet(
    visible: Boolean,
    document: DesignDocument,
    templates: List<DesignTemplate>,
    onDismiss: () -> Unit,
    onPick: (DesignTemplate) -> Unit
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
                .padding(bottom = 20.dp)
        ) {

            Text(
                text = "Tayyor maketlar",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MyPrintColors.TextPrimary
            )

            Text(
                text = "Tanlang va matnini o'zingiznikiga almashtiring",
                fontSize = 12.sp,
                color = MyPrintColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (templates.isEmpty()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Bu mahsulot uchun shablon hali yo'q",
                        fontSize = 13.sp,
                        color = MyPrintColors.TextSecondary
                    )
                }

                return@Column
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.heightIn(max = 460.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(items = templates, key = { it.id }) { tpl ->

                    TemplateCard(
                        template = tpl,
                        document = document,
                        onClick = { onPick(tpl) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: DesignTemplate,
    document: DesignDocument,
    onClick: () -> Unit
) {

    // Namuna hujjati bir marta yasaladi. Shablon qurish arzon
    // amal, lekin u ro'yxat aylantirilganda qayta-qayta
    // chaqirilishi mumkin — remember buni oldini oladi.
    val preview = remember(template.id, document.id) {
        document.copy(layers = template.build(document))
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MyPrintColors.Background)
            .clickable { onClick() }
            .padding(8.dp)
    ) {

        TemplatePreview(
            document = preview,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(
                    ratio = (document.widthMm / document.heightMm)
                        .coerceIn(0.4f, 3f)
                )
                .clip(RoundedCornerShape(7.dp))
                .background(Color.White)
                .border(
                    width = 1.dp,
                    color = MyPrintColors.Border,
                    shape = RoundedCornerShape(7.dp)
                )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = template.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MyPrintColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Namunani chizadi.
 *
 * Kanvasdagi bilan bir xil funksiya, lekin yo'riqchilarsiz va
 * tanlash ramkasisiz — namunada ular ortiqcha shovqin bo'lardi.
 */
@Composable
private fun TemplatePreview(
    document: DesignDocument,
    modifier: Modifier = Modifier
) {

    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier) {

        val pxPerMm = minOf(
            size.width / document.widthMm,
            size.height / document.heightMm
        )

        // Bleed ko'rsatilmaydi: namunada faqat tayyor mahsulot
        // ko'rinishi kerak, texnik zaxira maydon emas.
        val geometry = CanvasGeometry(
            document = document,
            pxPerMm = pxPerMm,
            originPx = Offset(
                x = (size.width - document.widthMm * pxPerMm) / 2f -
                        document.bleedMm * pxPerMm,
                y = (size.height - document.heightMm * pxPerMm) / 2f -
                        document.bleedMm * pxPerMm
            )
        )

        drawDocument(
            document = document,
            geometry = geometry,
            textMeasurer = textMeasurer
        )
    }
}
