package com.bespoke.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bespoke.app.data.viewmodel.AuthViewModel
import com.bespoke.app.ui.components.auth.BottomPanel
import com.bespoke.app.ui.components.auth.Greeting
import com.bespoke.app.ui.components.base.BespokeTopBar
import com.bespoke.app.ui.models.auth.AuthState
import com.bespoke.app.ui.models.auth.BottomPanelContent
import com.bespoke.app.ui.screens.MemberRootScreen
import com.bespoke.app.ui.screens.ProfileScreen
import com.bespoke.app.ui.screens.WelcomeScreen
import com.bespoke.app.ui.theme.BespokeBlue


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    when(authState) {
        is AuthState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .background(BespokeBlue)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.line_variants),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        BespokeTopBar(
                            canNavigateBack = false, {})
                    },
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                                bottom = paddingValues.calculateBottomPadding()
                            )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bespoke_wordmark),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(
                                    start = 24.dp,
                                    top = 128.dp
                                )
                        )
                    }
                }
            }
        }
        is AuthState.Success -> {
            androidx.navigation.compose.NavHost(
                navController = navController,
                startDestination = "memberRoot"
            ) {
                composable("memberRoot") {
                    MemberRootScreen(
                        navController = navController
                    )
                }
                composable("profile") {
                    ProfileScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
        else -> {
            WelcomeScreen()
        }
    }
}

