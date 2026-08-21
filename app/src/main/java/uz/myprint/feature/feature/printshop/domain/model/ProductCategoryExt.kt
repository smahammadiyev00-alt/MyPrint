package uz.myprint.feature.feature.product.domain.model

/**
 * Kiyim turidagi mahsulotlar o'lchamlar bo'yicha taqsimlanadi:
 * bitta buyurtmada 10 ta M, 15 ta L, 5 ta XL bo'lishi mumkin.
 *
 * Hozircha kategoriyaga bog'liq. Backend ulanganda bu bayroq
 * Product modelining o'ziga ko'chadi.
 */
val ProductCategory.usesSizeBreakdown: Boolean
    get() = when (this) {

        ProductCategory.T_SHIRT -> true

        else -> false
    }

/**
 * Buyurtmaga qarab kesiladigan mahsulotlar: o'lcham erkin kiritiladi
 * va narx kvadrat metr bo'yicha hisoblanadi.
 */
val ProductCategory.allowsCustomSize: Boolean
    get() = when (this) {

        ProductCategory.BANNER,
        ProductCategory.ROLL_UP,
        ProductCategory.STICKER-> true

        else -> false
    }