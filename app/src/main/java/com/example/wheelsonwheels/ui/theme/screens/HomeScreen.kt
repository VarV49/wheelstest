package com.example.wheelsonwheels.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wheelsonwheels.data.model.UserRole
import com.example.wheelsonwheels.viewmodel.AuthViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit,
    onBrowse: () -> Unit,
    onCart: () -> Unit,
    onOrders: () -> Unit,
    onCreateListing: () -> Unit
) {
    val user = authViewModel.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome, ${user?.name ?: "User"}!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = user?.email ?: "",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = "Role: ${user?.role?.name ?: ""}",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
        )

        HorizontalDivider()

        Spacer(modifier = Modifier.height(24.dp))

        // Buyer buttons
        if (user?.role == UserRole.BUYER || user?.role == UserRole.ADMIN) {
            Button(
                onClick = onBrowse,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) { Text("Browse Listings") }

            Button(
                onClick = onCart,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) { Text("My Cart") }

            Button(
                onClick = onOrders,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) { Text("My Orders") }
        }

        // Seller buttons
        if (user?.role == UserRole.SELLER || user?.role == UserRole.ADMIN) {
            Button(
                onClick = onCreateListing,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) { Text("Create Listing") }
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                authViewModel.logout()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Log Out") }
    }
}