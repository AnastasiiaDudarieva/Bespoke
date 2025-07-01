package com.bespoke.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bespoke.app.navigation.AppNavigation
import com.bespoke.app.ui.theme.BespokeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BespokeTheme(darkTheme = false) {
                AppNavigation()
            }
        }
    }
}


