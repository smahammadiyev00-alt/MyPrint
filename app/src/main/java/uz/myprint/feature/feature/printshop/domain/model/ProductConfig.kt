package uz.myprint.feature.feature.printshop.domain.model

import uz.myprint.feature.feature.product.domain.model.ProductCategory
import uz.myprint.feature.feature.product.domain.model.ProductMaterial
import uz.myprint.feature.feature.product.domain.model.ProductPrintType
import uz.myprint.feature.feature.product.domain.model.ProductSize

/**
 * Mijoz tanlagan konfiguratsiya.
 *
 * Soni har doim qatorlar orqali beriladi. Vizitka uchun bitta qator,
 * futbolka uchun har o'lcham bo'yicha alohida qator — shunda narx
 * hisoblash mantiqi ikkala holat uchun bir xil bo'ladi.
 */
data class ProductConfig(
    val productId: String,
    val category: ProductCategory,
    val material: ProductMaterial? = null,
    val printType: ProductPrintType? = null,
    val lines: List<ConfigLine> = emptyList(),
    val isRush: Boolean = false,
    val needsDelivery: Boolean = false
) {

    /** Barcha o'lchamlar bo'yicha jami. */
    val quantity: Int
        get() = lines.sumOf { it.quantity }

    /**
     * O'lchami bitta bo'lgan mahsulotlar uchun qulaylik.
     * Futbolkada ma'nosi yo'q — u yerda lines ishlatiladi.
     */
    val size: ProductSize?
        get() = lines.firstOrNull()?.size

    /** Material va bosma turi uchun qo'shimcha, o'lchamsiz. */
    val optionsPricePerUnit: Long
        get() = (material?.additionalPrice ?: 0L) +
                (printType?.additionalPrice ?: 0L)

    companion object {

        /**
         * Navigatsiya argumenti uchun: "m:10,l:15,xl:5".
         * Nol miqdorli qatorlar tashlab yuboriladi.
         */
        fun encodeLines(lines: List<ConfigLine>): String =
            lines
                .filter { it.quantity > 0 }
                .joinToString(",") { line ->
                    "${line.size?.id.orEmpty()}:${line.quantity}"
                }
                .ifBlank { ":0" }

        /**
         * "m:10,l:15,xl:5" -> qatorlar. O'lchamlar mahsulotdan topiladi.
         */
        fun decodeLines(
            encoded: String,
            availableSizes: List<ProductSize>
        ): List<ConfigLine> =
            encoded
                .split(",")
                .mapNotNull { part ->

                    val pieces = part.split(":")

                    if (pieces.size != 2) return@mapNotNull null

                    val quantity = pieces[1].toIntOrNull() ?: return@mapNotNull null

                    if (quantity <= 0) return@mapNotNull null

                    ConfigLine(
                        size = availableSizes.firstOrNull { it.id == pieces[0] },
                        quantity = quantity
                    )
                }
    }
}
