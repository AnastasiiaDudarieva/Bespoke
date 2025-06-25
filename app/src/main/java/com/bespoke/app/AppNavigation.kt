package com.bespoke.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bespoke.app.ui.screens.HomeScreen
import com.bespoke.app.ui.screens.WelcomeScreen
import com.bespoke.app.viewmodel.AuthState
import com.bespoke.app.viewmodel.AuthViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: AuthViewModel = viewModel()

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkIfLoggedIn()
    }

    when (authState) {
        is AuthState.Success -> {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomeScreen() }
            }
        }
        else -> {
            NavHost(navController = navController, startDestination = "welcome") {
                composable("welcome") {
                    WelcomeScreen(
                        onLoginSuccess = {
                            navController.navigate("home") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        },
                        viewModel = viewModel
                    )
                }
                composable("home") { HomeScreen() }
            }
        }
    }
}
