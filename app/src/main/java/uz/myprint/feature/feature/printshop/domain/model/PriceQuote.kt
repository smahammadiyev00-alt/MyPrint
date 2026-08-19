package uz.myprint.feature.feature.printshop.domain.model

/**
 * Bitta poligrafiyaning bitta konfiguratsiya uchun javobi.
 *
 * Ikki holat bor va ikkalasi ham normal:
 *  - Available: tarif mavjud, narx avtomatik hisoblandi
 *  - OnRequest: tarif yo'q yoki shart bajarilmadi, narx qo'lda beriladi
 *
 * Real hayotda buyurtmalarning katta qismi OnRequest bo'ladi,
 * shuning uchun bu chetlab o'tiladigan holat emas — asosiy oqim.
 */
sealed interface PriceQuote {

    data class Available(

        /** Bir dona uchun yakuniy narx (chegirma hisobga olingan). */
        val unitPrice: Long,

        /** Mahsulotlar summasi. */
        val itemsTotal: Long,

        /** Yetkazib berish. Bepul bo'lsa 0. */
        val deliveryPrice: Long,

        /** Umumiy to'lov. */
        val total: Long,

        /** Tayyorlash muddati, kunlarda. */
        val productionDays: Int,

        /** Tirajga berilgan chegirma foizi, 0 bo'lishi mumkin. */
        val discountPercent: Int = 0,

        val isRush: Boolean = false

    ) : PriceQuote

    data class OnRequest(

        val reason: Reason

    ) : PriceQuote {

        enum class Reason {

            /** Poligrafiyada bu mahsulot turi uchun tarif yo'q. */
            NO_TARIFF,

            /** Soni minimal chegaradan kam. */
            BELOW_MIN_QUANTITY,

            /** Shoshilinch buyurtma qabul qilinmaydi. */
            RUSH_NOT_AVAILABLE,

            /** Poligrafiya hozircha buyurtma qabul qilmayapti. */
            NOT_ACCEPTING
        }
    }
}
