package com.bespoke.app.ui.components.programs.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bespoke.app.ui.components.base.FirebaseStorageImageView
import com.bespoke.app.ui.viewmodel.DisplayedMedia

@Composable
fun ImageMediaItem(media: DisplayedMedia) {
    var isDialogOpen by remember { mutableStateOf(false) }

    Box {
        FirebaseStorageImageView(
            gsPath = media.url,
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickable { isDialogOpen = true },
            contentScale = ContentScale.Crop
        )

        if (isDialogOpen) {
            Dialog(
                onDismissRequest = { isDialogOpen = false },
                properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(Color.Transparent)
                        .clickable { isDialogOpen = false },
                    contentAlignment = Alignment.Center
                ) {
                    FirebaseStorageImageView(
                        gsPath = media.url,
                        modifier = Modifier
                            .wrapContentSize(),
                        contentScale = ContentScale.Fit,
                        cornerRadius = 0
                    )
                }
            }
        }
    }
}

