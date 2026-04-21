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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*

import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.example.wheelsonwheels.ui.components.RoleToggle
import com.example.wheelsonwheels.ui.theme.AppColors

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onOrders: () -> Unit,
    onLogout: () -> Unit,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val user = authViewModel.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
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

        // Role switch (still works)
        RoleToggle(
            currentRole = user?.role,
            onRoleSelected = { role ->
                authViewModel.updateUserRole(role)
            }
        )

        Spacer(Modifier.height(24.dp))

        // Buttons
        Text("Stuff", color = AppColors.GrayMuted)

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onOrders,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Your Orders")
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

        // Logout
        OutlinedButton(
            onClick = {
                authViewModel.logout()
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Log Out")
        }
    }
}