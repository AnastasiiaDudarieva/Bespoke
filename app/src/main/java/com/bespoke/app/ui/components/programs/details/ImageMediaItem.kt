package com.bespoke.app.ui.components.programs.details

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bespoke.app.ui.components.base.FirebaseStorageImageView
import com.bespoke.app.ui.viewmodel.DisplayedMedia

@Composable
fun ImageMediaItem(media: DisplayedMedia) {
    FirebaseStorageImageView(
            gsPath = media.url,
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )
}