package uz.myprint.feature.feature.printshop.domain.model

import uz.myprint.feature.feature.product.domain.model.ProductCategory

data class PrintShop(

    // Basic Information
    val id: String,
    val name: String,
    val description: String = "",

    // Media
    val logo: String = "",
    val cover: String = "",
    val gallery: List<String> = emptyList(),

    // Location
    val district: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,

    // Reputation
    val rating: Float,
    val reviewCount: Int,
    val completedOrders: Int = 0,

    // Capabilities
    val services: List<PrintService> = emptyList(),
    val badges: List<ShopBadge> = emptyList(),

    // Operations
    val workingHours: WorkingHours,
    val hasDelivery: Boolean = false,
    val acceptsOnlinePayment: Boolean = false,
    val isAcceptingOrders: Boolean = true,

    // Pricing
    val tariffs: List<ShopTariff> = emptyList()

) {

    val isVerified: Boolean
        get() = ShopBadge.VERIFIED in badges

    fun tariffFor(category: ProductCategory): ShopTariff? =
        tariffs.firstOrNull { it.category == category }
}
