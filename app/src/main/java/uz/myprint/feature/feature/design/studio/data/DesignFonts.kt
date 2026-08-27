package uz.myprint.feature.feature.design.studio.data

import android.content.Context
import android.graphics.Typeface
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.res.ResourcesCompat
import uz.myprint.R
import uz.myprint.feature.feature.design.studio.domain.DesignFont

/**
 * SHRIFTLARNI YUKLASH.
 *
 * Ikki xil ko'rinish kerak va ular bir-biriga mos bo'lishi shart:
 *
 *   FontFamily — Compose ekranda chizish uchun
 *   Typeface   — PDF eksportida kontur olish uchun
 *
 * Agar ular bir-biriga mos kelmasa, ekranda bir shrift, bosmada
 * boshqasi chiqadi. Shuning uchun ikkalasi ham AYNAN shu jadvaldan
 * olinadi va boshqa hech qayerda shrift tanlanmaydi.
 *
 * Fayllar res/font ichida. Yuklab olinadigan shriftlar ataylab
 * ishlatilmadi: bosmaxonada internet aloqasi yomon bo'lishi
 * mumkin, maket esa har qanday sharoitda ochilishi kerak.
 */
object DesignFonts {

    private lateinit var appContext: Context

    private val typefaceCache = mutableMapOf<String, Typeface>()

    private val familyCache = mutableMapOf<DesignFont, FontFamily>()

    /** MyPrintApp.onCreate ichida chaqiriladi. */
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    /**
     * Shrift resurslari.
     *
     * Har shrift uchun ikki fayl: oddiy va qalin. Qalin fayl
     * bo'lmasa tizim sun'iy qalinlashtiradi — ekranda o'tadi,
     * lekin bosmada harflar qo'pol ko'rinadi. Shuning uchun
     * ikkalasi ham qo'yilgan.
     */
    private fun resources(font: DesignFont): Pair<Int, Int>? = when (font) {

        DesignFont.MONTSERRAT ->
            R.font.montserrat_regular to R.font.montserrat_bold

        DesignFont.OPEN_SANS ->
            R.font.open_sans_regular to R.font.open_sans_bold

        DesignFont.RUBIK ->
            R.font.rubik_regular to R.font.rubik_bold

        DesignFont.OSWALD ->
            R.font.oswald_regular to R.font.oswald_bold

        DesignFont.PT_SERIF ->
            R.font.pt_serif_regular to R.font.pt_serif_bold

        DesignFont.PLAYFAIR ->
            R.font.playfair_regular to R.font.playfair_bold

        DesignFont.LORA ->
            R.font.lora_regular to R.font.lora_bold

        DesignFont.CAVEAT ->
            R.font.caveat_regular to R.font.caveat_bold

        DesignFont.COMFORTAA ->
            R.font.comfortaa_regular to R.font.comfortaa_bold

        // Bu uchtasida qalin variant yo'q — ular allaqachon
        // qalin yoki dekorativ, qalinlashtirish ma'nosiz.
        DesignFont.LOBSTER ->
            R.font.lobster_regular to R.font.lobster_regular

        DesignFont.PACIFICO ->
            R.font.pacifico_regular to R.font.pacifico_regular

        DesignFont.BEBAS ->
            R.font.bebas_regular to R.font.bebas_regular

        // Tizim shriftlari fayl talab qilmaydi.
        DesignFont.SANS,
        DesignFont.SERIF,
        DesignFont.MONO -> null
    }

    /** Compose uchun. Ekranda chizishda ishlatiladi. */
    fun family(font: DesignFont): FontFamily = familyCache.getOrPut(font) {

        val res = resources(font)

        if (res == null) {

            when (font.systemFamily) {
                "serif" -> FontFamily.Serif
                "monospace" -> FontFamily.Monospace
                else -> FontFamily.SansSerif
            }

        } else {

            runCatching {

                FontFamily(
                    Font(res.first, FontWeight.Normal),
                    Font(res.second, FontWeight.Bold)
                )

            }.getOrDefault(FontFamily.SansSerif)
        }
    }

    /**
     * PDF eksporti uchun.
     *
     * Bu yerda xato bo'lsa jimgina tizim shriftiga qaytiladi:
     * bitta shrift yuklanmagani uchun butun eksport to'xtashi
     * mumkin emas. Foydalanuvchi buzilgan shriftni ko'radi va
     * o'zgartiradi, lekin fayl baribir yasaladi.
     */
    fun typeface(
        font: DesignFont,
        bold: Boolean,
        italic: Boolean
    ): Typeface {

        val key = "${font.name}-$bold-$italic"

        typefaceCache[key]?.let { return it }

        val style = when {
            bold && italic -> Typeface.BOLD_ITALIC
            bold -> Typeface.BOLD
            italic -> Typeface.ITALIC
            else -> Typeface.NORMAL
        }

        val result = runCatching {

            val res = resources(font)

            if (res == null || !::appContext.isInitialized) {

                Typeface.create(font.systemFamily ?: "sans-serif", style)

            } else {

                val base = ResourcesCompat.getFont(
                    appContext,
                    if (bold) res.second else res.first
                ) ?: return@runCatching Typeface.create(
                    "sans-serif",
                    style
                )

                // Qalin fayl allaqachon yuklangani uchun uni
                // qayta qalinlashtirish shart emas — faqat
                // kursiv qo'shiladi.
                if (italic) {
                    Typeface.create(base, Typeface.ITALIC)
                } else {
                    base
                }
            }

        }.getOrElse { Typeface.create("sans-serif", style) }

        typefaceCache[key] = result

        return result
    }
}
