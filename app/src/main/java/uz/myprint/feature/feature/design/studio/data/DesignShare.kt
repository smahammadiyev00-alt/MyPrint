package uz.myprint.feature.feature.design.studio.data

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.myprint.feature.feature.design.studio.domain.DesignDocument
import java.io.File

/** Qaysi formatda ulashiladi. */
enum class ShareFormat {

    /**
     * Bosmaxona uchun. Matn vektor bo'lib qoladi, shrift ichiga
     * singdiriladi, o'lcham millimetrda aniq saqlanadi.
     */
    PDF,

    /**
     * Ko'rish va tasdiqlash uchun. Telegram va WhatsApp'da
     * darhol ochiladi, PDF esa yuklab olishni talab qiladi —
     * shuning uchun mijozga ko'rsatish uchun PNG qulayroq.
     */
    PNG
}

/**
 * MAKETNI ULASHISH.
 *
 * O'zbekistonda bosmaxona bilan aloqa amalda Telegram orqali
 * ketadi, shuning uchun bu funksiya server tayyor bo'lgunicha
 * ham to'liq foydali: mijoz maketni yasaydi va bevosita ustaga
 * yuboradi.
 *
 * Fayl cacheDir ga yoziladi. Sabab: bu papkani tizim o'zi
 * tozalaydi, ya'ni ulashilgan eski fayllar telefonda to'planib
 * qolmaydi. Ulashish paytida fayl kerak, keyin esa yo'q — uni
 * doimiy saqlashning ma'nosi yo'q, chunki maket JSON'da bor va
 * istalgan payt qaytadan yasash mumkin.
 */
object DesignShare {

    /**
     * Ulashish natijasi.
     *
     * message — foydalanuvchiga ko'rsatiladigan matn. Xato bo'lsa
     * ham, masshtab haqida ogohlantirish bo'lsa ham shu yerda.
     */
    data class Result(
        val success: Boolean,
        val message: String?
    )

    /** Faylni yasaydi va ulashish oynasini ochadi. */
    suspend fun share(
        context: Context,
        document: DesignDocument,
        title: String,
        format: ShareFormat
    ): Result {

        val safeName = title
            .replace(Regex("[^\\p{L}\\p{N}]+"), "_")
            .trim('_')
            .ifBlank { "maket" }

        var scaleNote: String? = null

        val file = withContext(Dispatchers.IO) {

            val target = File(
                context.cacheDir,
                "share/$safeName.${format.extension()}"
            )

            // Eski fayl qolib ketmasligi kerak: nomi bir xil
            // bo'lsa, ulashish oynasi eskisini yuborishi mumkin.
            target.delete()

            when (format) {

                ShareFormat.PDF -> {

                    val result = DesignPdfExporter.export(
                        context = context,
                        document = document,
                        target = target
                    )

                    result?.scaleLabel?.let { label ->

                        scaleNote = "Fayl $label masshtabda yasaldi — " +
                                "bosmaxonaga shuni ayting"
                    }

                    result?.file
                }

                ShareFormat.PNG -> DesignRasterizer.savePrintPng(
                    context = context,
                    document = document,
                    target = target
                )
            }
        } ?: return Result(false, "Faylni yasab bo'lmadi")

        val uri = runCatching {

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

        }.getOrNull() ?: return Result(false, "Faylga ruxsat berilmadi")

        val intent = Intent(Intent.ACTION_SEND).apply {

            type = format.mimeType()

            putExtra(Intent.EXTRA_STREAM, uri)

            putExtra(Intent.EXTRA_SUBJECT, title)

            // Masshtab faylning O'ZIGA yozilmaydi, xabar matniga
            // qo'shiladi. Telegram va pochta buni matn sifatida
            // ko'rsatadi, ya'ni bosmaxona faylni ochmasdanoq
            // ko'radi. Faylga yozilsa, e'tibordan chetda qolardi.
            scaleNote?.let { putExtra(Intent.EXTRA_TEXT, "$title · $it") }

            // Qabul qiluvchi ilovaga faylni o'qish huquqi
            // beriladi. Busiz Telegram "fayl topilmadi" deydi.
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Maketni yuborish")
            .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

        return runCatching {
            context.startActivity(chooser)
            Result(true, scaleNote)
        }.getOrElse {
            Result(false, "Ulashish ilovasi topilmadi")
        }
    }

    private fun ShareFormat.extension(): String = when (this) {
        ShareFormat.PDF -> "pdf"
        ShareFormat.PNG -> "png"
    }

    private fun ShareFormat.mimeType(): String = when (this) {
        ShareFormat.PDF -> "application/pdf"
        ShareFormat.PNG -> "image/png"
    }
}