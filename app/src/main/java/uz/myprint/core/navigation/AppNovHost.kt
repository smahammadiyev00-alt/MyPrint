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
import uz.myprint.feature.feature.cart.presentation.route.CartRoute
import uz.myprint.feature.feature.design.studio.presentation.DesignStudioRoute
import uz.myprint.feature.feature.home.HomeScreen
import uz.myprint.feature.feature.printshop.presentation.route.PrintShopSelectionRoute
import uz.myprint.feature.feature.product.detail.ProductDetailRoute
import uz.myprint.feature.feature.product.presentation.route.ProductRoute
import uz.myprint.feature.feature.promotion.presentation.SpecialOffersScreen
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
        Screen.Cart.route,
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
                        Screen.Cart.route -> "cart"
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

                    onCartClick = {
                        navController.navigate(Screen.Cart.route) {
                            launchSingleTop = true
                        }
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

            composable(Screen.Login.route) {

                LoginScreen(
                    onContinueClick = { phone ->
                        navController.navigate(
                            Screen.Otp.createRoute(phone)
                        )
                    }
                )
            }

            composable(
                route = Screen.Otp.route,
                arguments = listOf(
                    navArgument("phone") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->

                val phone =
                    backStackEntry.arguments?.getString("phone").orEmpty()

                OtpScreen(
                    phone = phone,
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
                    },

                    // Saqlangan loyihani bosganda studio o'sha
                    // maket bilan ochiladi: Route omborni tekshirib,
                    // yangi maket o'rniga qoralamani yuklaydi.
                    onProjectClick = { project ->

                        navController.navigate(
                            Screen.DesignStudio.createRoute(
                                productId = project.productId,
                                sizeId = project.sizeId
                                    .ifBlank { Screen.DesignStudio.NO_SIZE }
                            )
                        )
                    },

                    // Bo'sh holatdagi "Birinchi maketingizni
                    // yarating" kartasi mahsulotlar ro'yxatiga
                    // olib boradi.
                    onCreateProjectClick = {
                        navController.navigate(
                            Screen.Product.createRoute("ALL")
                        )
                    },

                    onSpecialOffersClick = {
                        navController.navigate(Screen.SpecialOffers.route)
                    },

                    onOfferClick = {
                        // Keyingi bosqichda SpecialOfferDetail ga o'tamiz
                    }
                )
            }

            composable(Screen.Design.route) {

                DesignScreen()
            }

            composable(Screen.SpecialOffers.route) {

                SpecialOffersScreen(
                    onBackClick = {
                        navController.popBackStack()
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
                    backStackEntry.arguments?.getString("category") ?: "ALL"

                if (category == "ALL") {

                    // "Barchasi" — hamma mahsulotlar ro'yxati
                    ProductRoute(
                        category = category,
                        onProductClick = { productId ->
                            navController.navigate(
                                Screen.ProductDetail.createRoute(productId)
                            )
                        }
                    )

                } else {

                    // Aniq kategoriya — ro'yxat ekrani o'tkazib yuboriladi
                    ProductDetailRoute(
                        category = category,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onDesignStudioClick = { pid, sizeId ->
                            navController.navigate(
                                Screen.DesignStudio.createRoute(pid, sizeId)
                            )
                        },
                        onOrderClick = { pid, mid, ptid, finishes, lines ->
                            navController.navigate(
                                Screen.PrintShopSelection.createRoute(
                                    productId = pid,
                                    materialId = mid,
                                    printTypeId = ptid,
                                    finishIds = finishes,
                                    lines = lines
                                )
                            )
                        }
                    )
                }
            }

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
                    },
                    onDesignStudioClick = { pid, sizeId ->
                        navController.navigate(
                            Screen.DesignStudio.createRoute(pid, sizeId)
                        )
                    },
                    onOrderClick = { pid, mid, ptid, finishes, lines ->
                        navController.navigate(
                            Screen.PrintShopSelection.createRoute(
                                productId = pid,
                                materialId = mid,
                                printTypeId = ptid,
                                finishIds = finishes,
                                lines = lines
                            )
                        )
                    }
                )
            }

            composable(
                route = Screen.PrintShopSelection.route,
                arguments = listOf(
                    navArgument("productId") { type = NavType.StringType },
                    navArgument("materialId") { type = NavType.StringType },
                    navArgument("printTypeId") { type = NavType.StringType },
                    navArgument("finishIds") { type = NavType.StringType },
                    navArgument("lines") { type = NavType.StringType }
                )
            ) { backStackEntry ->

                val args = backStackEntry.arguments

                PrintShopSelectionRoute(
                    productId = args?.getString("productId").orEmpty(),
                    materialId = args?.getString("materialId").orEmpty(),
                    printTypeId = args?.getString("printTypeId").orEmpty(),
                    finishIds = args?.getString("finishIds").orEmpty(),
                    lines = args?.getString("lines").orEmpty(),
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onAddedToCart = {
                        navController.navigate(Screen.Cart.route)
                    }
                )
            }

            composable(
                route = Screen.DesignStudio.route,
                arguments = listOf(
                    navArgument("productId") { type = NavType.StringType },
                    navArgument("sizeId") { type = NavType.StringType }
                )
            ) { backStackEntry ->

                val args = backStackEntry.arguments

                DesignStudioRoute(

                    productId = args?.getString("productId").orEmpty(),

                    sizeId = args?.getString("sizeId").orEmpty(),

                    // Orqaga bosilganda ham maket saqlanadi (buni
                    // DesignStudioRoute o'zi bajaradi), shuning
                    // uchun bu yerda faqat ekranni yopish qoladi.
                    onBackClick = {
                        navController.popBackStack()
                    },

                    // ✓ tugmasi: maket saqlanadi va foydalanuvchi
                    // bosh sahifadagi "Loyihalaringiz" ro'yxatiga
                    // qaytadi.
                    //
                    // popBackStack ishlatiladi, navigate emas:
                    // shunda studio va mahsulot ekrani stekdan
                    // olib tashlanadi. Aks holda foydalanuvchi
                    // orqaga bosganda yana tahrirlash ekraniga
                    // tushib qolardi — bu chalkash bo'lardi,
                    // chunki u ishini tugatgan edi.
                    //
                    // Home stekda topilmasa (masalan chuqur
                    // havola orqali kirilgan bo'lsa) odatdagi
                    // navigate ishlaydi.
                    onDoneClick = {

                        val returned = navController.popBackStack(
                            route = Screen.Home.route,
                            inclusive = false
                        )

                        if (!returned) {

                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }

            composable(Screen.Cart.route) {

                CartRoute(
                    onCheckout = {
                        // Keyingi bosqich: buyurtma tasdiqlash
                    },
                    onBrowseProducts = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}