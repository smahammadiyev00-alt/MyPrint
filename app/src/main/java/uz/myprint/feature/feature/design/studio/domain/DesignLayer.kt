package uz.myprint.feature.feature.design.studio.domain

import androidx.compose.ui.graphics.Color

/**
 * Qatlamning joylashuvi. Barcha o'lchamlar MILLIMETRDA.
 *
 * Piksel emas — chunki bir xil model ham ekranda, ham 300 DPI
 * eksportda ishlatiladi. Piksel saqlansa, eksport ekranga bog'lanib
 * qolardi va bosmada o'lcham buzilardi.
 *
 * Koordinata boshi — kesim chizig'ining (trim) chap yuqori burchagi.
 */
data class LayerTransform(

    val xMm: Float = 0f,

    val yMm: Float = 0f,

    val widthMm: Float = 40f,

    val heightMm: Float = 20f,

    /** Markaz atrofida burilish, gradus. */
    val rotationDeg: Float = 0f,

    val opacity: Float = 1f

) {

    val centerXMm: Float get() = xMm + widthMm / 2f

    val centerYMm: Float get() = yMm + heightMm / 2f

    /** Markazni joyida ushlab, o'lchamni almashtiradi. */
    fun resizedAroundCenter(
        newWidthMm: Float,
        newHeightMm: Float
    ): LayerTransform {

        val w = newWidthMm.coerceAtLeast(2f)
        val h = newHeightMm.coerceAtLeast(2f)

        return copy(
            xMm = centerXMm - w / 2f,
            yMm = centerYMm - h / 2f,
            widthMm = w,
            heightMm = h
        )
    }
}

enum class ShapeKind {
    RECTANGLE,
    ELLIPSE,
    TRIANGLE,
    LINE
}

enum class TextAlign {
    START,
    CENTER,
    END
}

/**
 * Harf registri uslub sifatida saqlanadi, matnning o'ziga
 * tegilmaydi. Shunda foydalanuvchi "KATTA" ni bosib, keyin
 * fikridan qaytsa, asl yozuvi tiklanadi.
 */
enum class TextCase {
    NORMAL,
    UPPER,
    LOWER
}

/**
 * Qatlamlar ro'yxatdagi tartibi bilan chiziladi: birinchisi eng
 * pastda. Alohida zIndex maydoni yo'q — ro'yxat tartibi yagona
 * haqiqat manbai bo'lgani ma'qul.
 */
sealed interface DesignLayer {

    val id: String

    val name: String

    val transform: LayerTransform

    val isLocked: Boolean

    val isVisible: Boolean

    /**
     * QAYSI QATLAM ICHIGA QIRQILADI.
     *
     * null bo'lsa — qatlam erkin, odatdagidek chiziladi.
     * To'ldirilgan bo'lsa — qatlam faqat o'sha qatlamning shakli
     * ichida ko'rinadi, tashqarisi qirqib tashlanadi. Photoshop
     * atamasi bilan aytganda "clipping mask".
     *
     * Nega id, obyekt emas: qatlam ichida boshqa qatlamning to'liq
     * nusxasi yotsa, uni tahrirlaganda ikki joyda yangilash kerak
     * bo'ladi va ular albatta bir-biriga mos kelmay qoladi.
     * Ro'yxat yagona manba bo'lib qolgani ma'qul.
     *
     * MUHIM: nishon qatlam ro'yxatda PASTROQDA turishi shart.
     * Aks holda "ichiga" degan tushuncha ma'nosini yo'qotadi,
     * chunki nishon allaqachon ustiga chizilgan bo'ladi.
     */
    val clipToId: String?

    /**
     * GURUH IDENTIFIKATORI.
     *
     * Bir xil groupId ga ega qatlamlar yaxlit birlik sifatida
     * ishlaydi: birortasi tanlansa hammasi tanlanadi, surilsa
     * hammasi suriladi, cho'zilsa hammasi mutanosib o'zgaradi.
     *
     * Nega bu "birlashtirish" emas, "guruhlash": bu muharrir
     * vektorli. Ikki qatlamni haqiqatdan bitta qilish uchun ularni
     * rasmga aylantirish kerak, u holda matnni qayta tahrirlash,
     * rangini o'zgartirish yoki shrift almashtirish imkoni
     * yo'qoladi. Guruh esa xuddi shu qulaylikni beradi, lekin
     * hech narsani yo'qotmaydi va istalgan payt ajratiladi.
     */
    val groupId: String?

    fun withTransform(transform: LayerTransform): DesignLayer

    /** Nusxa olishda yangi id beradi. */
    fun withId(id: String): DesignLayer

    fun withVisibility(visible: Boolean): DesignLayer

    fun withLock(locked: Boolean): DesignLayer

    fun withClip(targetId: String?): DesignLayer

    fun withGroup(groupId: String?): DesignLayer
}

data class TextLayer(

    override val id: String,

    override val name: String = "Matn",

    override val transform: LayerTransform,

    override val isLocked: Boolean = false,

    override val isVisible: Boolean = true,

    override val clipToId: String? = null,

    override val groupId: String? = null,

    val text: String = "Matn",

    /** Shrift balandligi mm'da — bosmada shunday o'lchanadi. */
    val fontSizeMm: Float = 5f,

    val font: DesignFont = DesignFont.SANS,

    val color: Color = Color.Black,

    val isBold: Boolean = false,

    val isItalic: Boolean = false,

    val isUnderline: Boolean = false,

    val textCase: TextCase = TextCase.NORMAL,

    val align: TextAlign = TextAlign.START,

    /** Qatorlar oralig'i, shrift o'lchamiga nisbatan. */
    val lineHeightMultiplier: Float = 1.2f,

    /** Harflar oralig'i, mm. Manfiy ham bo'lishi mumkin. */
    val letterSpacingMm: Float = 0f

) : DesignLayer {

    /** Ekranga chiqadigan matn — registr uslubi qo'llangan holda. */
    val displayText: String
        get() = when (textCase) {
            TextCase.NORMAL -> text
            TextCase.UPPER -> text.uppercase()
            TextCase.LOWER -> text.lowercase()
        }

    override fun withTransform(transform: LayerTransform) =
        copy(transform = transform)

    override fun withId(id: String) = copy(id = id)

    override fun withVisibility(visible: Boolean) =
        copy(isVisible = visible)

    override fun withLock(locked: Boolean) = copy(isLocked = locked)

    override fun withClip(targetId: String?) = copy(clipToId = targetId)

    override fun withGroup(groupId: String?) = copy(groupId = groupId)
}

data class ShapeLayer(

    override val id: String,

    override val name: String = "Shakl",

    override val transform: LayerTransform,

    override val isLocked: Boolean = false,

    override val isVisible: Boolean = true,

    override val clipToId: String? = null,

    override val groupId: String? = null,

    val kind: ShapeKind = ShapeKind.RECTANGLE,

    val fill: Color? = Color(0xFF7B4DFF),

    val strokeColor: Color? = null,

    val strokeWidthMm: Float = 0.5f,

    /** Faqat RECTANGLE uchun. */
    val cornerRadiusMm: Float = 0f

) : DesignLayer {

    override fun withTransform(transform: LayerTransform) =
        copy(transform = transform)

    override fun withId(id: String) = copy(id = id)

    override fun withVisibility(visible: Boolean) =
        copy(isVisible = visible)

    override fun withLock(locked: Boolean) = copy(isLocked = locked)

    override fun withClip(targetId: String?) = copy(clipToId = targetId)

    override fun withGroup(groupId: String?) = copy(groupId = groupId)
}

/**
 * Rasm qatlami. Bitmap bu yerda saqlanmaydi — faqat manba havolasi.
 */
data class ImageLayer(

    override val id: String,

    override val name: String = "Rasm",

    override val transform: LayerTransform,

    override val isLocked: Boolean = false,

    override val isVisible: Boolean = true,

    override val clipToId: String? = null,

    override val groupId: String? = null,

    val sourceUri: String,

    val cropLeft: Float = 0f,
    val cropTop: Float = 0f,
    val cropRight: Float = 1f,
    val cropBottom: Float = 1f

) : DesignLayer {

    override fun withTransform(transform: LayerTransform) =
        copy(transform = transform)

    override fun withId(id: String) = copy(id = id)

    override fun withVisibility(visible: Boolean) =
        copy(isVisible = visible)

    override fun withLock(locked: Boolean) = copy(isLocked = locked)

    override fun withClip(targetId: String?) = copy(clipToId = targetId)

    override fun withGroup(groupId: String?) = copy(groupId = groupId)
}