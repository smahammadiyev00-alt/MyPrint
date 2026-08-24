package uz.myprint.feature.feature.product.domain.model

/**
 * Bosma varianti ikki xil bo'ladi va ular UI'da har xil ishlaydi.
 *
 * SIDE   — bir-birini istisno qiladi: vizitka yo 1 taraf, yo 2 taraf.
 * FINISH — ustiga qo'shiladi: laminatsiya + UV birga bo'lishi mumkin.
 */
enum class PrintOptionKind {
    SIDE,
    FINISH
}

data class ProductPrintType(

    val id: String,

    val name: String,

    val description: String = "",

    val additionalPrice: Long = 0L,

    val isDefault: Boolean = false,

    /** Standart qiymat SIDE — banner va futbolka variantlari o'zgarmaydi. */
    val kind: PrintOptionKind = PrintOptionKind.SIDE,

    /**
     * Qaysi materiallarda mavjudligi. Bo'sh to'plam — hamma materialda
     * ishlaydi. UV lak faqat Soft Touch qog'ozida beriladi.
     */
    val allowedMaterialIds: Set<String> = emptySet(),

    /** Mavjud bo'lmaganda chip ostida ko'rsatiladigan izoh. */
    val unavailableHint: String = ""
)

/** Bo'sh allowedMaterialIds — cheklov yo'q degani. */
fun ProductPrintType.isAvailableFor(material: ProductMaterial?): Boolean =
    allowedMaterialIds.isEmpty() || material?.id in allowedMaterialIds
