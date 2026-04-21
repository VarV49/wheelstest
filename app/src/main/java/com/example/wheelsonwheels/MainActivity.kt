package com.example.wheelsonwheels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.wheelsonwheels.ui.navigation.NavGraph
import com.example.wheelsonwheels.ui.theme.WheelsOnWheelsTheme
import com.example.wheelsonwheels.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkThemeState = remember { mutableStateOf(true) }
            
            WheelsOnWheelsTheme(darkTheme = isDarkThemeState.value) {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    isDarkTheme = isDarkThemeState.value,
                    onThemeChange = { newValue -> isDarkThemeState.value = newValue }
                )
            }
        }
    }
}
