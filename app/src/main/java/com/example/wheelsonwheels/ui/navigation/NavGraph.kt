package com.example.wheelsonwheels.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wheelsonwheels.ui.screens.*
import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.example.wheelsonwheels.viewmodel.ListingViewModel
import com.example.wheelsonwheels.viewmodel.CartViewModel

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CREATE_LISTING = "create_listing"
    const val ORDERS = "orders"
    const val CART = "cart"
    const val BROWSE = "browse"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val listingViewModel: ListingViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoToLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                authViewModel = authViewModel,
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onBrowse = { navController.navigate(Routes.BROWSE) },
                onCart = { navController.navigate(Routes.CART) },
                onOrders = { navController.navigate(Routes.ORDERS) },
                onCreateListing = { navController.navigate(Routes.CREATE_LISTING) }
            )
        }

        composable(Routes.CREATE_LISTING) {
            CreateListingScreen(
                authViewModel = authViewModel,
                listingViewModel = listingViewModel,
                onListingCreated = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.BROWSE) {
            BrowseScreen(
                authViewModel = authViewModel,
                cartViewModel = cartViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CART) {
            CartScreen(
                authViewModel = authViewModel,
                cartViewModel = cartViewModel,
                onBack = { navController.popBackStack() },
                onCheckoutSuccess = {
                    navController.navigate(Routes.ORDERS) {
                        popUpTo(Routes.CART) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ORDERS) {
            OrdersScreen(
                authViewModel = authViewModel,
                cartViewModel = cartViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
