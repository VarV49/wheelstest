package com.example.wheelsonwheels.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wheelsonwheels.data.model.UserRole
import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.example.wheelsonwheels.ui.theme.AppColors

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onBrowse: () -> Unit,
    onCart: () -> Unit,
    onOrders: () -> Unit,
    onCreateListing: () -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    val user = authViewModel.currentUser

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {

            // HEADER
            Text(
                text = "WHEELS ON WHEELS",
                color = AppColors.RedPrimary,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Welcome, ${user?.name ?: "User"}!",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(24.dp))

            // QUICK ACTIONS
            Text("Quick Access", color = AppColors.GrayMuted)

            Spacer(Modifier.height(12.dp))

            val quickActionColors = ButtonColors(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.onPrimary,
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.onSurface)

            Button(
                onClick = onBrowse,
                modifier = Modifier.fillMaxWidth(),
                colors = quickActionColors,
            ) {
                Text("Browse Listings")
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onCart,
                modifier = Modifier.fillMaxWidth(),
                colors = quickActionColors
            ) {
                Text("Cart")
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onOrders,
                modifier = Modifier.fillMaxWidth(),
                colors = quickActionColors
            ) {
                Text("Orders")
            }

            // SELLER SECTION
            if (user?.role == UserRole.SELLER || user?.role == UserRole.ADMIN) {

                Spacer(Modifier.height(24.dp))

                Text("Seller Tools", color = AppColors.GrayMuted)

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = onCreateListing,
                    modifier = Modifier.fillMaxWidth(),
                    colors = quickActionColors
                ) {
                    Text("Create Listing")
                }
            }

            Spacer(Modifier.height(32.dp))

        }
    }
}