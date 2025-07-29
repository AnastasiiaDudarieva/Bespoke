package com.bespoke.app.ui.screens.components.programs.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bespoke.app.ui.viewmodel.MediaManagerViewModel
import com.bespoke.app.ui.viewmodel.MediaType

@Composable
fun MediaManagerView(
    viewModel: MediaManagerViewModel,
    horizontalItemPadding: Dp = 24.dp,
) {
    val audio  by viewModel.voiceRecordingUrl.collectAsState()
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        LazyRow(
            contentPadding = PaddingValues(horizontal = horizontalItemPadding),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(viewModel.displayedMedia.value) { _, media ->
                when (media.type) {
                    MediaType.IMAGE -> {
                        ImageMediaItem(media)
                    }

                    else -> Unit
                }
            }
        }

        audio?.let {
            AudioPlayerView(it)
        }


    }
}
