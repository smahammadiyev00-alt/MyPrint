package uz.myprint.feature.feature.design.studio.data

import android.content.Context
import android.graphics.Typeface
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.res.ResourcesCompat
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
 * boshqasi chiqadi. Shuning uchun ikkalasi ham AYNAN shu sinfdan
 * olinadi va boshqa hech qayerda shrift tanlanmaydi.
 *
 * ==== NEGA R.font ISHLATILMAYDI ====
 *
 * To'g'ridan-to'g'ri R.font.montserrat_regular yozilsa, fayl
 * yo'q bo'lganda LOYIHA YIG'ILMAYDI — va xato "Unresolved
 * reference: font" degan tushunarsiz shaklda chiqadi, chunki
 * R.font sinfi umuman yaratilmaydi.
 *
 * Bu yomon: 14 ta shriftdan bittasi yetishmagani uchun butun
 * ilova ishlamay qolishi mumkin emas. Shuning uchun resurs nomi
 * bo'yicha ish vaqtida qidiriladi. Fayl yo'q bo'lsa, o'sha shrift
 * ro'yxatda ko'rinmaydi, qolganlari ishlayveradi.
 *
 * Yon foydasi: shriftlarni bosqichma-bosqich qo'shish mumkin —
 * beshtasini qo'ydingiz, beshtasi ishlaydi.
 */
object DesignFonts {

    private lateinit var appContext: Context

    private val typefaceCache = mutableMapOf<String, Typeface>()

    private val familyCache = mutableMapOf<DesignFont, FontFamily>()

    /** Resurs nomi → identifikator. 0 bo'lsa fayl yo'q. */
    private val resIdCache = mutableMapOf<String, Int>()

    /** MyPrintApp.onCreate ichida chaqiriladi. */
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    /**
     * Resurs nomi enum nomidan hosil qilinadi.
     *
     *   MONTSERRAT → montserrat_regular / montserrat_bold
     *   PT_SERIF   → pt_serif_regular   / pt_serif_bold
     *
     * Alohida jadval yozilmadi: jadval bilan enum bir-biridan
     * uzilib qolishi mumkin, qoida esa doim mos keladi.
     */
    private fun resourceName(font: DesignFont, bold: Boolean): String =
        font.name.lowercase() + if (bold) "_bold" else "_regular"

    private fun resId(name: String): Int {

        if (!::appContext.isInitialized) return 0

        return resIdCache.getOrPut(name) {

            runCatching {

                appContext.resources.getIdentifier(
                    name,
                    "font",
                    appContext.packageName
                )

            }.getOrDefault(0)
        }
    }

    /**
     * Shrift fayli mavjudmi.
     *
     * Tanlash panelida ishlatiladi: yo'q shriftni ko'rsatib,
     * bosilganda tizim shriftiga o'tkazish foydalanuvchini
     * chalg'itadi.
     */
    fun isAvailable(font: DesignFont): Boolean =
        font.isSystem || resId(resourceName(font, bold = false)) != 0

    /** Ishlatishga tayyor shriftlar. */
    fun available(): List<DesignFont> =
        DesignFont.entries.filter { isAvailable(it) }

    /** Compose uchun. Ekranda chizishda ishlatiladi. */
    fun family(font: DesignFont): FontFamily = familyCache.getOrPut(font) {

        val systemFallback = when (font.systemFamily) {
            "serif" -> FontFamily.Serif
            "monospace" -> FontFamily.Monospace
            "sans-serif" -> FontFamily.SansSerif
            else -> null
        }

        if (systemFallback != null) return@getOrPut systemFallback

        val regular = resId(resourceName(font, bold = false))

        if (regular == 0) return@getOrPut FontFamily.SansSerif

        val bold = resId(resourceName(font, bold = true))

        runCatching {

            if (bold == 0) {

                // Qalin varianti yo'q — tizim sun'iy
                // qalinlashtiradi. Dekorativ shriftlarda bu
                // odatiy holat.
                FontFamily(Font(regular, FontWeight.Normal))

            } else {

                FontFamily(
                    Font(regular, FontWeight.Normal),
                    Font(bold, FontWeight.Bold)
                )
            }

        }.getOrDefault(FontFamily.SansSerif)
    }

    /**
     * PDF eksporti uchun.
     *
     * Xato bo'lsa jimgina tizim shriftiga qaytiladi: bitta shrift
     * yuklanmagani uchun butun eksport to'xtashi mumkin emas.
     * Foydalanuvchi boshqa shrift chiqqanini ko'radi va
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

            val fallback = {
                Typeface.create(font.systemFamily ?: "sans-serif", style)
            }

            if (font.isSystem || !::appContext.isInitialized) {
                return@runCatching fallback()
            }

            // Qalin fayl bo'lmasa oddiysi olinadi va tizim
            // qalinlashtiradi.
            val id = resId(resourceName(font, bold))
                .takeIf { it != 0 }
                ?: resId(resourceName(font, bold = false))

            if (id == 0) return@runCatching fallback()

            val base = ResourcesCompat.getFont(appContext, id)
                ?: return@runCatching fallback()

            when {
                // Fayl allaqachon qalin — qayta qalinlashtirilmaydi.
                bold && resId(resourceName(font, true)) != 0 && italic ->
                    Typeface.create(base, Typeface.ITALIC)

                bold && resId(resourceName(font, true)) != 0 -> base

                else -> Typeface.create(base, style)
            }

        }.getOrElse { Typeface.create("sans-serif", style) }

        typefaceCache[key] = result

        return result
    }
}