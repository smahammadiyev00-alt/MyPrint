package uz.myprint.feature.feature.project.model

import androidx.annotation.DrawableRes

/**
 * Foydalanuvchining loyihasi.
 *
 * Faqat biznes ma'lumoti. UI mantiqi bu yerga tushmasligi kerak.
 */
data class Project(

    /** Loyihaning noyob identifikatori. */
    val id: String,

    val title: String,

    val category: ProjectCategory,

    /**
     * Zaxira muqova — foydalanuvchi hali hech narsa chizmagan yoki
     * muqova fayli yo'qolgan holat uchun.
     */
    @DrawableRes
    val imageRes: Int,

    /**
     * Haqiqiy muqova — studio chizib bergan PNG faylning yo'li.
     *
     * Nega DrawableRes emas: bu foydalanuvchining o'z maketi,
     * ilova resurslari orasida bunday rasm bo'lishi mumkin emas.
     * null bo'lsa imageRes ishlatiladi.
     */
    val coverPath: String? = null,

    /**
     * Example:
     * "2 soat oldin tahrirlangan"
     */
    val updatedAt: String,

    /**
     * Studioga qaytish uchun kerak.
     *
     * Bularsiz kartani bosganda qaysi mahsulotning qaysi o'lchamini
     * ochishni bilib bo'lmaydi — vizitka bitta bo'lsa ham, 90×50
     * va 85×55 boshqa-boshqa maketlar.
     */
    val productId: String = "",

    val sizeId: String = ""
)