package com.example.wheelsonwheels.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.wheelsonwheels.ui.navigation.Routes

@Composable
fun NavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar {

        NavigationBarItem(
            selected = currentRoute == Routes.HOME,
            onClick = { onNavigate(Routes.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = currentRoute == Routes.BROWSE,
            onClick = { onNavigate(Routes.BROWSE) },
            icon = { Icon(Icons.Default.Search, contentDescription = "Browse") },
            label = { Text("Browse") }
        )

        NavigationBarItem(
            selected = currentRoute == Routes.CART,
            onClick = { onNavigate(Routes.CART) },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
            label = { Text("Cart") }
        )

        NavigationBarItem(
            selected = currentRoute == Routes.PROFILE,
            onClick = { onNavigate(Routes.PROFILE) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") }
        )
    }
}