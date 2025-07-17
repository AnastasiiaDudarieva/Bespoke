package com.bespoke.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bespoke.app.R

val BeatriceFontFamily = FontFamily(
    Font(R.font.beatrice_regular, FontWeight.Normal),
    Font(R.font.beatrice_semibold, FontWeight.W600),
    Font(R.font.beatrice_bold, FontWeight.Bold)
)

// Set of Material typography styles to start with
val Typography = Typography(


    titleSmall = TextStyle(
        fontFamily = BeatriceFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = BeatriceFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = BeatriceFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = BeatriceFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = BeatriceFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

//    for caps title
    labelLarge = TextStyle(
        fontFamily = BeatriceFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = BeatriceFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
)