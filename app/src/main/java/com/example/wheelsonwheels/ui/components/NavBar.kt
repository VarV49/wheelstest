package com.example.wheelsonwheels.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.wheelsonwheels.ui.navigation.Routes
import com.example.wheelsonwheels.ui.theme.AppColors



@Composable
fun NavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val NavItemColors = NavigationBarItemColors(
        MaterialTheme.colorScheme.onSurface,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.onSurface,
        MaterialTheme.colorScheme.onSurface,
        MaterialTheme.colorScheme.onSurfaceVariant,
        MaterialTheme.colorScheme.onSurfaceVariant
    )

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {

        NavigationBarItem(
            selected = currentRoute == Routes.HOME,
            onClick = { onNavigate(Routes.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavItemColors
        )

        NavigationBarItem(
            selected = currentRoute == Routes.BROWSE,
            onClick = { onNavigate(Routes.BROWSE) },
            icon = { Icon(Icons.Default.Search, contentDescription = "Browse") },
            label = { Text("Browse") },
            colors = NavItemColors
        )

        NavigationBarItem(
            selected = currentRoute == Routes.CART,
            onClick = { onNavigate(Routes.CART) },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
            label = { Text("Cart") },
            colors = NavItemColors
        )

        NavigationBarItem(
            selected = currentRoute == Routes.PROFILE,
            onClick = { onNavigate(Routes.PROFILE) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavItemColors
        )
    }
}