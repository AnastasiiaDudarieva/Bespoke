package com.bespoke.app.ui.components.base

import android.app.Activity
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import com.bespoke.app.ui.theme.BespokeBlue

@Composable
fun WebViewScreen(
    navController: NavHostController,
    url: String,
) {
    var canGoBack by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            setBackgroundColor(BespokeBlue.toArgb())
        }
    }

    BackHandler(enabled = true) {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            navController.popBackStack()
        }
    }

    LaunchedEffect(url) {
        webView.loadUrl(url)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BespokeBlue)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { webView },
                modifier = Modifier.fillMaxSize(),
                update = {
                    it.webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                            canGoBack = webView.canGoBack()
                        }
                    }
                }
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BespokeBlue)
                        .pointerInput(Unit) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}

