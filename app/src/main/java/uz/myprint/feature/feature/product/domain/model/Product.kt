package uz.myprint.feature.feature.product.domain.model

data class Product(

    // Basic Information
    val id: String,
    val name: String,
    val description: String,
    val category: ProductCategory,

    // Media
    val thumbnail: String,
    val gallery: List<String> = emptyList(),

    // Print Options
    val materials: List<ProductMaterial> = emptyList(),
    val printTypes: List<ProductPrintType> = emptyList(),
    val colorModes: List<ProductColorMode> = emptyList(),
    val sizes: List<ProductSize> = emptyList(),

    // Studio
    val studioSupported: Boolean = true,
    val aiSupported: Boolean = true,

    // Marketplace
    val isAvailable: Boolean = true,
    val isPopular: Boolean = false,
    val isFeatured: Boolean = false

)