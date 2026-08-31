package uz.myprint.feature.feature.design.studio.data

import androidx.compose.ui.graphics.Color
import uz.myprint.feature.feature.design.studio.domain.DesignFont
import uz.myprint.feature.feature.design.studio.domain.DesignTemplate
import uz.myprint.feature.feature.design.studio.domain.TextAlign
import uz.myprint.feature.feature.design.studio.domain.template
import uz.myprint.feature.feature.product.domain.model.ProductCategory

/**
 * SHABLONLAR KATALOGI.
 *
 * Telefonda noldan dizayn qiladigan odam kam — ekran kichik,
 * barmoq yo'g'on, sabr esa cheklangan. Shablon esa mijozga
 * "matnni almashtir va tayyor" degan yo'l beradi.
 *
 * Hamma o'lchamlar NISBATDA. Bitta shablon 90×50 vizitkada ham,
 * 85×55 da ham to'g'ri ko'rinadi.
 *
 * Matn namunalari ataylab o'zbekcha va real: "Ismingiz Familiya"
 * emas, aniq ko'rinadigan misol. Foydalanuvchi nimani
 * almashtirishi kerakligini darhol tushunadi.
 */
object TemplateCatalog {

    // ---- Ranglar ----

    private val Purple = Color(0xFF7B4DFF)
    private val PurpleDark = Color(0xFF4C1D95)
    private val Ink = Color(0xFF111827)
    private val Slate = Color(0xFF334155)
    private val Gray = Color(0xFF6B7280)
    private val Cream = Color(0xFFF8F5EF)
    private val Gold = Color(0xFFC9A227)
    private val Emerald = Color(0xFF047857)
    private val Crimson = Color(0xFFB91C1C)
    private val White = Color.White

    fun forCategory(category: ProductCategory): List<DesignTemplate> =
        when (category) {
            ProductCategory.BUSINESS_CARD -> businessCards
            ProductCategory.MUG -> mugs
            ProductCategory.BANNER,
            ProductCategory.ROLL_UP,
            ProductCategory.X_BANNER -> banners
            else -> businessCards
        }

    // =================================================================
    //  VIZITKA
    // =================================================================

    private val businessCards = listOf(

        template("bc_bar", "Chap tasma", ProductCategory.BUSINESS_CARD) {

            rect(0f, 0f, 0.06f, 1f, Purple, name = "Tasma")

            text(
                "Jahongir Karimov", 0.14f, 0.20f, 0.95f, 0.15f,
                Ink, DesignFont.MONTSERRAT, bold = true, name = "Ism"
            )

            text(
                "Grafik dizayner", 0.14f, 0.40f, 0.95f, 0.095f,
                Purple, DesignFont.MONTSERRAT, name = "Lavozim"
            )

            line(0.14f, 0.58f, 0.42f, 0.4f, Purple)

            text(
                "+998 90 123 45 67", 0.14f, 0.66f, 0.95f, 0.085f,
                Slate, DesignFont.OPEN_SANS, name = "Telefon"
            )

            text(
                "info@myprint.uz", 0.14f, 0.80f, 0.95f, 0.085f,
                Gray, DesignFont.OPEN_SANS, name = "Pochta"
            )
        },

        template("bc_dark", "To'q fon", ProductCategory.BUSINESS_CARD) {

            rect(0f, 0f, 1f, 1f, Ink, name = "Fon")

            text(
                "MYPRINT", 0.08f, 0.18f, 0.92f, 0.17f,
                White, DesignFont.MONTSERRAT, bold = true,
                letterSpacingMm = 1.2f, upper = true, name = "Nom"
            )

            line(0.08f, 0.44f, 0.30f, 0.5f, Purple)

            text(
                "Poligrafiya xizmatlari", 0.08f, 0.52f, 0.92f, 0.085f,
                Color(0xFF9CA3AF), DesignFont.OPEN_SANS, name = "Tavsif"
            )

            text(
                "+998 90 123 45 67", 0.08f, 0.74f, 0.92f, 0.09f,
                White, DesignFont.OPEN_SANS, bold = true, name = "Telefon"
            )
        },

        template("bc_center", "Markazda", ProductCategory.BUSINESS_CARD) {

            rect(0f, 0f, 1f, 1f, Cream, name = "Fon")

            text(
                "Sr.Print", 0.10f, 0.22f, 0.90f, 0.20f,
                Ink, DesignFont.PLAYFAIR, bold = true,
                align = TextAlign.CENTER, name = "Logotip"
            )

            line(0.38f, 0.50f, 0.62f, 0.4f, Gold)

            text(
                "Bosmaxona", 0.10f, 0.57f, 0.90f, 0.08f,
                Gray, DesignFont.LORA, align = TextAlign.CENTER,
                letterSpacingMm = 0.8f, upper = true, name = "Tavsif"
            )

            text(
                "88 171-11-33", 0.10f, 0.76f, 0.90f, 0.10f,
                Gold, DesignFont.PLAYFAIR, bold = true,
                align = TextAlign.CENTER, name = "Telefon"
            )
        },

        template("bc_bottom", "Pastki tasma", ProductCategory.BUSINESS_CARD) {

            rect(0f, 0f, 1f, 1f, Cream, name = "Fon")

            rect(0f, 0.72f, 1f, 1f, Ink, name = "Tasma")

            text(
                "Sr.Print", 0.08f, 0.18f, 0.92f, 0.22f,
                Ink, DesignFont.CAVEAT, bold = true,
                align = TextAlign.CENTER, name = "Logotip"
            )

            text(
                "88 171-11-33", 0.06f, 0.79f, 0.94f, 0.11f,
                White, DesignFont.MONTSERRAT, bold = true,
                align = TextAlign.CENTER, name = "Telefon"
            )
        },

        template("bc_split", "Ikki rang", ProductCategory.BUSINESS_CARD) {

            rect(0f, 0f, 0.38f, 1f, PurpleDark, name = "Chap")

            rect(0.38f, 0f, 1f, 1f, White, name = "O'ng")

            circle(0.19f, 0.5f, heightMm * 0.30f, Purple, name = "Logo joyi")

            text(
                "Aziza Rahimova", 0.46f, 0.22f, 0.96f, 0.13f,
                Ink, DesignFont.RUBIK, bold = true, name = "Ism"
            )

            text(
                "Menejer", 0.46f, 0.40f, 0.96f, 0.085f,
                Purple, DesignFont.RUBIK, name = "Lavozim"
            )

            text(
                "+998 90 123 45 67", 0.46f, 0.60f, 0.96f, 0.08f,
                Slate, DesignFont.OPEN_SANS, name = "Telefon"
            )

            text(
                "Toshkent sh.", 0.46f, 0.75f, 0.96f, 0.08f,
                Gray, DesignFont.OPEN_SANS, name = "Manzil"
            )
        },

        template("bc_top", "Yuqori tasma", ProductCategory.BUSINESS_CARD) {

            rect(0f, 0f, 1f, 0.26f, Emerald, name = "Tasma")

            text(
                "AGRO SAVDO", 0.07f, 0.06f, 0.93f, 0.11f,
                White, DesignFont.OSWALD, letterSpacingMm = 0.6f,
                upper = true, name = "Kompaniya"
            )

            text(
                "Bekzod Toshmatov", 0.07f, 0.38f, 0.93f, 0.13f,
                Ink, DesignFont.OSWALD, bold = true, name = "Ism"
            )

            text(
                "Sotuv bo'limi boshlig'i", 0.07f, 0.55f, 0.93f, 0.08f,
                Gray, DesignFont.OPEN_SANS, name = "Lavozim"
            )

            text(
                "+998 90 123 45 67  ·  agro.uz", 0.07f, 0.78f, 0.93f, 0.08f,
                Emerald, DesignFont.OPEN_SANS, bold = true, name = "Aloqa"
            )
        },

        template("bc_frame", "Ramka", ProductCategory.BUSINESS_CARD) {

            rect(0f, 0f, 1f, 1f, Crimson, name = "Fon")

            rect(0.05f, 0.09f, 0.95f, 0.91f, White, cornerMm = 1f, name = "Ichki")

            text(
                "Nilufar Yo'ldosheva", 0.11f, 0.24f, 0.89f, 0.12f,
                Ink, DesignFont.LORA, bold = true,
                align = TextAlign.CENTER, name = "Ism"
            )

            text(
                "Yuridik maslahatchi", 0.11f, 0.43f, 0.89f, 0.08f,
                Crimson, DesignFont.LORA, align = TextAlign.CENTER,
                name = "Lavozim"
            )

            text(
                "+998 90 123 45 67", 0.11f, 0.66f, 0.89f, 0.085f,
                Slate, DesignFont.OPEN_SANS, align = TextAlign.CENTER,
                name = "Telefon"
            )
        },

        template("bc_initial", "Katta harf", ProductCategory.BUSINESS_CARD) {

            rect(0f, 0f, 1f, 1f, White, name = "Fon")

            circle(0.17f, 0.35f, heightMm * 0.34f, Purple, name = "Doira")

            text(
                "M", 0.06f, 0.20f, 0.28f, 0.22f,
                White, DesignFont.MONTSERRAT, bold = true,
                align = TextAlign.CENTER, name = "Harf"
            )

            text(
                "MyPrint", 0.06f, 0.62f, 0.94f, 0.15f,
                Ink, DesignFont.MONTSERRAT, bold = true, name = "Nom"
            )

            text(
                "+998 90 123 45 67", 0.06f, 0.83f, 0.94f, 0.08f,
                Gray, DesignFont.OPEN_SANS, name = "Telefon"
            )
        },

        template("bc_minimal", "Minimal", ProductCategory.BUSINESS_CARD) {

            text(
                "SANJAR", 0.08f, 0.30f, 0.92f, 0.16f,
                Ink, DesignFont.BEBAS, letterSpacingMm = 2f,
                upper = true, name = "Ism"
            )

            text(
                "photographer", 0.08f, 0.52f, 0.92f, 0.07f,
                Gray, DesignFont.OPEN_SANS, letterSpacingMm = 1.5f,
                name = "Kasb"
            )

            line(0.08f, 0.68f, 0.92f, 0.25f, Color(0xFFE5E7EB))

            text(
                "+998 90 123 45 67", 0.08f, 0.76f, 0.92f, 0.07f,
                Slate, DesignFont.OPEN_SANS, name = "Telefon"
            )
        },

        template("bc_gold", "Oltin", ProductCategory.BUSINESS_CARD) {

            rect(0f, 0f, 1f, 1f, Ink, name = "Fon")

            rect(0.04f, 0.08f, 0.96f, 0.92f, Color(0x00000000),
                name = "Ichki")

            line(0.08f, 0.30f, 0.92f, 0.3f, Gold)

            text(
                "LUXE STUDIO", 0.08f, 0.36f, 0.92f, 0.13f,
                Gold, DesignFont.PLAYFAIR, bold = true,
                align = TextAlign.CENTER, letterSpacingMm = 1f,
                name = "Nom"
            )

            line(0.08f, 0.58f, 0.92f, 0.3f, Gold)

            text(
                "+998 90 123 45 67", 0.08f, 0.70f, 0.92f, 0.075f,
                White, DesignFont.OPEN_SANS, align = TextAlign.CENTER,
                letterSpacingMm = 0.5f, name = "Telefon"
            )
        }
    )

    // =================================================================
    //  BAKAL
    // =================================================================

    private val mugs = listOf(

        template("mug_text", "Katta yozuv", ProductCategory.MUG) {

            // Dasta chapda ~28% joyni egallaydi, matn o'ng
            // tomonga suriladi.
            text(
                "Eng zo'r ota", 0.34f, 0.32f, 0.94f, 0.22f,
                Ink, DesignFont.MONTSERRAT, bold = true,
                align = TextAlign.CENTER, name = "Yozuv"
            )
        },

        template("mug_frame", "Ramkali", ProductCategory.MUG) {

            rect(0.34f, 0.15f, 0.94f, 0.85f, Purple, cornerMm = 6f,
                name = "Ramka")

            text(
                "MyPrint", 0.36f, 0.36f, 0.92f, 0.20f,
                White, DesignFont.CAVEAT, bold = true,
                align = TextAlign.CENTER, name = "Yozuv"
            )
        }
    )

    // =================================================================
    //  BANNER
    // =================================================================

    private val banners = listOf(

        template("ban_sale", "Chegirma", ProductCategory.BANNER) {

            rect(0f, 0f, 1f, 1f, Crimson, name = "Fon")

            text(
                "CHEGIRMA", 0.05f, 0.18f, 0.95f, 0.16f,
                White, DesignFont.OSWALD, bold = true,
                align = TextAlign.CENTER, letterSpacingMm = 4f,
                upper = true, name = "Sarlavha"
            )

            text(
                "50%", 0.05f, 0.38f, 0.95f, 0.30f,
                Gold, DesignFont.OSWALD, bold = true,
                align = TextAlign.CENTER, name = "Foiz"
            )

            text(
                "+998 90 123 45 67", 0.05f, 0.76f, 0.95f, 0.08f,
                White, DesignFont.MONTSERRAT, bold = true,
                align = TextAlign.CENTER, name = "Telefon"
            )
        },

        template("ban_open", "Ochilish", ProductCategory.BANNER) {

            rect(0f, 0f, 1f, 1f, White, name = "Fon")

            rect(0f, 0f, 1f, 0.14f, Emerald, name = "Yuqori")

            rect(0f, 0.86f, 1f, 1f, Emerald, name = "Past")

            text(
                "TANTANALI OCHILISH", 0.05f, 0.30f, 0.95f, 0.13f,
                Emerald, DesignFont.MONTSERRAT, bold = true,
                align = TextAlign.CENTER, upper = true,
                name = "Sarlavha"
            )

            text(
                "Sizni kutamiz!", 0.05f, 0.52f, 0.95f, 0.10f,
                Ink, DesignFont.OPEN_SANS, align = TextAlign.CENTER,
                name = "Matn"
            )

            text(
                "Toshkent sh., Amir Temur 12", 0.05f, 0.68f, 0.95f, 0.07f,
                Gray, DesignFont.OPEN_SANS, align = TextAlign.CENTER,
                name = "Manzil"
            )
        }
    )
}
