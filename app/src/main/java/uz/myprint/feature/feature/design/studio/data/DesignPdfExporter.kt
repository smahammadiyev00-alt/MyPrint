package uz.myprint.feature.feature.design.studio.data

import android.content.Context
import android.graphics.pdf.PdfDocument as AndroidPdfDocument
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import uz.myprint.feature.feature.design.studio.domain.DesignDocument
import uz.myprint.feature.feature.design.studio.presentation.CanvasGeometry
import uz.myprint.feature.feature.design.studio.presentation.drawDocument
import java.io.File
import java.io.FileOutputStream
import kotlin.math.ceil

/**
 * PDF EKSPORT.
 *
 * Nega PNG yetarli emas: PNG'da matn — piksellar to'plami. Uni
 * kattalashtirsangiz donador bo'ladi, bosmaxona esa hech narsa
 * qila olmaydi. PDF'da matn MATN bo'lib qoladi va shrift fayl
 * ichiga singdiriladi — bosmada harflar qanchalik kichik bo'lsa
 * ham tiniq chiqadi.
 *
 * Android'ning PdfDocument sinfi oddiy android.graphics.Canvas
 * beradi. Compose esa uni o'rab olishi mumkin, shuning uchun ayni
 * `drawDocument` funksiyasi ishlatiladi — ekranda ko'ringan narsa
 * PDF'ga aynan tushadi. Ikkinchi chizuvchi yozish kerak emas, va
 * ular bir-biridan farq qilib qolish xavfi ham yo'q.
 *
 * CHEKLOV: rang sRGB'da qoladi, CMYK emas. Android'da ICC profil
 * orqali konvertatsiya qiladigan vosita yo'q. Bu PDF bosmaxona
 * uchun juda yaxshi qoralama, lekin PDF/X-1a standarti emas.
 * Yorqin ranglar (masalan #7B4DFF) bosmada biroz bo'g'iqroq
 * chiqadi — mijozni ogohlantirish kerak.
 */
/**
 * Eksport natijasi.
 *
 * Masshtab foydalanuvchiga aytilishi SHART: 1:10 da yasalgan
 * faylni bosmaxona haqiqiy o'lcham deb tushunsa, 3 metrlik banner
 * 30 santimetr bo'lib bosiladi.
 */
data class PdfExportResult(
    val file: File,
    val scaleDenominator: Int,
    val pageWidthMm: Float,
    val pageHeightMm: Float
) {

    val isScaled: Boolean get() = scaleDenominator > 1

    /** "1:10" yoki null. */
    val scaleLabel: String? get() =
        if (isScaled) "1:$scaleDenominator" else null
}

object DesignPdfExporter {

    /** 1 mm necha punkt. PDF punktda o'lchanadi: 1 dyuym = 72 pt. */
    private const val PT_PER_MM = 72f / 25.4f

    /**
     * PDF sahifasining eng katta tomoni — 14400 punkt (508 sm).
     *
     * Bu PDF spetsifikatsiyasining qat'iy chegarasi, dasturlarning
     * kamchiligi emas. Undan katta sahifa yasalsa, fayl rasman
     * buzuq bo'ladi: Corel yoki ochmaydi, yoki o'lchamni o'zicha
     * qirqib tashlaydi.
     *
     * 3 × 6 m banner esa 8788 × 17292 pt — chegaradan ikki barobar
     * ortiq. Ya'ni katta bannerni "haqiqiy o'lchamda" PDF qilishning
     * iloji yo'q, bu texnik cheklov.
     */
    private const val MAX_PAGE_PT = 14_400f

    /**
     * Ruxsat etilgan masshtablar.
     *
     * Poligrafiyada standart qiymatlar. Ixtiyoriy son (masalan
     * 1:7.3) ishlatilmaydi: bosmaxona buni qo'lda hisoblab
     * kattalashtiradi va g'alati son xatoga olib keladi.
     */
    private val SCALE_STEPS = listOf(1, 2, 5, 10, 20, 50, 100)

    /**
     * Maketni PDF qilib saqlaydi.
     *
     * Sahifa o'lchami bleed BILAN olinadi — bosmaxona kesish
     * uchun zaxira maydonni ko'rishi kerak.
     */
    /**
     * Maketni PDF qilib saqlaydi.
     *
     * Sahifa PDF chegarasiga sig'masa, avtomatik masshtabga
     * o'tiladi (1:10 va hokazo). Bu poligrafiyada odatiy amaliyot:
     * katta bannerlar doim kichraytirilgan holda topshiriladi va
     * bosmaxona o'zi kattalashtiradi. Vektor bo'lgani uchun sifat
     * yo'qolmaydi — chiziq 1:10 da ham matematik tavsif bo'lib
     * qoladi, piksel emas.
     */
    fun export(
        context: Context,
        document: DesignDocument,
        target: File
    ): PdfExportResult? = runCatching {

        val scale = chooseScale(document)

        val widthMm = document.fullWidthMm / scale

        val heightMm = document.fullHeightMm / scale

        // YUQORIGA yaxlitlanadi, oddiy yaxlitlash emas.
        //
        // PDF sahifasi faqat butun punktda bo'ladi. 89 mm =
        // 252.28 pt; oddiy yaxlitlashda 252 pt = 88.90 mm chiqadi
        // va sahifa kerakligidan 0.1 mm KICHIK bo'lib qoladi.
        // Kesish dopuski ±0.5 mm bo'lgani uchun bu sezilmaydi,
        // lekin yo'nalish noto'g'ri: bleed ataylab zaxira maydon,
        // uni kichraytirish maqsadiga zid. Ortiqcha 0.3 mm esa
        // baribir kesib tashlanadi.
        val widthPt = ceil(widthMm * PT_PER_MM).toInt()

        val heightPt = ceil(heightMm * PT_PER_MM).toInt()

        val pdf = AndroidPdfDocument()

        val pageInfo = AndroidPdfDocument.PageInfo
            .Builder(widthPt, heightPt, 1)
            .create()

        val page = pdf.startPage(pageInfo)

        val density = Density(density = 1f, fontScale = 1f)

        val textMeasurer = TextMeasurer(
            defaultFontFamilyResolver = createFontFamilyResolver(context),
            defaultDensity = density,
            defaultLayoutDirection = LayoutDirection.Ltr
        )

        // Masshtab sahifaning HAQIQIY punkt o'lchamidan
        // hisoblanadi, nazariy qiymatdan emas — aks holda maket
        // yaxlitlangan sahifada biroz siljib qolardi.
        //
        // Ikki o'q arzimas darajada farq qilishi mumkin (har biri
        // alohida yaxlitlangani uchun); kattarog'i olinadi, shunda
        // maket sahifani to'liq qoplaydi va chetda oq chiziq
        // qolmaydi.
        val ptPerMm = maxOf(
            widthPt / document.fullWidthMm,
            heightPt / document.fullHeightMm
        )

        // CanvasGeometry birlik nomini bilmaydi — u faqat
        // "millimetrni shunga ko'paytir" deydi. Aynan shuning
        // uchun bitta geometriya ham ekranga, ham 300 DPI rasmga,
        // ham PDF'ga xizmat qiladi.
        val geometry = CanvasGeometry(
            document = document,
            pxPerMm = ptPerMm,
            originPx = Offset.Zero
        )

        CanvasDrawScope().draw(
            density = density,
            layoutDirection = LayoutDirection.Ltr,
            canvas = Canvas(page.canvas),
            size = Size(widthPt.toFloat(), heightPt.toFloat())
        ) {
            // textAsPaths = true — Corel uchun hal qiluvchi.
            // Tafsiloti DesignRenderer.drawDocument izohida.
            drawDocument(
                document = document,
                geometry = geometry,
                textMeasurer = textMeasurer,
                textAsPaths = true
            )
        }

        pdf.finishPage(page)

        target.parentFile?.mkdirs()

        FileOutputStream(target).use { out ->
            pdf.writeTo(out)
        }

        pdf.close()

        PdfExportResult(
            file = target,
            scaleDenominator = scale.toInt(),
            pageWidthMm = widthPt / PT_PER_MM,
            pageHeightMm = heightPt / PT_PER_MM
        )

    }.getOrNull()

    /**
     * Sahifa chegaraga sig'adigan eng kichik masshtabni tanlaydi.
     *
     * Eng kichigi ataylab: 1:2 yetsa 1:10 ishlatilmaydi. Masshtab
     * qancha katta bo'lsa, bosmaxonada xato qilish ehtimoli ham
     * shuncha yuqori.
     */
    private fun chooseScale(document: DesignDocument): Float {

        val longestMm = maxOf(
            document.fullWidthMm,
            document.fullHeightMm
        )

        val longestPt = longestMm * PT_PER_MM

        if (longestPt <= MAX_PAGE_PT) return 1f

        return SCALE_STEPS
            .firstOrNull { longestPt / it <= MAX_PAGE_PT }
            ?.toFloat()

        // Ro'yxatdagi hech biri yetmasa (100 metrdan katta maket)
        // aniq hisoblangan qiymat olinadi. Chiroyli son bo'lmaydi,
        // lekin buzuq fayldan yaxshiroq.
            ?: ceil(longestPt / MAX_PAGE_PT)
    }
}