package com.bespoke.app.ui.screens

import SetStatusBarIconsDark
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bespoke.app.R
import com.bespoke.app.ui.models.auth.AuthState
import com.bespoke.app.ui.models.auth.BottomPanelContent
import com.bespoke.app.ui.screens.components.auth.BottomPanel
import com.bespoke.app.ui.screens.components.auth.Greeting
import com.bespoke.app.ui.screens.components.auth.rememberImeVisibility
import com.bespoke.app.ui.screens.components.base.BespokeTopBar
import com.bespoke.app.ui.theme.BespokeBlue
import com.bespoke.app.ui.viewmodel.AuthViewModel

@Composable
fun WelcomeScreen() {
    SetStatusBarIconsDark(darkIcons = false)

    val authViewModel: AuthViewModel = hiltViewModel()

    var panelState by remember { mutableStateOf<BottomPanelContent>(BottomPanelContent.Welcome) }
    val authState by authViewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val view = LocalView.current
    val imeVisible = rememberImeVisibility(view)


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
                    canNavigateBack = panelState != BottomPanelContent.Welcome,
                    onBackClick = { panelState = BottomPanelContent.Welcome },
                    color = Color.White
                )
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

                Spacer(modifier = Modifier.weight(1f))

                Greeting(
                    state = panelState,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    imeVisible = imeVisible
                )
                Spacer(modifier = Modifier.height(32.dp))
                BottomPanel(
                    state = panelState,
                    onChangeState = { panelState = it },
                    imeVisible = imeVisible
                )
            }
        }

        if (authState is AuthState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .pointerInput(Unit) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }



        LaunchedEffect(authState) {
            when (authState) {
                is AuthState.Error,
                    -> {
                    snackbarHostState.showSnackbar((authState as AuthState.Error).message)
                }

                is AuthState.Message,
                    -> {
                    snackbarHostState.showSnackbar((authState as AuthState.Message).message)
                }

                else -> {}
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 32.dp)
                .fillMaxWidth()
        )
    }

}

