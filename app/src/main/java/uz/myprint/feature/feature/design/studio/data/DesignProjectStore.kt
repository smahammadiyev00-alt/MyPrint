package uz.myprint.feature.feature.design.studio.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.myprint.feature.feature.design.studio.domain.DesignDocument
import uz.myprint.feature.feature.product.domain.model.ProductCategory
import java.io.File

/**
 * Saqlangan loyiha — "Loyihalaringiz" ro'yxatidagi bitta karta.
 */
data class SavedProject(

    val id: String,

    val title: String,

    val productId: String,

    /**
     * O'lcham identifikatori.
     *
     * Studioni qayta ochish uchun shart: mahsulot bitta bo'lsa
     * ham, 90×50 va 85×55 vizitka boshqa-boshqa maketlar.
     */
    val sizeId: String,

    val category: ProductCategory,

    /** Muqova PNG fayli. Yo'q bo'lishi mumkin — hali chizilmagan. */
    val coverPath: String?,

    val updatedAtMillis: Long,

    val widthMm: Float,

    val heightMm: Float,

    /** Nechta element qo'shilgan — bo'sh loyihani ajratish uchun. */
    val layerCount: Int
)

/**
 * LOYIHALAR OMBORI.
 *
 * Ma'lumotlar bazasi emas, oddiy fayllar. Sabab: Room KSP va
 * plagin talab qiladi, loyihada ular hali yo'q. Loyihalar soni
 * o'nlab bo'ladi, yuzlab emas — bunday hajmda fayl tizimi
 * bazadan sekinroq ishlamaydi va murakkabligi ancha kam.
 *
 * Har loyiha ikki fayldan iborat:
 *   {id}.json  — maketning o'zi, qayta ochish uchun
 *   {id}.png   — muqova, ro'yxatda ko'rsatish uchun
 *
 * Katalog: filesDir/projects — bu ilovaga xos ichki xotira,
 * boshqa ilovalar ko'rmaydi va ilova o'chirilganda tozalanadi.
 * Bulutga sinxronlash qo'shilganda shu papka yuklanadi.
 */
class DesignProjectStore(
    private val context: Context
) {

    private val root: File
        get() = File(context.filesDir, "projects").apply { mkdirs() }

    private fun documentFile(id: String) = File(root, "$id.json")

    private fun coverFile(id: String) = File(root, "$id.png")

    private fun metaFile(id: String) = File(root, "$id.meta")

    /**
     * Loyihani saqlaydi va muqovasini yangilaydi.
     *
     * Muqova chizish qimmat amal — shuning uchun IO oqimida
     * bajariladi. Avtosaqlashda uni har safar qayta chizmaslik
     * uchun `withCover = false` berish mumkin.
     */
    suspend fun save(
        document: DesignDocument,
        title: String,
        productId: String,
        sizeId: String,
        category: ProductCategory,
        withCover: Boolean = true
    ): SavedProject = withContext(Dispatchers.IO) {

        val id = document.id

        documentFile(id).writeText(DesignDocumentJson.encode(document))

        // Sarlavha va mahsulot maketning ichida yo'q — ular
        // buyurtmaga tegishli ma'lumot. Alohida kichik faylda
        // saqlanadi, JSON'ni ifloslantirmaslik uchun.
        metaFile(id).writeText(
            listOf(
                title,
                productId,
                category.name,
                System.currentTimeMillis().toString(),
                sizeId
            ).joinToString("\n")
        )

        val cover = if (withCover) {
            DesignRasterizer.saveCover(context, document, coverFile(id))
        } else {
            coverFile(id).takeIf { it.exists() }
        }

        SavedProject(
            id = id,
            title = title,
            productId = productId,
            sizeId = sizeId,
            category = category,
            coverPath = cover?.absolutePath,
            updatedAtMillis = System.currentTimeMillis(),
            widthMm = document.widthMm,
            heightMm = document.heightMm,
            layerCount = document.layers.size
        )
    }

    /** Maketni qayta ochish uchun o'qiydi. */
    suspend fun load(id: String): DesignDocument? =
        withContext(Dispatchers.IO) {

            val file = documentFile(id)

            if (!file.exists()) return@withContext null

            DesignDocumentJson.decode(file.readText())
        }

    /**
     * Barcha loyihalar, oxirgi tahrirlangani birinchi.
     *
     * Buzilgan fayl ro'yxatni to'xtatmaydi — shunchaki
     * o'tkazib yuboriladi.
     */
    suspend fun list(): List<SavedProject> = withContext(Dispatchers.IO) {

        root.listFiles { file -> file.extension == "json" }
            .orEmpty()
            .mapNotNull { file -> readProject(file.nameWithoutExtension) }
            .sortedByDescending { it.updatedAtMillis }
    }

    private fun readProject(id: String): SavedProject? = runCatching {

        val document = DesignDocumentJson
            .decode(documentFile(id).readText())
            ?: return null

        val meta = metaFile(id)
            .takeIf { it.exists() }
            ?.readLines()
            .orEmpty()

        SavedProject(
            id = id,
            title = meta.getOrNull(0)?.takeIf { it.isNotBlank() }
                ?: "Nomsiz loyiha",
            productId = meta.getOrNull(1).orEmpty(),
            sizeId = meta.getOrNull(4).orEmpty(),
            category = runCatching {
                enumValueOf<ProductCategory>(meta.getOrNull(2).orEmpty())
            }.getOrDefault(ProductCategory.OTHER),
            coverPath = coverFile(id).takeIf { it.exists() }?.absolutePath,
            updatedAtMillis = meta.getOrNull(3)?.toLongOrNull()
                ?: documentFile(id).lastModified(),
            widthMm = document.widthMm,
            heightMm = document.heightMm,
            layerCount = document.layers.size
        )

    }.getOrNull()

    /**
     * Faqat nomini o'zgartiradi.
     *
     * Maket faylига tegilmaydi — nom meta faylda yashaydi.
     * Muqova ham qayta chizilmaydi, chunki dizayn o'zgarmagan.
     */
    suspend fun rename(id: String, title: String): Boolean =
        withContext(Dispatchers.IO) {

            val meta = metaFile(id)

            if (!meta.exists()) return@withContext false

            val lines = meta.readLines().toMutableList()

            while (lines.size < 5) lines.add("")

            lines[0] = title
            lines[3] = System.currentTimeMillis().toString()

            meta.writeText(lines.joinToString("\n"))

            true
        }

    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        documentFile(id).delete()
        coverFile(id).delete()
        metaFile(id).delete()
        Unit
    }

    /** Nusxa ko'chirish — "shu asosda yangi maket" uchun. */
    suspend fun duplicate(id: String, newTitle: String): SavedProject? =
        withContext(Dispatchers.IO) {

            val document = load(id) ?: return@withContext null

            val source = readProject(id) ?: return@withContext null

            // Yangi id vaqt tamg'asi bilan: eski maket joyida
            // qoladi, nusxasi mustaqil fayl bo'ladi.
            save(
                document = document.copy(
                    id = "$id-copy-${System.currentTimeMillis()}"
                ),
                title = newTitle,
                productId = source.productId,
                sizeId = source.sizeId,
                category = source.category
            )
        }

    /**
     * Bosmaga tayyor PNG yasaydi va uning yo'lini qaytaradi.
     *
     * Bu YAKUNIY bosma fayli emas — rang sRGB'da qoladi. Buyurtma
     * yuborilganda server maket JSON'idan CMYK PDF yasashi kerak.
     * Bu fayl mijoz ko'rib tasdiqlashi uchun.
     */
    suspend fun exportPreviewPng(id: String): String? =
        withContext(Dispatchers.IO) {

            val document = load(id) ?: return@withContext null

            val target = File(
                context.cacheDir,
                "export/${id}_300dpi.png"
            )

            DesignRasterizer
                .savePrintPng(context, document, target)
                ?.absolutePath
        }
}

/** "2 soat oldin tahrirlangan" ko'rinishidagi matn. */
fun SavedProject.updatedLabel(nowMillis: Long = System.currentTimeMillis()): String {

    val diff = (nowMillis - updatedAtMillis).coerceAtLeast(0L)

    val minutes = diff / 60_000

    val hours = minutes / 60

    val days = hours / 24

    return when {
        minutes < 1L -> "Hozirgina tahrirlangan"
        minutes < 60L -> "$minutes daqiqa oldin tahrirlangan"
        hours < 24L -> "$hours soat oldin tahrirlangan"
        days < 30L -> "$days kun oldin tahrirlangan"
        else -> "${days / 30} oy oldin tahrirlangan"
    }
}