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
 * Manfiy qiymat bleed maydoniga chiqish degani, bu normal holat:
 * fon rasmi doim kesimdan tashqariga chiqib turishi kerak.
 */
data class LayerTransform(

    /** Chap yuqori burchak, mm. */
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
}

enum class ShapeKind {
    RECTANGLE,
    ELLIPSE,
    LINE
}

enum class TextAlign {
    START,
    CENTER,
    END
}

/**
 * Qatlamlar ro'yxatdagi tartibi bilan chiziladi: birinchisi eng
 * pastda. Alohida zIndex maydoni yo'q — ro'yxat tartibi yagona
 * haqiqat manbai bo'lgani ma'qul, aks holda ikkisi bir-biriga
 * zid bo'lib qoladi.
 */
sealed interface DesignLayer {

    val id: String

    val name: String

    val transform: LayerTransform

    val isLocked: Boolean

    val isVisible: Boolean

    fun withTransform(transform: LayerTransform): DesignLayer
}

data class TextLayer(

    override val id: String,

    override val name: String = "Matn",

    override val transform: LayerTransform,

    override val isLocked: Boolean = false,

    override val isVisible: Boolean = true,

    val text: String = "Matn",

    /** Shrift balandligi mm'da — bosmada shunday o'lchanadi. */
    val fontSizeMm: Float = 4f,

    val color: Color = Color.Black,

    val isBold: Boolean = false,

    val isItalic: Boolean = false,

    val align: TextAlign = TextAlign.START,

    val lineHeightMultiplier: Float = 1.2f,

    val letterSpacingMm: Float = 0f

) : DesignLayer {

    override fun withTransform(transform: LayerTransform) =
        copy(transform = transform)
}

data class ShapeLayer(

    override val id: String,

    override val name: String = "Shakl",

    override val transform: LayerTransform,

    override val isLocked: Boolean = false,

    override val isVisible: Boolean = true,

    val kind: ShapeKind = ShapeKind.RECTANGLE,

    val fill: Color? = Color(0xFF7B4DFF),

    val strokeColor: Color? = null,

    val strokeWidthMm: Float = 0.5f,

    /** Faqat RECTANGLE uchun. */
    val cornerRadiusMm: Float = 0f

) : DesignLayer {

    override fun withTransform(transform: LayerTransform) =
        copy(transform = transform)
}

/**
 * Rasm qatlami. Bitmap bu yerda saqlanmaydi — faqat manba havolasi.
 * Modelning o'zi yengil bo'lishi kerak: undo/redo tarixida o'nlab
 * nusxasi yotadi va har birida bitmap bo'lsa xotira portlaydi.
 */
data class ImageLayer(

    override val id: String,

    override val name: String = "Rasm",

    override val transform: LayerTransform,

    override val isLocked: Boolean = false,

    override val isVisible: Boolean = true,

    /** content:// yoki https:// */
    val sourceUri: String,

    /** Qirqish ramkasi, 0..1 oralig'ida manba rasmga nisbatan. */
    val cropLeft: Float = 0f,
    val cropTop: Float = 0f,
    val cropRight: Float = 1f,
    val cropBottom: Float = 1f

) : DesignLayer {

    override fun withTransform(transform: LayerTransform) =
        copy(transform = transform)
}
