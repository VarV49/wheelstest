package com.example.wheelsonwheels.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier

import com.example.wheelsonwheels.ui.screens.*
import com.example.wheelsonwheels.ui.components.NavBar
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
    const val PROFILE = "profile"
    const val MANAGEUSERS = "manage_users"
    const val MANAGELISTINGS = "manage_listings"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val listingViewModel: ListingViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = setOf(
        Routes.HOME,
        Routes.BROWSE,
        Routes.CART,
        Routes.PROFILE
    )

    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar && currentRoute != null) {
                NavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            popUpTo(Routes.HOME)
                        }
                    }
                )
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(padding)
        ) {

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
                    onBrowse = { navController.navigate(Routes.BROWSE) },
                    onCart = { navController.navigate(Routes.CART) },
                    onOrders = { navController.navigate(Routes.ORDERS) },
                    onCreateListing = { navController.navigate(Routes.CREATE_LISTING) },
                    isDarkTheme = isDarkTheme,
                    onThemeChange = onThemeChange,
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
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

            composable(Routes.PROFILE) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onOrders = { navController.navigate(Routes.ORDERS) },
                    onManageUsers = { navController.navigate(Routes.MANAGEUSERS) },
                    onManageListings = { navController.navigate(Routes.MANAGELISTINGS) },
                    isDarkTheme = isDarkTheme,
                    onThemeChange = onThemeChange,
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.MANAGEUSERS) {
                AdminUsersScreen(
                    authViewModel = authViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.MANAGELISTINGS) {
                AdminListingsScreen(
                    authViewModel = authViewModel,
                    listingViewModel = listingViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}