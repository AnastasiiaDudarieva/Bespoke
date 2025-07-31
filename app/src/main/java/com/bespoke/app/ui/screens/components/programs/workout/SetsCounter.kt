package com.bespoke.app.ui.screens.components.programs.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.White

@Composable
fun SetsCounter(
    currentSet: Int,
    totalSets: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SET $currentSet/$totalSets",
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            color = White,
            fontFamily = BeatriceFontFamily
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (idx in 0 until totalSets) {
                Box(
                    modifier = Modifier
                        .size(width = 24.dp, height = 2.dp)
                        .background(
                            color = if (idx < currentSet) Color.White else Color(0xFFB0B0B0)
                        )
                )
            }
        }
    }
}
