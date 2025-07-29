package com.bespoke.app.ui.screens.components.programs.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayCircle
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
import com.bespoke.app.data.model.ExerciseState
import com.bespoke.app.ui.screens.components.base.FirebaseStorageImageView
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.TextDark
import com.bespoke.app.ui.viewmodel.ProgramOverviewViewModel

@Composable
fun ExerciseEntryRow(
    entry: ExerciseEntry,
    state: ExerciseState = ExerciseState.setsStart,
    imageUrl: String? = null,
    viewModel: ProgramOverviewViewModel,
    onExerciseClick: (ExerciseEntry) -> Unit
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
            .clickable {
                viewModel.selectExercise(entry)
                onExerciseClick(entry)
            }
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
                lineHeight = 24.sp,
                color = TextDark
            )

            Text(
                text = viewModel.parameterListString(entry),
                fontSize = 14.sp,
                lineHeight = 16.sp,
                color = TextDark.copy(alpha = 0.5f)
            )
//            when (state) {
//                ExerciseState.SetsFinished -> StatusLabel("Completed", Green)
//                ExerciseState.InProgress -> StatusLabel("In Progress", BespokeBlue)
//                else -> {}
//            }

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
            .background(Color.Gray.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp)
            .height(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(dotColor, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = TextDark,
            fontFamily = BeatriceFontFamily
        )

    }
}


