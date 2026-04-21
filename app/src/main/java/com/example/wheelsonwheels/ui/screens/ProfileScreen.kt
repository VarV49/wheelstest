package com.example.wheelsonwheels.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*

import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.example.wheelsonwheels.ui.components.RoleToggle
import com.example.wheelsonwheels.ui.theme.AppColors
import com.example.wheelsonwheels.data.model.UserRole
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onOrders: () -> Unit,
    onMyListings: () -> Unit,
    onLogout: () -> Unit,
    onManageUsers: () -> Unit,
    onManageListings: () -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val user = authViewModel.currentUser
    val isAdminAccount = authViewModel.isAdminAccount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(24.dp))

        // Avatar
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(2.dp, AppColors.RedPrimary, RoundedCornerShape(40.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user?.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.RedPrimary
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = user?.name ?: "",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = user?.role?.name ?: "",
            fontSize = 13.sp,
            color = AppColors.GrayMuted
        )

        Spacer(Modifier.height(32.dp))

        // Role toggle — shows Admin button only for admin accounts
        RoleToggle(
            currentRole = user?.role,
            isAdmin = isAdminAccount,
            onRoleSelected = { role -> authViewModel.updateUserRole(role) }
        )

        Spacer(Modifier.height(24.dp))

        Text("Stuff", color = AppColors.GrayMuted)

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onOrders,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Your Orders")
        }

        // My Listings button — only visible when browsing as SELLER
        if (user?.role == UserRole.SELLER) {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onMyListings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.List, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("My Listings")
            }
        }

        Spacer(Modifier.height(32.dp))

        // Dark mode toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Dark Mode")
            Switch(
                checked = isDarkTheme,
                onCheckedChange = onThemeChange
            )
        }

        Spacer(Modifier.height(24.dp))

        HorizontalDivider()

        Spacer(Modifier.height(24.dp))

        // Admin Tools — visible whenever this is an admin account
        if (isAdminAccount) {
            Text(
                text = "Admin Tools",
                color = AppColors.GrayMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onManageUsers,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Manage Users")
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = onManageListings,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.List, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Manage Listings")
            }

            Spacer(Modifier.height(24.dp))
        }

        // Logout
        OutlinedButton(
            onClick = {
                authViewModel.logout()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Log Out")
        }
    }
}