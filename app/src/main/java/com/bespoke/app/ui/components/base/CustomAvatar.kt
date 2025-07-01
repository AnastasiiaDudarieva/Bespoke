package com.bespoke.app.ui.components.base

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.TextDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

internal val imageBitmapCache = mutableStateMapOf<String, Bitmap?>()

@Composable
fun CustomAvatar(
    modifier: Modifier = Modifier,
    url: String? = null,
    firstName: String? = null,
    lastName: String? = null,
    size: Dp,
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(url) {
        if (url != null) {
            val cachedBitmap = imageBitmapCache[url]
            if (cachedBitmap != null) {
                bitmap = cachedBitmap
                isLoading = false
            } else {
                isLoading = true
                val downloaded = downloadImageBitmap(url)
                imageBitmapCache[url] = downloaded
                bitmap = downloaded
                isLoading = false
            }
        }
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(size)
            .background(TextDark),
        contentAlignment = Alignment.Center
    ) {
        when {
            bitmap != null -> {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
                )
            }

            isLoading -> {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(size / 3)
                )
            }

            else -> {
                val initials = buildString {
                    append(firstName?.firstOrNull()?.uppercaseChar() ?: "")
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
}

suspend fun downloadImageBitmap(imageUrl: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connect()
            val input = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            Log.e("CustomAvatar", "Failed to load bitmap from $imageUrl", e)
            null
        }
    }
}
