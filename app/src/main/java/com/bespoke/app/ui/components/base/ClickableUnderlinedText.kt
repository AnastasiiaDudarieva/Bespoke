package com.bespoke.app.ui.components.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.TextDark

@Composable
fun ClickableUnderlinedText(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        style = TextStyle(
            color = TextDark,
            fontFamily = BeatriceFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            textDecoration = TextDecoration.Underline
        ),
        textAlign = TextAlign.Center
    )
}