package com.bespoke.app.ui.screens

import SetStatusBarIconsDark
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bespoke.app.data.model.ExerciseState
import com.bespoke.app.navigation.Screen
import com.bespoke.app.ui.screens.components.base.ExoVideoPlayer
import com.bespoke.app.ui.screens.components.base.KeepScreenOn
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.BespokeBlue
import com.bespoke.app.ui.theme.BorderGrayColor
import com.bespoke.app.ui.theme.Green
import com.bespoke.app.ui.theme.InputBackgroundColor
import com.bespoke.app.ui.theme.TextDark
import com.bespoke.app.ui.viewmodel.WorkoutDetailViewModel

@Composable
fun WorkoutDetailScreen(
    workoutId: String,
    navController: NavHostController,
    viewModel: WorkoutDetailViewModel = hiltViewModel(),
) {
    SetStatusBarIconsDark(darkIcons = false)
    KeepScreenOn()

    val exerciseState by viewModel.exerciseState.collectAsState()
    val currentExercise by viewModel.currentExerciseEntry.collectAsState()
    val currentSet by viewModel.currentSet.collectAsState()
    val videoUrl by viewModel.videoUrl.collectAsState()
    val thumbnailUrl by viewModel.thumbnailUrl.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val stateText by viewModel.stateText.collectAsState()

    val repCount = viewModel.repCount.collectAsState().value
    val elapsed = viewModel.elapsedSeconds.collectAsState().value


    LaunchedEffect(workoutId) {
        viewModel.loadWorkout(workoutId)
    }

    LaunchedEffect(currentExercise) {
        viewModel.loadVideoUrlIfNeeded()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(modifier = Modifier.weight(0.8f)) {
            videoUrl?.let { url ->

                Box(modifier = Modifier.fillMaxSize()) {
                    ExoVideoPlayer(
                        videoUrl = url,
                        thumbnailUrl = thumbnailUrl,
                    )
                    if (isPaused) {
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
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigateUp() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Color.White,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center

                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }
                if (currentExercise != null) {
                    SetsCounter(
                        currentSet = currentSet,
                        totalSets = currentExercise?.sets ?: 1
                    )
                }
                IconButton(
                    onClick = { /* TODO: Show list of all exercises  */ },
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Color.White,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center

                    ) {
                        Icon(
                            Icons.Filled.MoreHoriz,
                            contentDescription = "More",
                            tint = Color.Black
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(top = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedContent(
                    targetState = stateText,
                    transitionSpec = {
                        (slideInVertically(
                            animationSpec = tween(400)
                        ) { it } + fadeIn(animationSpec = tween(400))) togetherWith
                                (slideOutVertically(
                                    animationSpec = tween(400)
                                ) { -it } + fadeOut(animationSpec = tween(400)))
                    },
                    contentAlignment = Alignment.Center,
                    label = "StateTextAnimation"
                ) { text ->
                    Text(
                        text = text,
                        color = Color.White,
                        fontSize = 48.sp,
                        fontFamily = BeatriceFontFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            currentExercise?.let { exercise ->
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentExercise!!.name ?: "Exercise",
                            fontSize = 18.sp,
                            color = Color.White,
                            fontFamily = BeatriceFontFamily,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { viewModel.toNextExercise() }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.5f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center

                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next Exercise",
                                    tint = TextDark.copy(alpha = 0.5f)
                                )

                            }
                        }
                    }

                    when (exerciseState) {
                        ExerciseState.setsStart, ExerciseState.preActive, ExerciseState.setsFinished -> {
                            Text(
                                text = when (exercise.basedType) {
                                    "Reps" -> "${exercise.reps} Reps" + if (exercise.hasWeights) " • ${exercise.weight} lbs" else ""
                                    "Time" -> "${exercise.time} Sec" + if (exercise.hasWeights) " • ${exercise.weight} lbs" else ""
                                    "Time & Reps" -> "${exercise.time} Sec • ${exercise.reps} Reps" + if (exercise.hasWeights) " • ${exercise.weight} lbs" else ""
                                    else -> ""
                                },
                                fontSize = 24.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontFamily = BeatriceFontFamily
                            )

                        }

                        ExerciseState.active -> {
                            when (exercise.basedType) {
                                "Time" -> {
                                    val remainingTime = exercise.time - elapsed
                                    val clampedTime = if (remainingTime < 0) 0 else remainingTime
                                    val minutes = clampedTime / 60
                                    val seconds = clampedTime % 60
                                    val timeString = String.format("%01d:%02d", minutes, seconds)

                                    Text(
                                        text = timeString,
                                        fontSize = 24.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        fontFamily = BeatriceFontFamily
                                    )
                                }

                                "Reps" -> {
                                    val repText = String.format("%02d", repCount) +
                                            if (exercise.hasWeights) " • ${exercise.weight} kg" else ""

                                    Text(
                                        text = repText,
                                        fontSize = 24.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        fontFamily = BeatriceFontFamily
                                    )
                                }

                                "Time & Reps" -> {
                                    val remainingTime = exercise.time - elapsed
                                    val clampedTime = if (remainingTime < 0) 0 else remainingTime
                                    val minutes = clampedTime / 60
                                    val seconds = clampedTime % 60
                                    val timeString = String.format("%01d:%02d", minutes, seconds)

                                    val repString = "${exercise.reps} Reps" +
                                            if (exercise.hasWeights) " • ${exercise.weight} lbs" else ""

                                    Text(
                                        text = "$timeString • $repString",
                                        fontSize = 24.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                        fontFamily = BeatriceFontFamily
                                    )
                                }

                                else -> Unit
                            }
                        }

                        ExerciseState.rest, ExerciseState.preRest -> {
                            val remainingRest = exercise.rest - elapsed
                            val clampedRest = if (remainingRest < 0) 0 else remainingRest
                            val min = clampedRest / 60
                            val sec = clampedRest % 60
                            val restString = String.format("%01d:%02d", min, sec)

                            Text(
                                text = restString,
                                fontSize = 24.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontFamily = BeatriceFontFamily
                            )
                        }


                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }


        }


        currentExercise?.let { exercise ->
            when (exerciseState) {

                ExerciseState.active -> {
                    when (exercise.basedType) {
                        "Time", "Time & Reps" -> {
                            ProgressLine(
                                durationMillis = exercise.time * 1000,
                                progressTrigger = exercise.time,
                                isPaused = isPaused
                            )
                        }

                        "Reps" -> {
                            PulsingLines(
                                pausePerRepMillis = viewModel.timePerRepMillis(exercise).toInt(),
                                repPauseTrigger = repCount,
                                isPaused = isPaused
                            )
                        }
                    }
                }

                ExerciseState.rest -> {
                    ProgressLine(
                        durationMillis = exercise.rest * 1000,
                        progressTrigger = exercise.rest,
                        isPaused = isPaused
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f)
                .background(Color.White)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {

                    IconButton(
                        onClick = {
                            currentExercise?.let {
                                viewModel.selectExercise(it)
                                navController.navigate("${Screen.EXERCISE}?exerciseId=${it.id}")
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(56.dp)
                            .background(
                                InputBackgroundColor,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChecklistRtl,
                            contentDescription = "Review",
                            tint = TextDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Text(
                        text = "Review",
                        fontSize = 14.sp,
                        color = TextDark,
                        fontFamily = BeatriceFontFamily,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = 52.dp)
                    )
                }


                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {

                    IconButton(
                        onClick = { viewModel.togglePause() },
                        modifier = Modifier
                            .size(96.dp)
                            .background(
                                if (isPaused) BespokeBlue else InputBackgroundColor,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                            contentDescription = "Play/Pause",
                            tint = if (isPaused) Color.White else TextDark,
                            modifier = Modifier.size(48.dp)
                        )

                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {

                    IconButton(
                        onClick = { viewModel.toNextSet() },
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.Center)

                            .background(
                                InputBackgroundColor,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next",
                            tint = TextDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Text(
                        text = "Next",
                        fontSize = 14.sp,
                        color = TextDark,
                        fontFamily = BeatriceFontFamily,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = 52.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun SetsCounter(
    currentSet: Int,
    totalSets: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SET $currentSet/$totalSets",
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            color = Color.White,
            fontFamily = BeatriceFontFamily
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (idx in 0 until totalSets) {
                Box(
                    modifier = Modifier
                        .size(width = 24.dp, height = 2.dp)
                        .background(
                            color = if (idx < currentSet) Color.White else Color(0xFFB0B0B0)
                        )
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
) {
    val progress = remember { Animatable(0f) }
    val lastProgress = remember { mutableFloatStateOf(0f) }
    var direction by remember { mutableIntStateOf(1) }
    var wasPausedMidway by remember { mutableStateOf(false) }
    var needsSecondPhase by remember { mutableStateOf(false) }

    val half = pausePerRepMillis / 2

    // Старт репу
    LaunchedEffect(repPauseTrigger) {
        if (repPauseTrigger > 0) {
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
        if (isPaused) {
            if (progress.isRunning) {
                progress.stop()
                lastProgress.floatValue = progress.value
                wasPausedMidway = true
            }
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

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .background(BorderGrayColor)
            .height(6.dp)
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val lineHeight = size.height

        val leftStart = Offset(centerX, centerY)
        val leftEnd = Offset(centerX - centerX * progress.value, centerY)
        val rightStart = Offset(centerX, centerY)
        val rightEnd = Offset(centerX + centerX * progress.value, centerY)

        if (progress.value > 0f) {
            drawLine(
                color = BespokeBlue,
                start = leftStart,
                end = leftEnd,
                strokeWidth = lineHeight,
                cap = StrokeCap.Round
            )
            drawLine(
                color = BespokeBlue,
                start = rightStart,
                end = rightEnd,
                strokeWidth = lineHeight,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun ProgressLine(
    durationMillis: Int,
    progressTrigger: Int,
    isPaused: Boolean,
) {
    val progress = remember { Animatable(0f) }
    val lastProgress = remember { mutableFloatStateOf(0f) }
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

    LaunchedEffect(isPaused) {
        if (isPaused) {
            progress.stop()
            lastProgress.floatValue = progress.value
        } else {
            val remainingDuration = ((1f - lastProgress.floatValue) * durationMillis).toInt()
            progress.snapTo(lastProgress.floatValue)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = remainingDuration, easing = LinearEasing)
            )
        }
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(BorderGrayColor)
    ) {
        val width = size.width
        val centerY = size.height / 2
        val lineHeight = size.height

        val start = Offset(0f, centerY)
        val end = Offset(width * progress.value, centerY)

        if (progress.value > 0f) {
            drawLine(
                color = Green,
                start = start,
                end = end,
                strokeWidth = lineHeight,
                cap = StrokeCap.Round
            )
        }
    }
}

