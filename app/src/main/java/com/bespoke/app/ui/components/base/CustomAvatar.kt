package com.bespoke.app.ui.components.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.BespokeBlue
import com.bespoke.app.ui.theme.TextDark

@Composable
fun CustomAvatar(
    url: String?=null,
    firstName: String?=null,
    lastName: String?=null,
    size: Dp,
    modifier: Modifier
) {
    val painter: AsyncImagePainter? = url?.let {
        rememberAsyncImagePainter(model = it)
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(size)
            .background(TextDark),
        contentAlignment = Alignment.Center
    ) {
        if (painter != null && painter.state is AsyncImagePainter.State.Success) {
            AsyncImage(
                model = url,
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
            )
        } else {
            val initials = buildString {
                append(firstName?.firstOrNull()?.uppercaseChar() ?:"" )
                append(lastName?.firstOrNull()?.uppercaseChar() ?: "")
            }
            val fontSize = when {
                size > 40.dp -> 14.sp
                size > 30.dp -> 12.sp
                else -> 10.sp
            }
            Text(
                text = initials,
                style = TextStyle(
                    color = Color.White,
                    fontSize = fontSize,
                    fontFamily = BeatriceFontFamily
                )
            )
        }
    }
}
