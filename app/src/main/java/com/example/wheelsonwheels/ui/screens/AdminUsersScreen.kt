package com.example.wheelsonwheels.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

import com.example.wheelsonwheels.viewmodel.AuthViewModel
import com.example.wheelsonwheels.data.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {

    var users by remember { mutableStateOf(authViewModel.allUsers) }

    LaunchedEffect(Unit) {
        authViewModel.loadUsers()
        users = authViewModel.allUsers
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Users") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->

        val filteredUsers = users.filter { it.role != UserRole.ADMIN }

        if (filteredUsers.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No users found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(filteredUsers) { user ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {

                            Text(user.name, fontWeight = FontWeight.Bold)
                            Text(user.email, fontSize = 12.sp)
                            Text(user.role.name, fontSize = 12.sp)

                            if (user.isBanned) {
                                Text(
                                    text = "BANNED",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            Row {

                                if (user.isBanned) {
                                    Button(onClick = {
                                        authViewModel.unbanUser(user)

                                        authViewModel.loadUsers()
                                        users = authViewModel.allUsers
                                    }) {
                                        Text("Unban")
                                    }
                                } else {
                                    Button(onClick = {
                                        authViewModel.banUser(user, null)

                                        authViewModel.loadUsers()
                                        users = authViewModel.allUsers
                                    }) {
                                        Text("Ban")
                                    }
                                }

                                Spacer(Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        authViewModel.deleteUser(user)

                                        authViewModel.loadUsers()
                                        users = authViewModel.allUsers
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}