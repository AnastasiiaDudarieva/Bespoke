package com.bespoke.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bespoke.app.ui.models.auth.AuthState
import com.bespoke.app.ui.screens.HomeScreen
import com.bespoke.app.ui.screens.WelcomeScreen
import com.bespoke.app.data.viewmodel.AuthViewModel
import com.bespoke.app.ui.screens.MemberRootScreen
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.checkIfLoggedIn()
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                navController.navigate("root") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
            is AuthState.Idle,
            is AuthState.Error,
            is AuthState.Message -> {
                navController.navigate("welcome") {
                    popUpTo("root") { inclusive = true }
                }
            }
            else -> {}
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (authState is AuthState.Success) "root" else "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(viewModel = authViewModel)
        }

        composable("root") {
            MemberRootScreen(
                viewModel = authViewModel,
                onLogout = {
                    navController.navigate("welcome") {
                        popUpTo("root") { inclusive = true }
                    }
                }
            )
        }
    }
}

