package uz.myprint.feature.feature.project.components

import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.io.File

/**
 * Muqova uchun eng katta tomon, piksel.
 *
 * Karta 285dp kenglikda — hatto eng zich ekranda ham 1200 px dan
 * ortiq kerak emas. Bundan kattasi xotirani behuda yeydi.
 */
private const val MAX_COVER_PX = 1200

@Composable
fun ProjectCover(
    @DrawableRes imageRes: Int,
    badgeText: String,
    badgeColor: Color,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,

    /** Studio chizgan muqova. null bo'lsa imageRes ishlatiladi. */
    coverPath: String? = null
) {

    // remember tufayli har qayta chizishda diskdan o'qilmaydi.
    val cover = remember(coverPath) { decodeCover(coverPath) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(135.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp
                )
            )
            // Vizitka oq fonli bo'ladi, karta ham oq — orasida
            // chegara ko'rinishi uchun ochiq kulrang tag beriladi.
            .background(Color(0xFFEDEFF5))
    ) {

        if (cover != null) {

            Image(
                bitmap = cover,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),

                // Crop emas, Fit: maket kesilib ketmasligi kerak,
                // foydalanuvchi o'z ishini butunligicha ko'rsin.
                contentScale = ContentScale.Fit
            )

        } else {

            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.28f)
                        )
                    )
                )
        )

        ProjectMenuButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
            onClick = onMenuClick
        )

        ProjectBadge(
            text = badgeText,
            Color.Black.copy(alpha = 0.20f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        )
    }
}

/**
 * Muqovani KICHRAYTIRIB o'qiydi.
 *
 * Bu shunchaki tejamkorlik emas, qulashdan himoya. Android'da
 * Canvas bitta bitmapni ~100 MB gacha chiza oladi. Fayl qandaydir
 * sabab bilan katta bo'lib qolsa — eski versiyadan qolgan bo'lsa,
 * yoki ulkan banner maketidan chizilgan bo'lsa — uni to'liq o'qish
 * ilovani darhol yiqitadi. Eng yomoni: qulash BOSH SAHIFADA
 * yuz beradi, ya'ni foydalanuvchi ilovaga umuman kira olmaydi va
 * yagona chora ilovani o'chirib tashlash bo'lib qoladi.
 *
 * Ikki bosqichli o'qish: avval faqat O'LCHAMI olinadi (piksellar
 * xotiraga yuklanmaydi), keyin kerakli darajada siyraklashtirib
 * haqiqiy o'qish qilinadi.
 */
private fun decodeCover(path: String?): ImageBitmap? {

    val file = path?.let { File(it) } ?: return null

    if (!file.exists()) return null

    return runCatching {

        val bounds = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeFile(file.absolutePath, bounds)

        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            return@runCatching null
        }

        // inSampleSize faqat 2 ning darajalarini qabul qiladi:
        // 1, 2, 4, 8... Boshqa qiymat berilsa Android eng yaqin
        // pastki darajaga yaxlitlaydi.
        var sample = 1

        while (bounds.outWidth / sample > MAX_COVER_PX ||
            bounds.outHeight / sample > MAX_COVER_PX
        ) {
            sample *= 2
        }

        val options = BitmapFactory.Options().apply {
            inSampleSize = sample
        }

        BitmapFactory
            .decodeFile(file.absolutePath, options)
            ?.asImageBitmap()

    }.getOrNull()
}