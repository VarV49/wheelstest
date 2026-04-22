package com.example.wheelsonwheels.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.example.wheelsonwheels.data.model.UserRole
import androidx.compose.runtime.LaunchedEffect

@Composable
fun AdminUsersScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val users = authViewModel.allUsers

    LaunchedEffect(Unit) {
        authViewModel.loadUsers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Manage Users",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        users.forEach { user ->
            // Don't show the admin themselves in the list
            if (user.role == UserRole.ADMIN) return@forEach

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {

                    Text(user.name, fontWeight = FontWeight.Bold)
                    Text(text = user.email, fontSize = 12.sp)
                    Text(text = user.role.name, fontSize = 12.sp)

                    if (user.isBanned) {
                        Text(
                            text = "BANNED",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    if (user.isBanned) {
                        // Show Unban button when user is already banned
                        Button(onClick = {
                            authViewModel.unbanUser(user)
                        }) {
                            Text("Unban")
                        }
                    } else {
                        // Show ban options when user is not banned
                        Row {
                            Button(onClick = {
                                authViewModel.banUser(user, 86400000L) // 1 day
                            }) {
                                Text("Ban 1 Day")
                            }

                            Spacer(Modifier.width(8.dp))

                            Button(onClick = {
                                authViewModel.banUser(user, 7 * 86400000L) // 7 days
                            }) {
                                Text("Ban 7 Days")
                            }

                            Spacer(Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    authViewModel.banUser(user, null) // permanent
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Perm Ban")
                            }
                        }
                    }
                }
            }
        }
    }
}