package uz.myprint.feature.feature.product.domain.model

data class ProductFilter(

    val category: ProductCategory? = null,

    val material: ProductMaterial? = null,

    val printType: ProductPrintType? = null,

    val colorMode: ProductColorMode? = null,

    val size: ProductSize? = null,

    val minPrice: Double? = null,

    val maxPrice: Double? = null,

    val designerService: Boolean? = null,

    val aiSupported: Boolean? = null,

    val studioSupported: Boolean? = null,

    val maxDistance: Double? = null,

    val minRating: Float? = null,

    val deliveryAvailable: Boolean? = null,

    val maxProductionDays: Int? = null,

    val sortType: ProductSortType = ProductSortType.POPULAR

)