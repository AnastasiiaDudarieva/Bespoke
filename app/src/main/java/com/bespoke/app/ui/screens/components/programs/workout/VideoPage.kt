package com.bespoke.app.ui.screens.components.programs.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.bespoke.app.data.model.ExerciseState
import com.bespoke.app.ui.screens.components.base.ExoVideoPlayer
import com.bespoke.app.ui.viewmodel.WorkoutDetailViewModel

@Composable
fun VideoPage(viewModel: WorkoutDetailViewModel){
    val exerciseState by viewModel.exerciseState.collectAsState()
    val videoUrl by viewModel.videoUrl.collectAsState()
    val thumbnailUrl by viewModel.thumbnailUrl.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    
    videoUrl?.let { url ->
        Box(modifier = Modifier.fillMaxSize()) {
            ExoVideoPlayer(
                videoUrl = url,
                thumbnailUrl = thumbnailUrl,
            )
            if ((isPaused && exerciseState != ExerciseState.setsStart
                        && viewModel.elapsedSeconds.value == 0) || exerciseState == ExerciseState.rest
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.6f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = 600f
                        )
                    )
            )
            VideoProgress(
                modifier = Modifier
                    .align(Alignment.BottomCenter), viewModel
            )
        }
    }
}