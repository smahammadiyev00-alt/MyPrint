package uz.myprint.core.navigation

sealed class Screen(val route: String) {

    data object Splash : Screen("splash")

    data object Login : Screen("login")

    data object Otp : Screen("otp/{phone}") {
        fun createRoute(phone: String) = "otp/$phone"
    }
    data object Cart : Screen("cart")
    data object Home : Screen("home")


    data object Orders : Screen("orders")

    data object Favorites : Screen("favorites")

    data object Profile : Screen("profile")

    data object Design : Screen("design")

    data object Product : Screen("product/{category}") {

        fun createRoute(category: String): String {
            return "product/$category"
        }
    }

    data object ProductDetail : Screen("product_detail/{productId}") {

        fun createRoute(productId: String): String {
            return "product_detail/$productId"
        }
    }

    data object DesignStudio : Screen(
        "design_studio/{productId}/{sizeId}"
    ) {

        /** Bo'sh yo'l bo'lagi marshrutni buzadi, shuning uchun belgi. */
        const val NO_SIZE = "-"

        fun createRoute(
            productId: String,
            sizeId: String
        ): String {
            return "design_studio/$productId/${sizeId.ifBlank { NO_SIZE }}"
        }
    }

    data object SpecialOffers : Screen("special_offers")
    data object PrintShopSelection : Screen(
        "printshop/{productId}/{materialId}/{printTypeId}/{finishIds}/{lines}"
    ) {

        /** finishIds — "laminate,uv_print" yoki bo'sh bo'lsa "-". */
        fun createRoute(
            productId: String,
            materialId: String,
            printTypeId: String,
            finishIds: String,
            lines: String
        ): String {
            return "printshop/$productId/$materialId/$printTypeId/$finishIds/$lines"
        }
    }
}