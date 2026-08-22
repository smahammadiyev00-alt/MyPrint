package uz.myprint.feature.feature.cart.domain.model

import uz.myprint.feature.feature.printshop.domain.model.PriceQuote
import uz.myprint.feature.feature.printshop.domain.model.ProductConfig

/**
 * Savatdagi bitta pozitsiya.
 *
 * Poligrafiya shu yerda biriktiriladi — mijoz avval mahsulotni
 * sozlaydi, keyin poligrafiyani tanlaydi, keyin savatga qo'shadi.
 *
 * Narx nusxa sifatida saqlanadi: poligrafiya tarifni o'zgartirsa,
 * savatdagi narx o'z-o'zidan o'zgarib ketmasligi kerak. Buyurtma
 * yuborilganda narx qayta tekshiriladi.
 */
data class CartItem(

    val id: String,

    val productId: String,

    val productName: String,

    val config: ProductConfig,

    val shopId: String,

    val shopName: String,

    /** Savatga qo'shilgan paytdagi narx. */
    val quote: PriceQuote,

    /** Chop etiladigan fayl. Hali yuklanmagan bo'lsa null. */
    val designFileUrl: String? = null,

    val addedAtMillis: Long = System.currentTimeMillis()

) {

    val quantity: Int
        get() = config.quantity

    val total: Long
        get() = (quote as? PriceQuote.Available)?.total ?: 0L

    /**
     * Buyurtma yuborishga tayyormi.
     * Fayl va hisoblangan narx bo'lishi shart.
     */
    val isReadyToOrder: Boolean
        get() = quote is PriceQuote.Available && !designFileUrl.isNullOrBlank()
}