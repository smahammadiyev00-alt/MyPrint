package uz.myprint.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import uz.myprint.feature.design.presentation.DesignScreen
import uz.myprint.feature.feature.SplashScreen.SplashScreen
import uz.myprint.feature.feature.home.HomeScreen
import uz.myprint.feature.feature.product.detail.ProductDetailRoute
import uz.myprint.feature.feature.product.presentation.route.ProductRoute
import uz.myprint.feature.login.LoginScreen
import uz.myprint.feature.otp.OtpScreen

@Composable
fun AppNavHost() {

    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = setOf(
        Screen.Home.route,
        Screen.Orders.route,
        Screen.Favorites.route,
        Screen.Profile.route
    )

    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {

                MyPrintBottomBar(
                    selectedItem = when (currentRoute) {
                        Screen.Home.route -> "home"
                        Screen.Orders.route -> "orders"
                        Screen.Favorites.route -> "favorites"
                        Screen.Profile.route -> "profile"
                        else -> "home"
                    },

                    onHomeClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    },

                    onOrdersClick = {
                        // Keyingi bosqichda OrdersScreen ulaymiz
                    },

                    onCreateClick = {
                        navController.navigate(Screen.Design.route)
                    },

                    onFavoritesClick = {
                        // Keyingi bosqichda FavoritesScreen ulaymiz
                    },

                    onProfileClick = {
                        // Keyingi bosqichda ProfileScreen ulaymiz
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(
                    navArgument("productId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->

                val productId =
                    backStackEntry.arguments?.getString("productId") ?: ""

                ProductDetailRoute(
                    productId = productId,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Splash.route) {

                SplashScreen(
                    onSplashFinished = {

                        navController.navigate(Screen.Login.route) {

                            popUpTo(Screen.Splash.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(Screen.Design.route) {

                DesignScreen()
            }

            composable(Screen.Login.route) {

                LoginScreen(
                    onContinueClick = {
                        navController.navigate(Screen.Otp.route)
                    }
                )
            }

            composable(Screen.Otp.route) {

                OtpScreen(
                    onVerifySuccess = {

                        navController.navigate(Screen.Home.route) {

                            popUpTo(Screen.Login.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {

                HomeScreen(

                    onCategoryClick = { category ->

                        navController.navigate(
                            Screen.Product.createRoute(
                                category.category.name
                            )
                        )
                    },

                    onProductClick = {
                        // Hozircha bo'sh
                    }
                )
            }

            composable(
                route = Screen.Product.route,
                arguments = listOf(
                    navArgument("category") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->

                val category =
                    backStackEntry.arguments?.getString("category")
                        ?: "ALL"

                ProductRoute(

                    category = category,

                    onProductClick = { productId ->

                        navController.navigate(
                            Screen.ProductDetail.createRoute(productId)
                        )
                    }
                )
            }
        }
    }
}