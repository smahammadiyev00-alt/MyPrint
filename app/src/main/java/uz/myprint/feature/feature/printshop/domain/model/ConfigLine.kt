package uz.myprint.feature.feature.printshop.domain.model

import uz.myprint.feature.feature.product.domain.model.ProductSize

/**
 * Buyurtmaning bitta qatori: o'lcham va shu o'lchamdagi soni.
 *
 * Vizitka yoki banner uchun bitta qator bo'ladi.
 * Futbolka uchun bir nechta: 10 ta M, 15 ta L, 5 ta XL.
 *
 * O'lcham null bo'lishi mumkin — masalan bakalning o'lchami bitta.
 */
data class ConfigLine(

    val size: ProductSize?,

    val quantity: Int

) {

    /** Shu qatordagi bir donaga qo'shiladigan summa. */
    val sizePricePerUnit: Long
        get() = size?.additionalPrice ?: 0L
}
