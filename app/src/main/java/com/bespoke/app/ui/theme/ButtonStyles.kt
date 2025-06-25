package com.bespoke.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val BespokeButtonShape: Shape = RoundedCornerShape(0.dp)

val BespokeButtonColors: ButtonColors
    @Composable
    get() = buttonColors(
        containerColor = BespokeBlue,
        contentColor = Color.White
    )

val BespokeButtonCancelColors: ButtonColors
    @Composable
    get() = buttonColors(
        containerColor = InputBackgroundColor,
        contentColor = TextDark
    )

val BespokeButtonTextStyle = TextStyle(
    fontFamily = BeatriceFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp
)
