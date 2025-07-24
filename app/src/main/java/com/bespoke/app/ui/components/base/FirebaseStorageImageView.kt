package com.bespoke.app.ui.components.base

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


@Composable
fun FirebaseStorageImageView(
    gsPath: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    enableGradientOverlay: Boolean = true,
    cornerRadius: Int = 16,
) {
    var downloadUrl by remember(gsPath) { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(gsPath) {
        val cached = FirebaseStorageUrlCache.get(gsPath)
        if (cached != null) {
            downloadUrl = cached
            isLoading = false
        } else {
            Firebase.storage.getReferenceFromUrl(gsPath)
                .downloadUrl
                .addOnSuccessListener {
                    FirebaseStorageUrlCache.set(gsPath, it)
                    downloadUrl = it
                    isLoading = false
                }
                .addOnFailureListener {
                    Log.e("FirebaseStorageImageView", "Download failed: ${it.message}")
                    isLoading = false
                }
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(Color.Gray),
    ) {
        when {

            downloadUrl != null -> {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(downloadUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = contentScale,
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            drawContent()
                            if (enableGradientOverlay) {
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.45f),
                                            Color.Transparent
                                        )
                                    )
                                )
                            }
                        }
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray)
                )
            }
        }
    }
}

object FirebaseStorageUrlCache {
    private val cache = mutableMapOf<String, Uri>()

    fun get(gsPath: String): Uri? = cache[gsPath]
    fun set(gsPath: String, uri: Uri) {
        cache[gsPath] = uri
    }
}
