package uz.myprint.feature.feature.printshop.data.dummy

import uz.myprint.feature.feature.printshop.domain.model.PrintShop
import uz.myprint.feature.feature.printshop.domain.model.QuantityTier
import uz.myprint.feature.feature.printshop.domain.model.ShopTariff
import uz.myprint.feature.feature.printshop.domain.model.WorkingHours
import uz.myprint.feature.feature.product.domain.model.ProductCategory
import uz.myprint.feature.feature.printshop.domain.model.PrintService
import uz.myprint.feature.feature.printshop.domain.model.ShopBadge
import uz.myprint.feature.feature.printshop.domain.model.PricingUnit
import uz.myprint.feature.feature.printshop.domain.model.RollOption
/**
 * Vaqtinchalik ma'lumot. Backend ulanganda faqat data source almashadi,
 * UI va PriceCalculator o'zgarmaydi.
 *
 * Narxlar taxminiy: vizitka uchun dona narxi 900-1200 so'm oralig'ida,
 * 300 dona ~ 270 000 - 360 000 so'm chiqadi.
 */
object PrintShopDummyData {

    val shops: List<PrintShop> = listOf(

        PrintShop(
            id = "shop_print_pro",
            name = "Print Pro",
            description = "Premium sifatli offset va raqamli bosma.",
            district = "Yunusobod",
            address = "Amir Temur ko'chasi 108",
            latitude = 41.3450,
            longitude = 69.2870,
            rating = 4.9f,
            reviewCount = 1289,
            completedOrders = 5400,
            services = listOf(
                PrintService.DTF,
                PrintService.UV,
                PrintService.OFFSET,
                PrintService.DIGITAL
            ),
            badges = listOf(ShopBadge.VERIFIED, ShopBadge.PARTNER),
            workingHours = WorkingHours(
                opensAtMinutes = 9 * 60,
                closesAtMinutes = 20 * 60,
                worksOnSunday = true
            ),
            hasDelivery = true,
            acceptsOnlinePayment = true,
            tariffs = listOf(
                ShopTariff(
                    category = ProductCategory.BUSINESS_CARD,
                    basePricePerUnit = 1_200,
                    minQuantity = 100,
                    productionDays = 1,
                    quantityTiers = listOf(
                        QuantityTier(fromQuantity = 300, discountPercent = 10),
                        QuantityTier(fromQuantity = 500, discountPercent = 18),
                        QuantityTier(fromQuantity = 1000, discountPercent = 25)
                    ),
                    rushAvailable = true,
                    rushMultiplier = 1.4f,
                    rushProductionDays = 1,
                    deliveryPrice = 25_000,
                    freeDeliveryFrom = 300_000
                ),
                ShopTariff(
                    category = ProductCategory.BANNER,
                    basePricePerUnit = 65_000,
                    minQuantity = 1,
                    productionDays = 2,
                    rushAvailable = true,
                    deliveryPrice = 30_000
                )
            )
        ),

        PrintShop(
            id = "shop_smart_print",
            name = "Smart Print",
            description = "Tezkor bosma, futbolka va suvenir.",
            district = "Chilonzor",
            address = "Bunyodkor shoh ko'chasi 12",
            latitude = 41.2750,
            longitude = 69.2040,
            rating = 4.8f,
            reviewCount = 964,
            completedOrders = 3100,
            services = listOf(
                PrintService.DTF,
                PrintService.UV,
                PrintService.SUBLIMATION
            ),
            badges = listOf(ShopBadge.VERIFIED, ShopBadge.TOP),
            workingHours = WorkingHours(
                opensAtMinutes = 9 * 60,
                closesAtMinutes = 19 * 60
            ),
            hasDelivery = true,
            acceptsOnlinePayment = true,
            tariffs = listOf(
                ShopTariff(
                    category = ProductCategory.BUSINESS_CARD,
                    basePricePerUnit = 1_100,
                    minQuantity = 100,
                    productionDays = 1,
                    quantityTiers = listOf(
                        QuantityTier(fromQuantity = 300, discountPercent = 8),
                        QuantityTier(fromQuantity = 500, discountPercent = 15)
                    ),
                    rushAvailable = false,
                    deliveryPrice = 20_000,
                    freeDeliveryFrom = 250_000
                ),
                ShopTariff(
                    category = ProductCategory.BANNER,
                    pricingUnit = PricingUnit.PER_LINEAR_METER,
                    rolls = listOf(
                        RollOption(widthMeters = 1.0f, pricePerLinearMeter = 35_000),
                        RollOption(widthMeters = 1.2f, pricePerLinearMeter = 42_000),
                        RollOption(widthMeters = 1.5f, pricePerLinearMeter = 52_000),
                        RollOption(widthMeters = 1.7f, pricePerLinearMeter = 59_000),
                        RollOption(widthMeters = 3.0f, pricePerLinearMeter = 105_000)
                    ),
                    minQuantity = 1,
                    productionDays = 2,
                    rushAvailable = true,
                    deliveryPrice = 30_000
                )
            )
        ),

        PrintShop(
            id = "shop_mega_print",
            name = "Mega Print",
            description = "Katta tirajli offset bosma.",
            district = "Shayxontohur",
            address = "Navoiy ko'chasi 30",
            latitude = 41.3170,
            longitude = 69.2380,
            rating = 4.7f,
            reviewCount = 723,
            completedOrders = 2200,
            services = listOf(
                PrintService.OFFSET,
                PrintService.DIGITAL,
                PrintService.LAMINATION
            ),
            badges = listOf(ShopBadge.VERIFIED),
            workingHours = WorkingHours(
                opensAtMinutes = 10 * 60,
                closesAtMinutes = 18 * 60,
                worksOnSaturday = false
            ),
            hasDelivery = false,
            acceptsOnlinePayment = false,
            tariffs = listOf(
                ShopTariff(
                    category = ProductCategory.BUSINESS_CARD,
                    basePricePerUnit = 950,
                    minQuantity = 500,
                    productionDays = 3,
                    quantityTiers = listOf(
                        QuantityTier(fromQuantity = 1000, discountPercent = 20)
                    )
                )
            )
        ),

        // Tarifi yo'q poligrafiya: narx so'rov bo'yicha beriladi.
        PrintShop(
            id = "shop_color_max",
            name = "Color Max",
            description = "Rangli bosma va lazer kesish.",
            district = "Mirzo Ulug'bek",
            address = "Mustaqillik shoh ko'chasi 5",
            latitude = 41.3320,
            longitude = 69.3350,
            rating = 4.6f,
            reviewCount = 532,
            completedOrders = 1400,
            services = listOf(
                PrintService.UV,
                PrintService.LASER_CUT,
                PrintService.DIGITAL
            ),
            badges = emptyList(),
            workingHours = WorkingHours(isOpen24 = true),
            hasDelivery = true,
            acceptsOnlinePayment = false,
            tariffs = emptyList()
        )
    )
}