package uz.myprint.feature.feature.design.studio.domain

import uz.myprint.feature.feature.design.studio.domain.DesignTemplate
import uz.myprint.feature.feature.product.domain.model.ProductCategory

/**
 * TAYYOR MAKET.
 *
 * Qatlamlar ro'yxati emas, ularni YASAYDIGAN funksiya saqlanadi.
 *
 * Sabab: vizitka 90 × 50 ham, 85 × 55 ham bo'ladi; banner esa
 * har xil o'lchamda. Qatlamlarni qat'iy millimetrda saqlasak,
 * har o'lcham uchun alohida shablon yozish kerak bo'lardi va
 * ularni bir xil ko'rinishda ushlab turish imkonsiz bo'lardi.
 *
 * Funksiya esa maket o'lchamini olib, hamma narsani NISBATDA
 * hisoblaydi — bitta shablon barcha o'lchamlarga mos tushadi.
 */
data class DesignTemplate(

    val id: String,

    val name: String,

    val category: ProductCategory,

    /**
     * Qatlamlarni yasaydi.
     *
     * Har chaqiruvda YANGI id'lar bilan qaytaradi, aks holda
     * bitta shablonni ikki marta qo'llaganda id'lar takrorlanib,
     * tanlash va sloylar paneli buzilardi.
     */
    val build: (DesignDocument) -> List<DesignLayer>
)

/**
 * Shablon yasashda ishlatiladigan yordamchi.
 *
 * Koordinatalar 0..1 nisbatda beriladi va millimetrga o'giriladi.
 * Shablon yozayotganda "chapdan 8 mm" emas, "kenglikning 8%"
 * deb o'ylash kerak — shunda u har qanday o'lchamda to'g'ri
 * ko'rinadi.
 */
class TemplateScope(
    private val document: DesignDocument,
    private val idPrefix: String
) {

    private var counter = 0

    private val layers = mutableListOf<DesignLayer>()

    val widthMm: Float get() = document.widthMm

    val heightMm: Float get() = document.heightMm

    private fun nextId(): String = "$idPrefix-${counter++}"

    /** Nisbatdan millimetrga: gorizontal. */
    fun x(fraction: Float): Float = widthMm * fraction

    /** Nisbatdan millimetrga: vertikal. */
    fun y(fraction: Float): Float = heightMm * fraction

    fun rect(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        fill: androidx.compose.ui.graphics.Color,
        cornerMm: Float = 0f,
        name: String = "Shakl"
    ) {
        layers += ShapeLayer(
            id = nextId(),
            name = name,
            transform = LayerTransform(
                xMm = x(left),
                yMm = y(top),
                widthMm = x(right - left),
                heightMm = y(bottom - top)
            ),
            kind = ShapeKind.RECTANGLE,
            fill = fill,
            cornerRadiusMm = cornerMm
        )
    }

    fun circle(
        centerX: Float,
        centerY: Float,
        diameterMm: Float,
        fill: androidx.compose.ui.graphics.Color,
        name: String = "Aylana"
    ) {
        layers += ShapeLayer(
            id = nextId(),
            name = name,
            transform = LayerTransform(
                xMm = x(centerX) - diameterMm / 2f,
                yMm = y(centerY) - diameterMm / 2f,
                widthMm = diameterMm,
                heightMm = diameterMm
            ),
            kind = ShapeKind.ELLIPSE,
            fill = fill
        )
    }

    fun line(
        left: Float,
        top: Float,
        right: Float,
        thicknessMm: Float,
        color: androidx.compose.ui.graphics.Color
    ) {
        layers += ShapeLayer(
            id = nextId(),
            name = "Chiziq",
            transform = LayerTransform(
                xMm = x(left),
                yMm = y(top),
                widthMm = x(right - left),
                heightMm = thicknessMm
            ),
            kind = ShapeKind.RECTANGLE,
            fill = color
        )
    }

    /**
     * Matn.
     *
     * @param sizeFraction shrift balandligi maket BALANDLIGIGA
     *        nisbatan. Kenglikka emas: shrift vertikal o'lchov,
     *        va vizitka gorizontal cho'zilsa harflar kattalashib
     *        ketmasligi kerak.
     */
    fun text(
        value: String,
        left: Float,
        top: Float,
        right: Float,
        sizeFraction: Float,
        color: androidx.compose.ui.graphics.Color,
        font: DesignFont = DesignFont.SANS,
        bold: Boolean = false,
        align: TextAlign = TextAlign.START,
        letterSpacingMm: Float = 0f,
        upper: Boolean = false,
        name: String = "Matn"
    ) {

        val fontSize = heightMm * sizeFraction

        layers += TextLayer(
            id = nextId(),
            name = name,
            transform = LayerTransform(
                xMm = x(left),
                yMm = y(top),
                widthMm = x(right - left),

                // Balandlik shriftdan 1.6 barobar: bir qatorli
                // matn uchun yetarli zaxira, ikki qatorga
                // o'tganda foydalanuvchi o'zi cho'zadi.
                heightMm = fontSize * 1.6f
            ),
            text = value,
            fontSizeMm = fontSize,
            font = font,
            color = color,
            isBold = bold,
            align = align,
            letterSpacingMm = letterSpacingMm,
            textCase = if (upper) TextCase.UPPER else TextCase.NORMAL
        )
    }

    internal fun result(): List<DesignLayer> = layers.toList()
}

/** Shablonni qulay yozish uchun. */
fun template(
    id: String,
    name: String,
    category: ProductCategory,
    block: TemplateScope.() -> Unit
): DesignTemplate = DesignTemplate(
    id = id,
    name = name,
    category = category,
    build = { document ->

        // id prefiksi har chaqiruvda yangi — bitta shablonni
        // ikki marta qo'llaganda qatlam id'lari to'qnashmasligi
        // kerak.
        val scope = TemplateScope(
            document = document,
            idPrefix = "$id-${System.currentTimeMillis()}"
        )

        scope.block()

        scope.result()
    }
)