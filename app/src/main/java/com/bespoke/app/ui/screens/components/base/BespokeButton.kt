package com.bespoke.app.ui.screens.components.base

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bespoke.app.ui.theme.BespokeButtonColors
import com.bespoke.app.ui.theme.BespokeButtonShape
import com.bespoke.app.ui.theme.BespokeButtonTextStyle

@Composable
fun BespokeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    colors:ButtonColors = BespokeButtonColors
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = colors,
        shape = BespokeButtonShape
    ) {
        Text(text = text, style = BespokeButtonTextStyle)
    }
}
