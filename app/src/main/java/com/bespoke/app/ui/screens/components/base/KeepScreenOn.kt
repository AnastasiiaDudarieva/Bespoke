package com.bespoke.app.ui.screens.components.base

import androidx.compose.ui.platform.LocalView
import androidx.compose.runtime.DisposableEffect
import android.view.WindowManager
import androidx.compose.runtime.Composable

@Composable
fun KeepScreenOn() {
    val view = LocalView.current
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose {
            view.keepScreenOn = false
        }
    }
}
