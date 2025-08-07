package com.bespoke.app.ui.screens

import SetStatusBarIconsDark
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.ChecklistRtl
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bespoke.app.R
import com.bespoke.app.data.model.ExerciseEntry
import com.bespoke.app.data.model.ExerciseState
import com.bespoke.app.data.model.Provider
import com.bespoke.app.navigation.Screen
import com.bespoke.app.ui.screens.components.base.CircleIconButton
import com.bespoke.app.ui.screens.components.base.CustomAvatar
import com.bespoke.app.ui.screens.components.base.KeepScreenOn
import com.bespoke.app.ui.screens.components.programs.workout.CenteredTextWithIcon
import com.bespoke.app.ui.screens.components.programs.workout.SetsCounter
import com.bespoke.app.ui.screens.components.programs.workout.TimerPage
import com.bespoke.app.ui.screens.components.programs.workout.VideoPage
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.BespokeBlue
import com.bespoke.app.ui.theme.InputBackgroundColor
import com.bespoke.app.ui.theme.TextDark
import com.bespoke.app.ui.theme.iconSize
import com.bespoke.app.ui.viewmodel.WorkoutDetailViewModel
import com.bespoke.app.utils.AudioPlayer
import kotlinx.coroutines.launch

@Composable
fun WorkoutDetailScreen(
    workoutId: String,
    navController: NavHostController,
    viewModel: WorkoutDetailViewModel = hiltViewModel(),
) {
    SetStatusBarIconsDark(darkIcons = false)
    KeepScreenOn()

    val context = LocalContext.current
    val audioPlayer = remember { AudioPlayer(context) }
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

    val workout by viewModel.currentWorkout.collectAsState()
    val exerciseState by viewModel.exerciseState.collectAsState()
    val currentExercise by viewModel.currentExerciseEntry.collectAsState()
    val currentSet by viewModel.currentSet.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val stateText by viewModel.stateText.collectAsState()
    val provider by viewModel.provider.collectAsState()
    val repCount by viewModel.repCount.collectAsState()
    val elapsed by viewModel.elapsedSeconds.collectAsState()

    LaunchedEffect(stateText, repCount) {
        if (!isPaused) playAudioCues(audioPlayer, stateText, repCount)
    }
    LaunchedEffect(exerciseState) {
        if (!isPaused) playStateAudio(audioPlayer, exerciseState)
    }
    LaunchedEffect(workoutId) { viewModel.loadWorkout(workoutId) }
    LaunchedEffect(currentExercise) { viewModel.loadMediaUrlsIfNeeded() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(modifier = Modifier.weight(0.8f)) {
            WorkoutPager(pagerState, viewModel)

            WorkoutTopBar(
                currentExercise = currentExercise,
                currentSet = currentSet,
                provider = provider,
                onClose = {
                    coroutineScope.launch {
                        if (!isPaused) viewModel.togglePause()
                        viewModel.updateWorkout()
                        navController.navigateUp()
                    }
                },
                onAvatarClick = {
                    currentExercise?.let {
                        viewModel.selectExercise(it)
                        navController.navigate("${Screen.EXERCISE}?exerciseId=${it.id}")
                    }
                },
                onMoreClick = {
                    workout?.programId?.let { id ->
                        navController.navigate("${Screen.PROGRAM_OVERVIEW_SIMPLE}?programId=$id")
                    }
                }
            )

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
                    if (pagerState.currentPage == 1 && text == "Recover")
                        return@AnimatedContent
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
                ExerciseInfo(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp),
                    exercise, exerciseState, elapsed, repCount, viewModel
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
                            contentDescription = stringResource(R.string.review),
                            tint = TextDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    if (currentExercise?.mediaList?.isNotEmpty() == true) {
                        Box(
                            modifier = Modifier
                                .offset(x = 20.dp, y = 20.dp)
                                .size(22.dp)
                                .background(color = BespokeBlue, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = "Attachment",
                                modifier = Modifier.size(12.dp),
                                tint = Color.White
                            )
                        }
                    }


                    Text(
                        text = stringResource(R.string.review),
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
                            contentDescription = stringResource(R.string.next),
                            tint = TextDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.next),
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
private fun WorkoutPager(pagerState: PagerState, viewModel: WorkoutDetailViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 2,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> VideoPage(viewModel = viewModel)

                1 -> TimerPage(viewModel = viewModel)
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(2) { index ->
                val color = if (pagerState.currentPage == index)
                    Color.White else Color(0xFFB0B0B0)

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(6.dp)
                        .background(color = color, shape = MaterialTheme.shapes.small)
                )
            }
        }
    }
}

@Composable
private fun WorkoutTopBar(
    currentExercise: ExerciseEntry?,
    currentSet: Int,
    provider: Provider?,
    onClose: () -> Unit,
    onAvatarClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleIconButton(icon = Icons.Filled.Close, contentDescription = "Back", onClick = onClose)
        if (currentExercise?.mediaList?.isNotEmpty() == true) {
            Spacer(Modifier.width(32.dp))
        }
        currentExercise?.let {
            SetsCounter(currentSet = currentSet, totalSets = it.sets)
        }
        if (currentExercise?.mediaList?.isNotEmpty() == true) {
            provider?.let {
                CustomAvatar(
                    url = it.avatar,
                    size = 32.dp,
                    firstName = it.firstName.orEmpty(),
                    lastName = it.lastName.orEmpty(),
                    modifier = Modifier.clickable(onClick = onAvatarClick)
                )
            }
        }

        CircleIconButton(
            icon = Icons.Filled.MoreHoriz,
            contentDescription = "Program Overview",
            onClick = onMoreClick
        )
    }
}


@Composable
private fun ExerciseInfo(
    modifier: Modifier,
    exercise: ExerciseEntry,
    exerciseState: ExerciseState,
    elapsed: Int,
    repCount: Int,
    viewModel: WorkoutDetailViewModel,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CenteredTextWithIcon(
            text = exercise.name ?: "Exercise",
            onNext = { viewModel.toNextExercise() }
        )
        Text(
            text = getExerciseInfoText(exercise, exerciseState, elapsed, repCount),
            fontSize = 24.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontFamily = BeatriceFontFamily
        )
        Spacer(modifier = Modifier.height(iconSize))
    }
}


private fun playAudioCues(audioPlayer: AudioPlayer, stateText: String, repCount: Int) {
    when (stateText) {
        "Get Ready!" -> audioPlayer.play(R.raw.get_ready)
        "Go" -> audioPlayer.play(R.raw.start)
        "Complete!" -> audioPlayer.play(R.raw.end)
    }
    if (repCount > 0) audioPlayer.play(R.raw.bip)
}

private fun playStateAudio(audioPlayer: AudioPlayer, state: ExerciseState) {
    when (state) {
        ExerciseState.rest -> audioPlayer.play(R.raw.recover)
        ExerciseState.active -> audioPlayer.play(R.raw.bip)
        else -> {}
    }
}

private fun getExerciseInfoText(
    exercise: ExerciseEntry,
    state: ExerciseState,
    elapsed: Int,
    repCount: Int,
): String {
    return when (state) {
        ExerciseState.active -> when (exercise.basedType) {
            "Time" -> formatTime(exercise.time - elapsed)
            "Reps" -> String.format("%02d", repCount) +
                    if (exercise.hasWeights) " • ${exercise.weight} kg" else ""

            "Time & Reps" -> "${formatTime(exercise.time - elapsed)} • ${exercise.reps} Reps" +
                    if (exercise.hasWeights) " • ${exercise.weight} lbs" else ""

            else -> ""
        }

        ExerciseState.preRest -> ""
        ExerciseState.rest -> formatTime(exercise.rest - elapsed)
        ExerciseState.setsStart, ExerciseState.preActive, ExerciseState.setsFinished -> when (exercise.basedType) {
            "Reps" -> "${exercise.reps} Reps" + if (exercise.hasWeights) " • ${exercise.weight} lbs" else ""
            "Time" -> "${exercise.time} Sec" + if (exercise.hasWeights) " • ${exercise.weight} lbs" else ""
            "Time & Reps" -> "${exercise.time} Sec • ${exercise.reps} Reps" + if (exercise.hasWeights) " • ${exercise.weight} lbs" else ""
            else -> ""
        }
    }
}

private fun formatTime(seconds: Int): String {
    val clamped = seconds.coerceAtLeast(0)
    return String.format("%01d:%02d", clamped / 60, clamped % 60)
}
