package com.bespoke.app.ui.components.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.TextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BespokeTopBar(
    canNavigateBack: Boolean,
    onBackClick: () -> Unit,
    title: String? = null,
    color: Color = TextDark
) {
    TopAppBar(
        title = {
            if (title != null) {
                Text(
                    fontFamily = BeatriceFontFamily,
                    text = title,
                    color = color
                )
            }
        },
        navigationIcon = {
            AnimatedVisibility(
                visible = canNavigateBack,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = color
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}
