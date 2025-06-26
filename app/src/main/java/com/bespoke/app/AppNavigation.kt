package com.bespoke.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bespoke.app.ui.models.AuthState
import com.bespoke.app.ui.screens.HomeScreen
import com.bespoke.app.ui.screens.WelcomeScreen
import com.bespoke.app.viewmodel.AuthViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = "welcome") {

        composable("welcome") {
            WelcomeScreen(
                viewModel = authViewModel
            )
        }

        composable("home") {
            HomeScreen(
                viewModel = authViewModel,
                onLogout = {
                    navController.navigate("welcome") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                navController.navigate("home") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
            else -> {}
        }
    }
}
