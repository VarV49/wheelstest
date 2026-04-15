package com.example.wheelsonwheels.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wheelsonwheels.ui.screens.*
import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.example.wheelsonwheels.viewmodel.ListingViewModel

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val CREATE_LISTING = "create_listing"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val listingViewModel: ListingViewModel = viewModel()

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
                onBrowse = { /* teammate adds route here */ },
                onCart = { /* teammate adds route here */ },
                onOrders = { /* teammate adds route here */ },
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
    }
}