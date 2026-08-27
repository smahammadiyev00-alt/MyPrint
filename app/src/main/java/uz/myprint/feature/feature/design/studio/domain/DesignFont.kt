package uz.myprint.feature.feature.design.studio.domain

/**
 * MAKETDA ISHLATILADIGAN SHRIFTLAR.
 *
 * Bu yerda RESURS identifikatori yo'q — enum domen qatlamida
 * turadi va Android resurslariga bog'lanmasligi kerak. Haqiqiy
 * fayllar bilan bog'lanish data qatlamidagi DesignFonts da.
 *
 * Enum nomi JSON'ga yoziladi, shuning uchun uni O'ZGARTIRMANG:
 * nom o'zgarsa, saqlangan maketlar shrifti yo'qoladi va
 * standartga qaytadi. Yangi shrift qo'shish xavfsiz, mavjudini
 * qayta nomlash esa yo'q.
 */
enum class DesignFont(

    val label: String,

    /**
     * Kirill harflarini qo'llab-quvvatlaydimi.
     *
     * Bu maydon shunchaki ma'lumot uchun emas, ASOSIY masala.
     * Ko'p mashhur shriftlarda (Pacifico, Bebas Neue) faqat lotin
     * bor. Mijoz o'zbek kirillida "Ўзбекистон" deb yozsa, harflar
     * to'rtburchakka aylanadi yoki tizim boshqa shriftga sakraydi
     * — va bu faqat bosmadan keyin bilinadi.
     *
     * UI'da shunday shriftlar belgilanadi, kirill matn terilganda
     * esa ogohlantirish chiqadi.
     */
    val supportsCyrillic: Boolean,

    /**
     * Tizim shrifti (fayl talab qilmaydi).
     *
     * Zaxira sifatida saqlanadi: agar resurs fayli qandaydir
     * sabab bilan yuklanmasa, maket baribir chiziladi.
     */
    val systemFamily: String? = null

) {

    // ---- Tizim shriftlari (doim mavjud) ----

    SANS("Oddiy", supportsCyrillic = true, systemFamily = "sans-serif"),

    SERIF("Serif", supportsCyrillic = true, systemFamily = "serif"),

    MONO("Mono", supportsCyrillic = true, systemFamily = "monospace"),

    // ---- Sans: zamonaviy vizitka va banner ----

    MONTSERRAT("Montserrat", supportsCyrillic = true),

    OPEN_SANS("Open Sans", supportsCyrillic = true),

    RUBIK("Rubik", supportsCyrillic = true),

    /** Tor va baland — banner sarlavhasi uchun juda mos. */
    OSWALD("Oswald", supportsCyrillic = true),

    // ---- Serif: rasmiy hujjat va taklifnoma ----

    /** Rus tili uchun maxsus yaratilgan, kirilli mukammal. */
    PT_SERIF("PT Serif", supportsCyrillic = true),

    /** Nikoh taklifnomalari uchun klassik tanlov. */
    PLAYFAIR("Playfair", supportsCyrillic = true),

    LORA("Lora", supportsCyrillic = true),

    // ---- Dekorativ ----

    /** Qo'lyozma uslubi, kirilli bor. */
    CAVEAT("Caveat", supportsCyrillic = true),

    COMFORTAA("Comfortaa", supportsCyrillic = true),

    LOBSTER("Lobster", supportsCyrillic = true),

    /** Faqat lotin — kirill matnda ishlatilmaydi. */
    PACIFICO("Pacifico", supportsCyrillic = false),

    /** Faqat lotin. Yirik sarlavhalar uchun kuchli. */
    BEBAS("Bebas Neue", supportsCyrillic = false);

    val isSystem: Boolean get() = systemFamily != null

    companion object {

        /**
         * Matnda kirill harflari bormi.
         *
         * Faqat asosiy kirill diapazoni tekshiriladi: o'zbek
         * kirillidagi maxsus harflar (ў, қ, ғ, ҳ) ham shu
         * diapazonning kengaytmasida, shuning uchun ular ham
         * qamrab olinadi.
         */
        fun hasCyrillic(text: String): Boolean =
            text.any { it in '\u0400'..'\u04FF' }
    }
}
