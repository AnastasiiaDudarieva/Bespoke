package com.bespoke.app.ui.screens.components.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.bespoke.app.ui.theme.iconSize

@Composable
fun CircleIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit,
) {

    IconButton(
        onClick = onClick,
        modifier = modifier.size(iconSize)
    ) {
        Box(
            modifier = modifier
                .size(iconSize)
                .background(Color.White, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = contentDescription, tint = Color.Black)
        }
    }
}