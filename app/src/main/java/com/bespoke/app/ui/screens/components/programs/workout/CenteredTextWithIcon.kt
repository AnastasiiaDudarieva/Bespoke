package com.bespoke.app.ui.screens.components.programs.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.TextDark

@Composable
fun CenteredTextWithIcon(
    text: String,
    iconSize: Dp = 32.dp,
    onNext: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconTotalWidth = iconSize + 8.dp // 8.dp — spacing

            Text(
                text = text,
                fontSize = 18.sp,
                maxLines = 2,
                color = Color.White,
                fontFamily = BeatriceFontFamily,
                modifier = Modifier
                    .weight(1f, fill = false) // so it doesn’t push the icon out
                    .padding(end = 8.dp)
                    .widthIn(max = LocalConfiguration.current.screenWidthDp.dp - iconTotalWidth - 32.dp) // padding
            )

            IconButton(
                onClick = onNext,
                modifier = Modifier.size(iconSize)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.White.copy(alpha = 0.5f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Exercise",
                        tint = TextDark.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

