package com.bespoke.app.ui.screens.components.programs.workout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bespoke.app.data.model.ExerciseState
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.BespokeBlue
import com.bespoke.app.ui.theme.BespokeDarkBlue
import com.bespoke.app.ui.theme.DarkGreen
import com.bespoke.app.ui.theme.Green
import com.bespoke.app.ui.viewmodel.WorkoutDetailViewModel
import kotlin.math.roundToInt


@Composable
fun TimerPage(modifier: Modifier = Modifier, viewModel: WorkoutDetailViewModel) {
    val exerciseState by viewModel.exerciseState.collectAsState()
    val currentExercise by viewModel.currentExerciseEntry.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()

    val repCount = viewModel.repCount.collectAsState().value
    val elapsed = viewModel.elapsedSeconds.collectAsState().value
    val timeCurrentRep = viewModel.timeInCurrentRep.collectAsState().value

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        currentExercise?.let { exercise ->
            when (exerciseState) {
                ExerciseState.active -> {
                    var state = ""
                    var name = ""
                    when (exercise.basedType) {
                        "Time", "Time & Reps" -> {
                            VerticalProgressLine(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                                durationMillis = exercise.time * 1000,
                                progressTrigger = exercise.time,
                                isPaused = isPaused,
                                currentProgressMillis = elapsed,
                                backgroundColor = BespokeDarkBlue,
                                progressColor = BespokeBlue
                            )
                            val remainingTime = exercise.time - elapsed
                            val clampedTime = if (remainingTime < 0) 0 else remainingTime
                            val minutes = clampedTime / 60
                            val seconds = clampedTime % 60
                            state = String.format("%01d:%02d", minutes, seconds)
                            name = "Time"

                        }

                        "Reps" -> {
                            PulsingCircle(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(),
                                pausePerRepMillis = viewModel.timePerRepMillis(
                                    exercise
                                )
                                    .toInt(),
                                repPauseTrigger = repCount,
                                isPaused = isPaused,
                                currentProgressMillis = (timeCurrentRep * 1000).roundToInt()
                            )
                            state = String.format("%02d", repCount)
                            name = "Rep"
                        }
                    }
                    if (!isPaused) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = name,
                                    fontSize = 24.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontFamily = BeatriceFontFamily,
                                )
                                Spacer(Modifier.height(48.dp))
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = state,
                                    fontSize = 136.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontFamily = BeatriceFontFamily,
                                    fontWeight = FontWeight.Thin
                                )
                            }
                        }
                    }
                }

                ExerciseState.preRest -> {
                    PreRestCircle(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    )
                }

                ExerciseState.rest -> {
                    VerticalProgressLine(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        durationMillis = exercise.rest * 1000,
                        progressTrigger = exercise.rest,
                        isPaused = isPaused,
                        currentProgressMillis = elapsed * 1000
                    )
                    if (!isPaused) {
                        val remainingTime = exercise.rest - elapsed
                        val clampedTime = if (remainingTime < 0) 0 else remainingTime
                        val minutes = clampedTime / 60
                        val seconds = clampedTime % 60
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Recover",
                                    fontSize = 24.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontFamily = BeatriceFontFamily,
                                )
                                Spacer(Modifier.height(48.dp))
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = String.format("%01d:%02d", minutes, seconds),
                                    fontSize = 136.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontFamily = BeatriceFontFamily,
                                    fontWeight = FontWeight.Thin
                                )
                            }
                        }
                    }

                }

                else -> Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(BespokeDarkBlue)
                )

            }
        }


    }

}

@Composable
fun VerticalProgressLine(
    modifier: Modifier = Modifier,
    durationMillis: Int,
    progressTrigger: Int,
    isPaused: Boolean,
    currentProgressMillis: Int,
    backgroundColor: Color = DarkGreen,
    progressColor: Color = Green,
) {
    val progress = remember { Animatable(1f) }
    var stage by remember { mutableIntStateOf(0) }
    val lastProgress = remember { mutableFloatStateOf(1f) }
    var isStarted by remember { mutableStateOf(false) }
    var lastTrigger by remember { mutableIntStateOf(0) }

    val initialProgress = remember(durationMillis, currentProgressMillis) {
        if (durationMillis > 0) 1f - (currentProgressMillis / durationMillis.toFloat()) else 1f
    }

    LaunchedEffect(Unit) {
        if (!isStarted && initialProgress < 1f) {
            progress.snapTo(initialProgress)
            lastProgress.floatValue = progress.value
            isStarted = true
            stage = 1
            progress.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = (durationMillis * initialProgress).toInt(),
                    easing = LinearEasing
                )
            )
        }
    }

    LaunchedEffect(progressTrigger) {
        if (progressTrigger > lastTrigger) {
            lastTrigger = progressTrigger
            isStarted = true
            stage = 0
            progress.snapTo(1f)
            lastProgress.floatValue = 1f

            stage = 1
            progress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)
            )
        }
    }

    LaunchedEffect(isPaused) {
        if (isPaused && progress.isRunning) {
            progress.stop()
            lastProgress.floatValue = progress.value
        } else if (!isPaused && lastProgress.floatValue in 0f..1f && !progress.isRunning) {
            val remaining = (lastProgress.floatValue * durationMillis).toInt()
            progress.snapTo(lastProgress.floatValue)
            progress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = remaining, easing = LinearEasing)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(backgroundColor)
            .drawWithContent {
                val height = size.height
                val centerX = size.width / 2

                val progressHeight = height * progress.value
                drawLine(
                    color = progressColor,
                    start = Offset(centerX, height),
                    end = Offset(centerX, height - progressHeight),
                    strokeWidth = size.width,
                )
            }
    )
}

@Composable
fun PulsingCircle(
    modifier: Modifier = Modifier,
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

    val half = pausePerRepMillis / 2

    val initialProgress = remember(pausePerRepMillis, currentProgressMillis) {
        (currentProgressMillis / pausePerRepMillis.toFloat()).coerceIn(0f, 1f)
    }

    LaunchedEffect(Unit) {
        if (!isStarted && initialProgress > 0f) {
            direction = if (initialProgress <= 0.5f) 1 else -1
            wasPausedMidway = if (initialProgress <= 0.5f) false else true
            needsSecondPhase = (direction == 1)
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
        modifier = modifier
            .fillMaxSize()
            .background(BespokeDarkBlue)
            .clipToBounds(),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints {
            val maxDiameterPx = maxOf(
                constraints.maxWidth.toFloat(),
                constraints.maxHeight.toFloat()
            )

            val animatedDiameter = with(LocalDensity.current) {
                (maxDiameterPx * progress.value).toDp()
            }

            val offsetY = with(LocalDensity.current) { 48.dp.toPx() }
            Canvas(modifier = Modifier.size(animatedDiameter)) {
                drawCircle(
                    color = BespokeBlue,
                    radius = (size.maxDimension * 1.5f) / 2f,
                    center = Offset(x = size.width / 2f, y = size.height / 2f + offsetY)
                )
            }
        }
    }

}


@Composable
fun PreRestCircle(
    modifier: Modifier = Modifier,
) {
    val progress = remember { Animatable(0f) }
    val lastProgress = remember { mutableFloatStateOf(0f) }


    LaunchedEffect(Unit) {
        progress.snapTo(0f)
        lastProgress.floatValue = 0f

        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
    }



    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BespokeDarkBlue)
            .clipToBounds(),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints {
            val maxDiameterPx = maxOf(
                constraints.maxWidth.toFloat(),
                constraints.maxHeight.toFloat()
            )

            val animatedDiameter = with(LocalDensity.current) {
                (maxDiameterPx * progress.value).toDp()
            }
            val offsetY = with(LocalDensity.current) { 48.dp.toPx() }

            Canvas(modifier = Modifier.size(animatedDiameter)) {
                drawCircle(
                    color = Green,
                    radius = (size.maxDimension * 1.5f) / 2f,
                    center = Offset(x = size.width / 2f, y = size.height / 2f + offsetY)
                )
            }
        }
    }

}
