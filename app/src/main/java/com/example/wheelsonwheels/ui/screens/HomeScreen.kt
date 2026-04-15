package com.example.wheelsonwheels.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wheelsonwheels.data.model.UserRole
import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.example.wheelsonwheels.ui.components.DashboardCard
import androidx.compose.animation.*
import androidx.compose.runtime.*

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    onBrowse: () -> Unit,
    onCart: () -> Unit,
    onOrders: () -> Unit,
    onCreateListing: () -> Unit
) {

    var showBanner by remember { mutableStateOf(false) }
    val user = authViewModel.currentUser

    LaunchedEffect(user?.role) {
        showBanner = true
        kotlinx.coroutines.delay(1200)
        showBanner = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Welcome back 👋",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = user?.name ?: "",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Role: ${user?.role?.name ?: ""}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(20.dp))

        // DASHBOARD CARDS
        DashboardCard("Browse Listings", "onBrowse", onBrowse)
        DashboardCard("My Cart", "onCart", onCart)
        DashboardCard("My Orders", "onOrders", onOrders)

        Spacer(Modifier.height(16.dp))

        // SELLER ONLY
        if (user?.role == UserRole.SELLER || user?.role == UserRole.ADMIN) {
            DashboardCard("Create Listing", "onCreateListing", onCreateListing)
        }

        Spacer(Modifier.weight(1f))

        OutlinedButton(
            onClick = {
                authViewModel.logout()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Out")
        }

        Button(
            onClick = { authViewModel.switchRole() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Switch Role")
        }
    }
}