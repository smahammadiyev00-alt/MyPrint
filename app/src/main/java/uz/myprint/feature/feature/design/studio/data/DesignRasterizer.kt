package uz.myprint.feature.feature.design.studio.data

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import uz.myprint.feature.feature.design.studio.domain.DesignDocument
import uz.myprint.feature.feature.design.studio.domain.PRINT_PX_PER_MM
import uz.myprint.feature.feature.design.studio.presentation.CanvasGeometry
import uz.myprint.feature.feature.design.studio.presentation.drawDocument
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

/**
 * Maketni rasmga aylantiradi.
 *
 * Ikki xil vazifa uchun ishlatiladi va ular bir-biridan jiddiy
 * farq qiladi:
 *
 *  MUQOVA (preview) — "Loyihalaringiz" ro'yxatidagi kichik rasm.
 *      Kichik, tez, sRGB. Sifat muhim emas.
 *
 *  BOSMA (export)  — bosmaxonaga ketadigan fayl. 300 DPI, bleed
 *      bilan. LEKIN bu sRGB'da chiqadi, CMYK emas.
 *
 * CMYK haqida ochiq gapirish kerak: Android'da rangni ICC profil
 * orqali CMYK'ga aylantiradigan tayyor vosita yo'q. Ekrandagi
 * yorqin binafsha (#7B4DFF) CMYK'da bosilmaydi — u bo'g'iq
 * siyohrangga aylanadi. Shuning uchun bu yerdagi PNG bosmaxonaga
 * YAKUNIY fayl emas, faqat ko'rish va tasdiqlash uchun. Haqiqiy
 * bosma fayli (PDF/X-1a, CMYK) serverda, maket JSON'idan
 * yasalishi kerak.
 */
object DesignRasterizer {

    /** Muqova uchun eng katta tomon, piksel. */
    private const val COVER_MAX_PX = 720

    /**
     * Bitta bitmapdagi eng ko'p piksel soni.
     *
     * Android'da Canvas bitta bitmapni ~100 MB gacha chiza oladi.
     * Har piksel 4 baytdan, demak chegara ~26 million piksel.
     * 16 million zaxira bilan olingan.
     *
     * MUHIM: chegara TOMON bo'yicha emas, UMUMIY piksel bo'yicha
     * qo'yilishi kerak. Avval har tomon alohida 8000 ga
     * cheklangan edi va banner uchun 8000 × 8000 = 64 million
     * piksel chiqib, ilova qulagan. Har tomon alohida "xavfsiz"
     * ko'ringani bilan, ko'paytmasi xavfsiz emas.
     */
    private const val MAX_PIXELS = 16_000_000L

    /**
     * Maketni ImageBitmap qilib chizadi.
     *
     * Kompozitsiyadan tashqarida ishlaydi — shuning uchun
     * TextMeasurer qo'lda yig'iladi. Aks holda uni faqat
     * @Composable ichida olish mumkin bo'lardi va saqlash
     * ekranga bog'lanib qolardi.
     */
    fun render(
        context: Context,
        document: DesignDocument,
        pxPerMm: Float
    ): ImageBitmap {

        // Avval so'ralgan o'lcham hisoblanadi...
        var widthPx = (document.fullWidthMm * pxPerMm)
            .roundToInt()
            .coerceAtLeast(1)

        var heightPx = (document.fullHeightMm * pxPerMm)
            .roundToInt()
            .coerceAtLeast(1)

        // ...keyin umumiy piksel soni chegaradan oshsa, IKKALA
        // tomon bir xil koeffitsiyentga kichraytiriladi. Shunda
        // maketning nisbati buzilmaydi.
        val total = widthPx.toLong() * heightPx.toLong()

        if (total > MAX_PIXELS) {

            val factor = kotlin.math.sqrt(
                MAX_PIXELS.toDouble() / total.toDouble()
            ).toFloat()

            widthPx = (widthPx * factor).roundToInt().coerceAtLeast(1)
            heightPx = (heightPx * factor).roundToInt().coerceAtLeast(1)
        }

        val bitmap = ImageBitmap(widthPx, heightPx)

        val density = Density(density = 1f, fontScale = 1f)

        val textMeasurer = TextMeasurer(
            defaultFontFamilyResolver = createFontFamilyResolver(context),
            defaultDensity = density,
            defaultLayoutDirection = LayoutDirection.Ltr
        )

        val geometry = CanvasGeometry(
            document = document,
            pxPerMm = pxPerMm,
            originPx = Offset.Zero
        )

        CanvasDrawScope().draw(
            density = density,
            layoutDirection = LayoutDirection.Ltr,
            canvas = Canvas(bitmap),
            size = Size(widthPx.toFloat(), heightPx.toFloat())
        ) {
            drawDocument(document, geometry, textMeasurer)
        }

        return bitmap
    }

    /**
     * Ro'yxat uchun kichik muqova.
     *
     * Miqyos maketning o'zidan hisoblanadi: banner 3000 mm,
     * vizitka 90 mm — qat'iy pxPerMm ikkalasiga ham to'g'ri
     * kelmaydi. Bannerda fayl ulkan bo'lib ketardi, vizitkada
     * esa muqova mayda chiqardi.
     */
    fun saveCover(
        context: Context,
        document: DesignDocument,
        target: File
    ): File? = runCatching {

        val longest = maxOf(document.fullWidthMm, document.fullHeightMm)
            .coerceAtLeast(1f)

        // Pastki chegara yo'q.
        //
        // Avval 0.5 turgan edi va bu xato edi: banner uchun
        // "kamida shuncha aniqlik" degan talab ma'nosiz, chunki
        // muqova ro'yxatda 285dp kenglikda ko'rsatiladi. Katta
        // maket uchun juda mayda masshtab aynan to'g'ri natija.
        val pxPerMm = (COVER_MAX_PX / longest).coerceAtMost(12f)

        val bitmap = render(context, document, pxPerMm)

        target.parentFile?.mkdirs()

        FileOutputStream(target).use { out ->
            bitmap.asAndroidBitmap()
                .compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        target

    }.getOrNull()

    /**
     * 300 DPI da to'liq o'lchamdagi rasm.
     *
     * Vizitka uchun: 94 × 54 mm (bleed bilan) → 1110 × 638 px.
     */
    fun savePrintPng(
        context: Context,
        document: DesignDocument,
        target: File
    ): File? = runCatching {

        val bitmap = render(context, document, PRINT_PX_PER_MM)

        target.parentFile?.mkdirs()

        FileOutputStream(target).use { out ->
            bitmap.asAndroidBitmap()
                .compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        target

    }.getOrNull()
}