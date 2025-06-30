package com.bespoke.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bespoke.app.ui.components.base.BespokeTopBar

@Composable
fun ProfileScreen(
    onBack: () -> Unit
) {

    Scaffold(
        topBar = {
            BespokeTopBar(
                canNavigateBack = true,
                onBackClick = onBack,
                title = "Profile"
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Text(text = "Profile")
        }
    }
}
