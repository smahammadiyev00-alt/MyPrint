package uz.myprint.feature.feature.design.studio.domain

import androidx.compose.ui.graphics.Color
import uz.myprint.feature.feature.product.domain.model.ProductCategory

/**
 * BOSMA YUZASI.
 *
 * PrintSurface dan farqi muhim va ular chalkashmasligi kerak:
 *
 *   PrintSurface   — mahsulot o'lchamidan bosma maydonini
 *                    HISOBLAYDI. Bakal 82 mm diametr → 210 mm
 *                    yoyilgan yuza. Bitta natija qaytaradi.
 *
 *   ProductSurface — mahsulotning BITTA yuzasini tasvirlaydi:
 *                    o'lchami, yo'riqchilari, taqiqlangan
 *                    zonalari va o'z qatlamlari.
 *
 * Ya'ni PrintSurface hisoblaydi, ProductSurface saqlaydi.
 *
 * HOZIRCHA ISHLATILMAYDI: DesignDocument bitta yuza bilan
 * ishlaydi. Bu model vizitkaning orqa tomoni qo'shilganda ishga
 * tushadi — o'shanda studio ekraniga tab qatori qo'shiladi.
 *
 * Vizitka, bakal, banner va futbolka bir-biridan mahsulot sifatida
 * emas, YUZA sifatida farq qiladi:
 *
 *   vizitka   — 2 ta tekis yuza (old / orqa), bleed bor
 *   bakal     — 1 ta yoyilgan yuza, dasta joyi bosilmaydi, birikish
 *               chizig'i bor, 3D o'rash ko'rinishi kerak
 *   banner    — 1 ta juda katta yuza, chetida luvers zonasi bor,
 *               bleed o'rniga qayrilma (hem) hisoblanadi
 *   futbolka  — 4 ta yuza (old, orqa, yeng), bleed yo'q, o'rniga
 *               mato ustidagi bosma maydoni
 *
 * Shuning uchun DesignStudioScreen mahsulot turini BILMASLIGI kerak.
 * U faqat "menda shu yuza bor, uning yo'riqchilari shular" deb
 * ishlaydi. Yangi mahsulot qo'shish = yangi preset yozish, yangi
 * ekran emas.
 */
data class ProductSurface(

    val id: String,

    /** Foydalanuvchiga ko'rinadigan nom: "Old tomoni", "Yeng o'ng". */
    val title: String,

    /** Kesim (trim) o'lchami, mm. */
    val widthMm: Float,

    val heightMm: Float,

    /**
     * Kesimdan tashqariga chiqadigan zaxira.
     * Futbolkada 0 — mato kesilmaydi.
     */
    val bleedMm: Float = 2f,

    /** Kesimdan ichkaridagi xavfsiz chegara. */
    val safeMarginMm: Float = 3f,

    val background: Color = Color.White,

    /**
     * Bu yuzada bosib bo'lmaydigan yoki ehtiyot bo'lish kerak
     * bo'lgan joylar: bakal dastasi, banner luversi, futbolka choki.
     */
    val zones: List<SurfaceZone> = emptyList(),

    /** Kanvas yonida qanday oldindan ko'rsatish kerakligi. */
    val preview: SurfacePreview = SurfacePreview.Flat,

    val layers: List<DesignLayer> = emptyList()

) {

    val fullWidthMm: Float get() = widthMm + bleedMm * 2f

    val fullHeightMm: Float get() = heightMm + bleedMm * 2f

    fun layerById(id: String): DesignLayer? =
        layers.firstOrNull { it.id == id }

    /**
     * Ogohlantirishga sabab bo'ladigan qatlamlar.
     *
     * Ikki xil sabab bor va ular bir xil emas: xavfsiz maydondan
     * chiqish "kesilib qolishi mumkin", taqiqlangan zonaga tushish
     * esa "umuman bosilmaydi". Ikkinchisi jiddiyroq.
     */
    fun issues(): List<SurfaceIssue> {

        val result = mutableListOf<SurfaceIssue>()

        layers.filter { it.isVisible }.forEach { layer ->

            val t = layer.transform

            val outsideSafe = t.xMm < safeMarginMm ||
                    t.yMm < safeMarginMm ||
                    t.xMm + t.widthMm > widthMm - safeMarginMm ||
                    t.yMm + t.heightMm > heightMm - safeMarginMm

            if (outsideSafe) {
                result += SurfaceIssue(
                    layerId = layer.id,
                    severity = IssueSeverity.WARNING,
                    message = "Xavfsiz maydondan chiqqan"
                )
            }

            zones.filter { it.kind == ZoneKind.NO_PRINT }.forEach { zone ->

                if (zone.intersects(t)) {
                    result += SurfaceIssue(
                        layerId = layer.id,
                        severity = IssueSeverity.ERROR,
                        message = "\"${zone.title}\" joyiga tushgan — bosilmaydi"
                    )
                }
            }
        }

        return result
    }
}

/**
 * Yuzadagi maxsus hudud.
 *
 * Koordinatalar kesim chizig'ining chap yuqori burchagidan, mm.
 */
data class SurfaceZone(

    val id: String,

    val title: String,

    val xMm: Float,

    val yMm: Float,

    val widthMm: Float,

    val heightMm: Float,

    val kind: ZoneKind,

    val color: Color

) {

    fun intersects(t: LayerTransform): Boolean =
        t.xMm < xMm + widthMm &&
                t.xMm + t.widthMm > xMm &&
                t.yMm < yMm + heightMm &&
                t.yMm + t.heightMm > yMm
}

enum class ZoneKind {

    /** Umuman bosilmaydi — bakal dastasi, banner luversi. */
    NO_PRINT,

    /** Bosiladi, lekin ko'rinmay qolishi mumkin — bakal birikishi. */
    CAUTION,

    /** Faqat tavsiya — logotip uchun qulay joy. */
    HINT
}

enum class IssueSeverity { WARNING, ERROR }

data class SurfaceIssue(
    val layerId: String,
    val severity: IssueSeverity,
    val message: String
)

/**
 * Yuza qanday ko'rsatiladi.
 *
 * Bu MyPrint mokapidagi o'ng paneldagi 3D/Wrap/Flat blokini
 * boshqaradi. Sinf ichida rasm chizilmaydi — faqat "qanday
 * chizilsin" degan ma'lumot. Chizishning o'zi presentation
 * qatlamida.
 */
sealed interface SurfacePreview {

    /** Faqat tekis ko'rinish. Vizitka, banner, stiker. */
    data object Flat : SurfacePreview

    /**
     * Silindrga o'raladi. Bakal, termos, banka.
     *
     * @param diameterMm silindr diametri — 3D egrilik shundan
     * @param handleSide dasta qaysi tomonda ko'rinadi
     */
    data class Cylinder(
        val diameterMm: Float,
        val handleSide: HandleSide = HandleSide.LEFT
    ) : SurfacePreview

    /**
     * Mahsulot fotosi ustiga qo'yiladi. Futbolka, sumka, kepka.
     *
     * @param mockupAsset drawable/asset nomi
     * @param areaLeft..areaBottom bosma maydonining mokap rasmidagi
     *        o'rni, 0..1 nisbatda — piksel emas, chunki mokap
     *        rasmining o'lchami o'zgarishi mumkin
     */
    data class Mockup(
        val mockupAsset: String,
        val areaLeft: Float,
        val areaTop: Float,
        val areaRight: Float,
        val areaBottom: Float
    ) : SurfacePreview
}

enum class HandleSide { LEFT, RIGHT }

/**
 * Tayyor yuzalar.
 *
 * Yangi mahsulot qo'shganda FAQAT shu obyektga qo'shiladi.
 * Studio ekraniga tegilmaydi.
 */
object SurfacePresets {

    private val ZoneNoPrint = Color(0xFF7B4DFF)
    private val ZoneCaution = Color(0xFFEF4444)
    private val ZoneHint = Color(0xFF22C55E)

    /** Standart vizitka: old va orqa tomon. */
    fun businessCard(
        widthMm: Float = 90f,
        heightMm: Float = 50f
    ): List<ProductSurface> = listOf(

        ProductSurface(
            id = "front",
            title = "Old tomoni",
            widthMm = widthMm,
            heightMm = heightMm,
            bleedMm = 2f,
            safeMarginMm = 3f
        ),

        ProductSurface(
            id = "back",
            title = "Orqa tomoni",
            widthMm = widthMm,
            heightMm = heightMm,
            bleedMm = 2f,
            safeMarginMm = 3f
        )
    )

    /**
     * Bakal — yoyilgan holda 210 × 96 mm.
     *
     * Dasta chapda 60 mm joyni egallaydi va u yerga tushgan narsa
     * umuman bosilmaydi. O'ng chetdagi 15 mm — ikki chet birikadigan
     * joy, u yerdagi tasvir bo'linib ko'rinadi.
     */
    fun mug(
        widthMm: Float = 210f,
        heightMm: Float = 96f,
        handleWidthMm: Float = 60f,
        joinWidthMm: Float = 15f
    ): List<ProductSurface> = listOf(

        ProductSurface(
            id = "wrap",
            title = "Bakal aylanasi",
            widthMm = widthMm,
            heightMm = heightMm,
            bleedMm = 3f,
            safeMarginMm = 5f,
            zones = listOf(

                SurfaceZone(
                    id = "handle",
                    title = "Dasta joyi",
                    xMm = 0f,
                    yMm = 0f,
                    widthMm = handleWidthMm,
                    heightMm = heightMm,
                    kind = ZoneKind.NO_PRINT,
                    color = ZoneNoPrint
                ),

                SurfaceZone(
                    id = "join",
                    title = "Birikish chizig'i",
                    xMm = widthMm - joinWidthMm,
                    yMm = 0f,
                    widthMm = joinWidthMm,
                    heightMm = heightMm,
                    kind = ZoneKind.CAUTION,
                    color = ZoneCaution
                )
            ),
            preview = SurfacePreview.Cylinder(
                diameterMm = widthMm / Math.PI.toFloat(),
                handleSide = HandleSide.LEFT
            )
        )
    )

    /**
     * Banner.
     *
     * Bleed o'rniga qayrilma: chetdan 50 mm mato orqaga buklanadi
     * va choklanadi, u joydagi hech narsa ko'rinmaydi. Luvers
     * teshiklari har 500 mm da chetdan 25 mm ichkarida.
     */
    fun banner(
        widthMm: Float,
        heightMm: Float,
        hemMm: Float = 50f,
        eyeletInsetMm: Float = 25f
    ): List<ProductSurface> {

        val zones = mutableListOf(
            SurfaceZone(
                id = "hem",
                title = "Qayrilma (chok)",
                xMm = 0f,
                yMm = 0f,
                widthMm = widthMm,
                heightMm = hemMm,
                kind = ZoneKind.NO_PRINT,
                color = ZoneNoPrint
            )
        )

        // Luvers teshiklari — har biri kichik doira, lekin
        // to'rtburchak sifatida tekshirilgani yetarli.
        val step = 500f
        var x = eyeletInsetMm

        while (x <= widthMm - eyeletInsetMm) {

            zones += SurfaceZone(
                id = "eyelet_top_$x",
                title = "Luvers",
                xMm = x - 10f,
                yMm = eyeletInsetMm - 10f,
                widthMm = 20f,
                heightMm = 20f,
                kind = ZoneKind.CAUTION,
                color = ZoneCaution
            )

            x += step
        }

        return listOf(
            ProductSurface(
                id = "face",
                title = "Banner yuzasi",
                widthMm = widthMm,
                heightMm = heightMm,
                bleedMm = hemMm,
                safeMarginMm = hemMm + 20f,
                zones = zones
            )
        )
    }

    /** Futbolka — to'rt yuza, kesim yo'q, mokap ustida ko'rsatiladi. */
    fun tShirt(): List<ProductSurface> = listOf(

        mockupSurface("front", "Old tomoni", 280f, 380f, "tshirt_front"),
        mockupSurface("back", "Orqa tomoni", 280f, 380f, "tshirt_back"),
        mockupSurface("sleeve_r", "Yeng o'ng", 90f, 70f, "tshirt_sleeve_r"),
        mockupSurface("sleeve_l", "Yeng chap", 90f, 70f, "tshirt_sleeve_l")
    )

    private fun mockupSurface(
        id: String,
        title: String,
        widthMm: Float,
        heightMm: Float,
        asset: String
    ) = ProductSurface(
        id = id,
        title = title,
        widthMm = widthMm,
        heightMm = heightMm,
        bleedMm = 0f,
        safeMarginMm = 10f,
        preview = SurfacePreview.Mockup(
            mockupAsset = asset,
            areaLeft = 0.30f,
            areaTop = 0.22f,
            areaRight = 0.70f,
            areaBottom = 0.68f
        )
    )

    /**
     * Kategoriya bo'yicha standart yuzalar.
     *
     * O'lcham mahsulotdan keladi — bu yerda faqat qanday yuzalar
     * borligi va ularning yo'riqchilari aniqlanadi.
     */
    fun forCategory(
        category: ProductCategory,
        widthMm: Float,
        heightMm: Float
    ): List<ProductSurface> = when (category) {

        ProductCategory.BUSINESS_CARD -> businessCard(widthMm, heightMm)

        ProductCategory.MUG -> mug(widthMm, heightMm)

        ProductCategory.T_SHIRT -> tShirt()

        ProductCategory.BANNER,
        ProductCategory.ROLL_UP,
        ProductCategory.X_BANNER -> banner(widthMm, heightMm)

        else -> listOf(
            ProductSurface(
                id = "main",
                title = "Maket",
                widthMm = widthMm,
                heightMm = heightMm
            )
        )
    }
}