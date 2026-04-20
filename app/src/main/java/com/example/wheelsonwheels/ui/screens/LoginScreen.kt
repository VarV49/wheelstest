package com.example.wheelsonwheels.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.*
import com.example.wheelsonwheels.R
import com.example.wheelsonwheels.ui.theme.AppColors
import com.example.wheelsonwheels.viewmodel.AuthState
import com.example.wheelsonwheels.viewmodel.AuthViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by authViewModel.authState.observeAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BlackDeep)
    ) {
        // Red glow top-left
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-80).dp, y = (-80).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AppColors.RedMuted.copy(alpha = 0.2f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Logo ─────────────────────────────────────────────────────────
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.wow_launcher),
                contentDescription = null,
                modifier = Modifier
                    .height(90.dp)
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(8.dp))

            // ── Tagline ───────────────────────────────────────────────────────
            Text(
                text = "COLLECTOR'S MARKETPLACE",
                fontSize = 10.sp,
                fontWeight = FontWeight.W700,
                letterSpacing = 2.5.sp,
                color = AppColors.GrayMuted
            )

            Spacer(Modifier.height(40.dp))

            // ── Card ──────────────────────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = AppColors.BlackCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.BlackBorder),
                tonalElevation = 0.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = "Sign In",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.OffWhite
                    )
                    Text(
                        text = "Welcome back",
                        fontSize = 13.sp,
                        color = AppColors.GrayMuted,
                        modifier = Modifier.padding(top = 2.dp, bottom = 20.dp)
                    )

                    // Email field
                    DarkTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = AppColors.GrayMuted, modifier = Modifier.size(18.dp)) },
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(Modifier.height(12.dp))

                    // Password field
                    DarkTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = AppColors.GrayMuted, modifier = Modifier.size(18.dp)) },
                        keyboardType = KeyboardType.Password,
                        isPassword = true
                    )

                    // Error message
                    if (authState is AuthState.Error) {
                        Text(
                            text = (authState as AuthState.Error).message,
                            color = AppColors.RedPrimary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Login button / loading
                    if (authState is AuthState.Loading) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AppColors.RedPrimary, modifier = Modifier.size(28.dp))
                        }
                    } else {
                        Button(
                            onClick = { authViewModel.login(email.trim(), password) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppColors.RedPrimary,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Log In", fontWeight = FontWeight.W600, letterSpacing = 0.5.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Register link ─────────────────────────────────────────────────
            TextButton(onClick = onGoToRegister) {
                Text(
                    text = "Don't have an account? ",
                    color = AppColors.GrayMuted,
                    fontSize = 13.sp
                )
                Text(
                    text = "Register",
                    color = AppColors.RedPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.W600
                )
            }
        }
    }
}

// ── Reusable dark text field ──────────────────────────────────────────────────
@Composable
fun DarkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        leadingIcon = leadingIcon,
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.RedPrimary,
            unfocusedBorderColor = AppColors.BlackBorder,
            focusedLabelColor = AppColors.RedPrimary,
            unfocusedLabelColor = AppColors.GrayMuted,
            cursorColor = AppColors.RedPrimary,
            focusedTextColor = AppColors.OffWhite,
            unfocusedTextColor = AppColors.OffWhite,
            focusedContainerColor = AppColors.BlackDeep,
            unfocusedContainerColor = AppColors.BlackDeep,
        )
    )
}