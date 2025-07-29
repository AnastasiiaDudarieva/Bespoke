package com.bespoke.app.ui.screens.components.programs.workout

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.bespoke.app.data.model.ExerciseEntry
import com.bespoke.app.data.model.ExerciseState
import com.bespoke.app.data.model.timeForUse

@Composable
fun WorkoutTimer(
    counter: Float,
    exerciseState: ExerciseState,
    currentExercise: ExerciseEntry?,
    isPaused: Boolean,
    isPausedBtwnRep: Boolean
) {
    val reps = currentExercise?.reps ?: 0
    val timeForUse = currentExercise?.timeForUse() ?: 0
    val timePerRepFrames = if (reps > 0) (timeForUse * 30) / reps else 0

    val repNumber = if (timePerRepFrames > 0) {
        ((counter / timePerRepFrames).toInt() + 1).coerceAtMost(reps)
    } else 0

    val secondsLeft = ((timeForUse * 30 - counter) / 30).toInt().coerceAtLeast(0)
    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        when (exerciseState) {
            ExerciseState.active -> {
                if (timeForUse > 0 && reps > 0) {
                    Text("Time left: %02d:%02d".format(minutes, seconds), color = Color.White, fontSize = 28.sp)
                    Text("Rep: $repNumber / $reps", color = Color.White, fontSize = 28.sp)
                } else {
                    Text("Reps: $repNumber / $reps", color = Color.White, fontSize = 28.sp)
                }
            }
            ExerciseState.rest -> {
                Text("Resting...", color = Color.LightGray, fontSize = 24.sp)
            }
            else -> {
                Text("Get Ready", color = Color.White, fontSize = 24.sp)
            }
        }
        if (isPaused) {
            Text("Paused", color = Color.Yellow, fontSize = 16.sp)
        }
        if (isPausedBtwnRep) {
            Text("Pause between reps...", color = Color.Cyan, fontSize = 16.sp)
        }
    }
}

// Helper to format float seconds to mm:ss string
fun formatTime(time: Float): String {
    val minutes = time.toInt() / 60
    val seconds = time.toInt() % 60
    return String.format("%02d:%02d", minutes, seconds)
}
