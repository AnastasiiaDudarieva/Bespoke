package com.bespoke.app.ui.screens.components.programs.workout

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bespoke.app.R
import com.bespoke.app.data.model.ExerciseState
import com.bespoke.app.data.model.timePerRep
import com.bespoke.app.ui.theme.BespokeBlue
import com.bespoke.app.ui.theme.BorderGrayColor
import com.bespoke.app.ui.theme.Green
import com.bespoke.app.ui.viewmodel.WorkoutDetailViewModel
import com.bespoke.app.utils.AudioPlayer
import kotlin.math.roundToInt

@Composable
fun VideoProgress(modifier: Modifier = Modifier, viewModel: WorkoutDetailViewModel) {

    val exerciseState by viewModel.exerciseState.collectAsState()
    val currentExercise by viewModel.currentExerciseEntry.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()

    val repCount = viewModel.repCount.collectAsState().value
    val elapsed = viewModel.elapsedSeconds.collectAsState().value
    val timeCurrentRep = viewModel.timeInCurrentRepMillis.collectAsState().value

    Box(modifier = modifier.fillMaxWidth()) {
        currentExercise?.let { exercise ->
            when (exerciseState) {
                ExerciseState.active -> {
                    when (exercise.basedType) {
                        "Time", "Time & Reps" -> {
                            ProgressLine(
                                durationMillis = exercise.time * 1000,
                                progressTrigger = exercise.time,
                                isPaused = isPaused,
                                currentProgressMillis = elapsed * 1000,
                                progressColor = BespokeBlue
                            )
                        }

                        "Reps" -> {
                            PulsingLines(
                                pausePerRepMillis = exercise.timePerRep()*1000,
                                repPauseTrigger = repCount,
                                isPaused = isPaused,
                                currentProgressMillis = timeCurrentRep.roundToInt()
                            )
                        }
                    }
                }

                ExerciseState.rest -> {
                    ProgressLine(
                        durationMillis = exercise.rest * 1000,
                        progressTrigger = exercise.rest,
                        isPaused = isPaused,
                        currentProgressMillis = elapsed * 1000
                    )
                }

                else -> Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(BorderGrayColor)
                )

            }
        }

    }

}

@Composable
fun PulsingLines(
    pausePerRepMillis: Int,
    repPauseTrigger: Int,
    isPaused: Boolean,
    currentProgressMillis: Int,
) {
    val progress = remember { Animatable(0f) }
    val lastProgress = remember { mutableFloatStateOf(0f) }
    var direction by remember { mutableIntStateOf(1) }
    var wasPausedMidway by remember { mutableStateOf(false) }
    var needsSecondPhase by remember { mutableStateOf(false) }
    var isStarted by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val audioPlayer = remember { AudioPlayer(context) }

Log.e("pausePerRepMillis", "${pausePerRepMillis}")
    val half = pausePerRepMillis / 2

    val initialProgress = remember(pausePerRepMillis, currentProgressMillis) {
        (currentProgressMillis / pausePerRepMillis.toFloat()).coerceIn(0f, 1f)
    }

    LaunchedEffect(Unit) {
        if (!isStarted && initialProgress > 0f) {
            direction = if (initialProgress <= 0.5f) 1 else -1
            wasPausedMidway = if (initialProgress <= 0.5f) false else true
            if (direction == 1)
                needsSecondPhase = true
            else
                needsSecondPhase = false
            val startProgress = if (direction == 1) initialProgress else 1f - initialProgress
            val remainingMillis = (startProgress * half).toInt()

            progress.snapTo(initialProgress)
            lastProgress.floatValue = initialProgress
            isStarted = true

            progress.animateTo(
                targetValue = if (direction == 1) 1f else 0f,
                animationSpec = tween(durationMillis = remainingMillis, easing = LinearEasing)
            )
        }
    }

    LaunchedEffect(repPauseTrigger) {
        if (repPauseTrigger > 0 && !isPaused) {
            audioPlayer.play(R.raw.bip)
            progress.snapTo(0f)
            direction = 1
            lastProgress.floatValue = 0f
            wasPausedMidway = false
            needsSecondPhase = true

            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = half, easing = LinearEasing)
            )

            direction = -1
            needsSecondPhase = false

            progress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = half, easing = LinearEasing)
            )
        }
    }

    LaunchedEffect(isPaused) {
        if (isPaused && progress.isRunning) {
            progress.stop()
            lastProgress.floatValue = progress.value
            wasPausedMidway = true
        } else {
            if (
                repPauseTrigger > 0 &&
                wasPausedMidway &&
                lastProgress.floatValue in 0f..1f
            ) {
                val remaining =
                    ((if (direction == 1) 1f - lastProgress.floatValue else lastProgress.floatValue) * half).toInt()

                progress.snapTo(lastProgress.floatValue)

                progress.animateTo(
                    targetValue = if (direction == 1) 1f else 0f,
                    animationSpec = tween(durationMillis = remaining, easing = LinearEasing)
                )

                if (needsSecondPhase) {
                    direction = -1
                    needsSecondPhase = false
                    progress.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = half, easing = LinearEasing)
                    )
                }

                wasPausedMidway = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(BorderGrayColor)
            .drawWithContent {
                val centerX = size.width / 2
                val centerY = size.height / 2
                val lineHeight = size.height

                val offsetX = centerX * progress.value

                if (progress.value > 0f) {
                    drawLine(
                        color = BespokeBlue,
                        start = Offset(centerX, centerY),
                        end = Offset(centerX - offsetX, centerY),
                        strokeWidth = lineHeight,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = BespokeBlue,
                        start = Offset(centerX, centerY),
                        end = Offset(centerX + offsetX, centerY),
                        strokeWidth = lineHeight,
                        cap = StrokeCap.Round
                    )
                }
            }
    )
}


@Composable
fun ProgressLine(
    durationMillis: Int,
    progressTrigger: Int,
    isPaused: Boolean,
    currentProgressMillis: Int,
    progressColor: Color = Green,
) {
    val progress = remember { Animatable(0f) }
    val lastProgress = remember { mutableFloatStateOf(0f) }

    val initialProgress = remember(durationMillis, currentProgressMillis) {
        if (durationMillis > 0) currentProgressMillis / durationMillis.toFloat() else 0f
    }
    LaunchedEffect(Unit) {
        if (initialProgress < 1f) {
            progress.snapTo(initialProgress)
            lastProgress.floatValue = progress.value
            progress.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = (durationMillis * initialProgress).toInt(),
                    easing = LinearEasing
                )
            )
        }
    }

    // Ініціалізація (з відновленням прогресу)
    LaunchedEffect(initialProgress, durationMillis) {
        progress.snapTo(initialProgress)
        lastProgress.floatValue = initialProgress
    }

    // Тригер для запуску з 0
    LaunchedEffect(progressTrigger) {
        if (progressTrigger > 0) {
            progress.snapTo(0f)
            lastProgress.floatValue = 0f
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)
            )
        }
    }

    // Pause / Resume
    LaunchedEffect(isPaused) {
        if (isPaused && progress.isRunning) {
            progress.stop()
            lastProgress.floatValue = progress.value
        } else if (!isPaused && lastProgress.floatValue in 0f..1f) {
            val remaining = ((1f - lastProgress.floatValue) * durationMillis).toInt()
            progress.snapTo(lastProgress.floatValue)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = remaining, easing = LinearEasing)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(Color.LightGray)
            .drawWithContent {
                val width = size.width
                val centerY = size.height / 2
                val progressWidth = width * progress.value

                drawLine(
                    color = progressColor,
                    start = Offset(0f, centerY),
                    end = Offset(progressWidth, centerY),
                    strokeWidth = size.height,
                    cap = StrokeCap.Round
                )
            }
    )
}
