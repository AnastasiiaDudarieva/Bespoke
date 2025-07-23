package com.bespoke.app.ui.components.programs.details

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.VerticalDistribute
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bespoke.app.data.model.ExerciseEntry
import com.bespoke.app.data.model.MediaKind
import com.bespoke.app.ui.components.base.FirebaseStorageImageView
import com.bespoke.app.ui.viewmodel.ProgramOverviewViewModel

@Composable
fun ExerciseEntryRow(
    entry: ExerciseEntry,
    state: ExerciseState = ExerciseState.NotStarted,
    imageUrl: String? = null,
    viewModel: ProgramOverviewViewModel
) {
    var url by remember { mutableStateOf(imageUrl) }

    LaunchedEffect(entry) {
        if (url == null) {
            val square = entry.exerciseMedia?.firstOrNull()?.squarePath
            if (square != null) {
                url = viewModel.resolveFirebaseUrl(square)
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        url?.let {
            FirebaseStorageImageView(
                gsPath = it,
                modifier = Modifier
                    .size(112.dp)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop,
                cornerRadius = 4
            )
        }

        Spacer(modifier = Modifier.width(24.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = entry.name ?: "No name",
                fontSize = 16.sp,
                color = Color.Black
            )

            Text(
                text = viewModel.parameterListString(entry),
                fontSize = 14.sp,
                color = Color.Gray
            )

            when (state) {
                ExerciseState.Complete -> StatusLabel("Completed", Color(0xFF9E9E9E))
                ExerciseState.InProgress -> StatusLabel("In Progress", Color(0xFF6A1B9A))
                else -> {}
            }

            Log.e("entry.mediaList", "${entry.mediaList}")
            Row(verticalAlignment = Alignment.CenterVertically) {
                entry.mediaList?.forEach { item ->
                    val icon = when (item.kind?.lowercase()) {
                        "video" -> Icons.Default.PlayCircle
                        "audio" -> Icons.Default.Audiotrack
                        "image" -> Icons.Default.Image
                        else -> null
                    }

                    icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .size(16.dp)
                        )
                    }
                }

                if (!entry.comments.isNullOrEmpty()) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.FormatAlignLeft,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatusLabel(text: String, dotColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 4.dp)
            .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(dotColor, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 12.sp)
    }
}

enum class ExerciseState {
    NotStarted, InProgress, Complete, preActive
}

