package uz.myprint.feature.feature.design.studio.domain

import uz.myprint.feature.feature.product.domain.model.ProductCategory
import uz.myprint.feature.feature.product.domain.model.ProductSize
import uz.myprint.feature.feature.product.domain.model.SizeUnit

/**
 * MAHSULOT O'LCHAMI ≠ BOSMA MAYDONI.
 *
 * Shu farq e'tibordan chetda qolgani uchun bakal noto'g'ri
 * o'lchamda ochilardi. Katalogdagi `mugStandard` 82 × 95 mm —
 * bu bakalning DIAMETRI va BALANDLIGI, ya'ni jismoniy o'lchami.
 * Bosiladigan narsa esa uning YOYILGAN yuzasi.
 *
 * To'liq aylana: π × 82 ≈ 257 mm. Lekin dasta atrofidagi ~47 mm
 * ga bosib bo'lmaydi, shuning uchun amaliy bosma eni ≈ 210 mm.
 *
 * Xuddi shu mantiq boshqa mahsulotlarda ham kerak:
 *   futbolka   — 510 × 710 mm ko'ylakning o'zi, bosma maydoni
 *                ko'krakdagi ~280 × 380 mm
 *   banner     — o'lcham to'g'ri, lekin bleed 2 mm emas, qayrilma
 *                uchun 50 mm kerak
 *   roll-up    — pastki qismi mexanizm ichida qolib ketadi
 */
data class PrintSurface(

    val widthMm: Float,

    val heightMm: Float,

    val bleedMm: Float,

    val safeMarginMm: Float,

    /**
     * Foydalanuvchiga ko'rsatiladigan izoh.
     *
     * Bakal 82 mm deb tanlangan-u, studioda 210 mm chiqsa, buni
     * tushuntirmasa mijoz xato deb o'ylaydi.
     */
    val note: String? = null
) {

    companion object {

        /** Katalog birligini millimetrga keltiradi. */
        private fun ProductSize.toMm(): Pair<Float, Float> {

            val factor = when (unit) {
                SizeUnit.MM -> 1f
                SizeUnit.CM -> 10f
                SizeUnit.M -> 1000f
            }

            return width * factor to height * factor
        }

        /**
         * Bakalning yoyilgan yuzasi.
         *
         * @param diameterMm katalogdagi eni — bu diametr
         * @param handleClearanceMm dasta atrofida bo'sh qoladigan yoy
         */
        private fun mugWrap(
            diameterMm: Float,
            heightMm: Float,
            handleClearanceMm: Float = 47f
        ): PrintSurface {

            val circumference = (Math.PI * diameterMm).toFloat()

            val printable = (circumference - handleClearanceMm)
                .coerceAtLeast(60f)

            return PrintSurface(

                // 5 mm ga yaxlitlanadi: poligrafiyada 209.7 mm degan
                // o'lcham bilan ishlash noqulay, mijoz ham
                // tushunmaydi.
                widthMm = (printable / 5f).toInt() * 5f,
                heightMm = heightMm + 1f,
                bleedMm = 3f,
                safeMarginMm = 5f,
                note = "Bakal aylanasi (yoyilgan)"
            )
        }

        /**
         * Mahsulot o'lchamidan bosma maydonini hisoblaydi.
         *
         * Erkin (custom) o'lcham berilganda mahsulot turi baribir
         * hisobga olinadi — foydalanuvchi "2 × 3 m banner" desa,
         * unga ham 50 mm qayrilma kerak.
         */
        fun forProduct(
            category: ProductCategory,
            size: ProductSize
        ): PrintSurface {

            val (w, h) = size.toMm()

            return when (category) {

                ProductCategory.MUG -> mugWrap(
                    diameterMm = w,
                    heightMm = h
                )

                ProductCategory.T_SHIRT -> PrintSurface(

                    // Ko'krak bosmasi A3 dan oshmaydi — termopress
                    // plitasi shundan katta emas.
                    widthMm = minOf(w * 0.55f, 297f),
                    heightMm = minOf(h * 0.54f, 400f),
                    bleedMm = 0f,
                    safeMarginMm = 10f,
                    note = "Ko'krak bosma maydoni"
                )

                ProductCategory.BANNER -> PrintSurface(
                    widthMm = w,
                    heightMm = h,
                    bleedMm = 50f,
                    safeMarginMm = 70f,
                    note = "Chetdan 50 mm qayrilmaga ketadi"
                )

                ProductCategory.ROLL_UP -> PrintSurface(
                    widthMm = w,
                    heightMm = h,
                    bleedMm = 20f,
                    safeMarginMm = 30f,
                    note = "Pastki 150 mm mexanizmda ko'rinmaydi"
                )

                ProductCategory.X_BANNER -> PrintSurface(
                    widthMm = w,
                    heightMm = h,
                    bleedMm = 20f,
                    safeMarginMm = 30f
                )

                ProductCategory.STICKER,
                ProductCategory.LABEL -> PrintSurface(
                    widthMm = w,
                    heightMm = h,
                    bleedMm = 3f,
                    safeMarginMm = 3f
                )

                else -> PrintSurface(
                    widthMm = w,
                    heightMm = h,
                    bleedMm = 2f,
                    safeMarginMm = 3f
                )
            }
        }
    }
}