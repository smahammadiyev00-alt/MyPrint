package uz.myprint.core.navigation

sealed class Screen(val route: String) {

    data object Splash : Screen("splash")

    data object Login : Screen("login")

    data object Otp : Screen("otp")

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
}