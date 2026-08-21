package uz.myprint.feature.feature.product.domain.model

/**
 * Kiyim turidagi mahsulotlar o'lchamlar bo'yicha taqsimlanadi:
 * bitta buyurtmada 10 ta M, 15 ta L, 5 ta XL bo'lishi mumkin.
 *
 * Vizitka yoki banner uchun bitta o'lcham tanlanadi va soni bitta.
 *
 * Hozircha kategoriyaga bog'liq. Backend ulanganda bu bayroq
 * Product modelining o'ziga ko'chadi, chunki bir kategoriya ichida
 * ham istisnolar bo'lishi mumkin.
 */
val ProductCategory.usesSizeBreakdown: Boolean
    get() = when (this) {

        ProductCategory.T_SHIRT -> true

        else -> false
    }
