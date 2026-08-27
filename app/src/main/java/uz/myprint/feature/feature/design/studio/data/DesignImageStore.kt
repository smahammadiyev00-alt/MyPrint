package uz.myprint.feature.feature.design.studio.data

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * RASMLAR OMBORI.
 *
 * Galereyadan tanlangan rasm NUSXA olinadi, havolasi emas.
 *
 * Sabab jiddiy: Photo Picker qaytaradigan content:// havolasi
 * VAQTINCHALIK ruxsat bilan keladi. Ilova qayta ishga tushsa,
 * yoki foydalanuvchi rasmni galereyadan o'chirsa, havola ishlamay
 * qoladi va maketda rasm o'rniga bo'shliq paydo bo'ladi. Mijoz
 * bir hafta oldin yasagan vizitkasini ochib, logotipini
 * ko'rmasligi — kechirib bo'lmaydigan holat.
 *
 * Nusxa filesDir/images ichida yotadi, ya'ni maket JSON'i bilan
 * bir joyda va bir umr yashaydi.
 */
class DesignImageStore(
    private val context: Context
) {

    private val root: File
        get() = File(context.filesDir, "images").apply { mkdirs() }

    /**
     * Xotiradagi kesh.
     *
     * Kalit — fayl yo'li va o'lcham chegarasi birgalikda. Bir xil
     * rasm ekranda kichik, eksportda katta kerak bo'ladi; ularni
     * bitta kalitga qo'shsak, biri ikkinchisini siqib chiqarardi.
     */
    private val cache = mutableMapOf<String, ImageBitmap>()

    /**
     * Galereyadan tanlangan rasmni ichki xotiraga ko'chiradi.
     *
     * @return saqlangan faylning yo'li, xato bo'lsa null
     */
    suspend fun import(uri: Uri): ImportedImage? =
        withContext(Dispatchers.IO) {

            runCatching {

                val target = File(root, "${UUID.randomUUID()}.img")

                context.contentResolver.openInputStream(uri)?.use { input ->
                    target.outputStream().use { output ->
                        input.copyTo(output)
                    }
                } ?: return@runCatching null

                // Asl o'lcham darhol o'qiladi: u DPI ogohlantirishi
                // uchun kerak va keyin har safar faylni ochib
                // o'lchashning ma'nosi yo'q.
                val bounds = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }

                BitmapFactory.decodeFile(target.absolutePath, bounds)

                if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
                    target.delete()
                    return@runCatching null
                }

                ImportedImage(
                    path = target.absolutePath,
                    widthPx = bounds.outWidth,
                    heightPx = bounds.outHeight
                )

            }.getOrNull()
        }

    /**
     * Rasmni chizish uchun yuklaydi.
     *
     * Har doim kichraytirib o'qiladi. 12 megapiksellik telefon
     * fotosi xotirada 48 MB egallaydi, vizitkada esa u 3 sm
     * kenglikda ko'rsatiladi — to'liq yuklashning ma'nosi yo'q va
     * bir necha rasm qo'shilsa ilova xotiradan qulaydi.
     */
    fun load(path: String, maxPx: Int): ImageBitmap? {

        val key = "$path@$maxPx"

        cache[key]?.let { return it }

        return runCatching {

            val file = File(path)

            if (!file.exists()) return null

            val bounds = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            BitmapFactory.decodeFile(path, bounds)

            if (bounds.outWidth <= 0) return null

            var sample = 1

            while (bounds.outWidth / sample > maxPx ||
                bounds.outHeight / sample > maxPx
            ) {
                sample *= 2
            }

            val bitmap = BitmapFactory.decodeFile(
                path,
                BitmapFactory.Options().apply { inSampleSize = sample }
            ) ?: return null

            bitmap.asImageBitmap().also { cache[key] = it }

        }.getOrNull()
    }

    /** Rasmning piksel o'lchami. Kesh orqali tez ishlaydi. */
    fun pixelSize(path: String): Pair<Int, Int>? = runCatching {

        val bounds = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        BitmapFactory.decodeFile(path, bounds)

        if (bounds.outWidth <= 0) null
        else bounds.outWidth to bounds.outHeight

    }.getOrNull()

    fun delete(path: String) {
        runCatching { File(path).delete() }
        cache.keys.filter { it.startsWith("$path@") }.forEach(cache::remove)
    }

    /** Xotira siqilganda chaqiriladi. */
    fun clearCache() = cache.clear()
}

data class ImportedImage(
    val path: String,
    val widthPx: Int,
    val heightPx: Int
) {

    val aspectRatio: Float
        get() = if (heightPx > 0) widthPx.toFloat() / heightPx else 1f
}

/** Bosma sifati bahosi. */
enum class PrintQuality {

    /** 300 DPI va undan yuqori — bosmaxona talabi bajarilgan. */
    GOOD,

    /** 150–300 DPI — bosiladi, lekin diqqat bilan qarasa bilinadi. */
    ACCEPTABLE,

    /** 150 DPI dan past — bosmada donador chiqadi. */
    POOR
}

/**
 * Rasmning maketdagi haqiqiy aniqligini hisoblaydi.
 *
 * Bu studioning eng foydali ogohlantirishlaridan biri. Foydalanuvchi
 * ekranda hammasi tiniq ko'rinadi deb o'ylaydi — ekran 400 DPI
 * atrofida, vizitka esa 300 DPI da bosiladi va rasm cho'zilgan
 * bo'lsa donador chiqadi. Buni BOSMADAN OLDIN aytish kerak, aks
 * holda mijoz pul to'lagandan keyin biladi.
 */
fun printDpi(pixelWidth: Int, widthMm: Float): Int {

    if (widthMm <= 0f) return 0

    val inches = widthMm / 25.4f

    return (pixelWidth / inches).toInt()
}

fun printQuality(dpi: Int): PrintQuality = when {
    dpi >= 300 -> PrintQuality.GOOD
    dpi >= 150 -> PrintQuality.ACCEPTABLE
    else -> PrintQuality.POOR
}
