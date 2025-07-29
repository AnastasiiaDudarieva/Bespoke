package com.bespoke.app.ui.screens

import SetStatusBarIconsDark
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bespoke.app.R
import com.bespoke.app.data.model.lengthDisplay
import com.bespoke.app.navigation.Screen
import com.bespoke.app.ui.screens.components.base.BespokeTopBar
import com.bespoke.app.ui.screens.components.base.FirebaseStorageImageView
import com.bespoke.app.ui.screens.components.programs.details.EquipmentNeeded
import com.bespoke.app.ui.screens.components.programs.details.ProgramSectionBlock
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.BespokeBlue
import com.bespoke.app.ui.theme.TextDark
import com.bespoke.app.ui.viewmodel.ProgramOverviewViewModel
import kotlinx.coroutines.launch

@Composable
fun ProgramOverviewScreen(
    programId: String,
    navController: NavHostController,
    viewModel: ProgramOverviewViewModel = hiltViewModel(),
) {

    val programs by viewModel.programs.collectAsState()
    val loadedProgram = programs.find { it.id == programId }

    val programState by viewModel.program.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(loadedProgram) {
        if (loadedProgram != null) {
            viewModel.loadProgramData(loadedProgram)
        }
    }

    val program = programState ?: return
    val scrollState = rememberLazyListState()

    val showCollapsedHeader by remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex > 0 || scrollState.firstVisibleItemScrollOffset > 150
        }
    }
    SetStatusBarIconsDark(darkIcons = showCollapsedHeader)

    val toolbarColor by animateColorAsState(
        targetValue = if (showCollapsedHeader) Color.White else Color.Transparent,
        label = "ToolbarColor"
    )


    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(390.dp)
                ) {
                    program.thumbnail?.let {
                        FirebaseStorageImageView(
                            gsPath = it,
                            cornerRadius = 0
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent),
                                    startY = 0f,
                                    endY = 300f
                                )
                            )
                    )

                    AnimatedVisibility(visible = !showCollapsedHeader) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(
                                    top = WindowInsets.statusBars.asPaddingValues()
                                        .calculateTopPadding() + 96.dp,
                                    start = 24.dp, end = 24.dp
                                )
                        ) {
                            Text(
                                "${program.lengthDisplay()} MIN",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                lineHeight = 48.sp,
                                text = program.title ?: "",
                                fontSize = 40.sp,
                                color = Color.White,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = BeatriceFontFamily
                            )
                        }
                    }
                }
            }

            item {

                Box(
                    modifier = Modifier
                        .offset(y = (-48).dp)
                        .padding(start = 24.dp)
                ) {
                    IconButton(
                        onClick = {

                            coroutineScope.launch {
                                isLoading = true
                                val workout = viewModel.startWorkout()
                                isLoading = false
                                navController.navigate("${Screen.WORKOUT}?workoutId=${workout?.id}")
                            }

                        },
                        modifier = Modifier
                            .size(96.dp)
                            .background(BespokeBlue, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.program_overview),
                    style = MaterialTheme.typography.titleSmall,
                    color = TextDark,
                    modifier = Modifier.padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
                )
            }

            item {
                EquipmentNeeded(program = program, viewModel)
            }

            itemsIndexed(program.sections) { index, section ->
                ProgramSectionBlock(
                    section = section,
                    index = index + 1,
                    viewModel = viewModel,
                    onExerciseClick = {exercise->
                        navController.navigate("${Screen.EXERCISE}?exerciseId=${exercise.id}")
                    }
                )
                if (index < program.sections.lastIndex) {
                    HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(toolbarColor)
                .statusBarsPadding(),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!showCollapsedHeader) {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .padding(start = 16.dp, top = 4.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    }
                }

                AnimatedContent(
                    targetState = showCollapsedHeader,
                    label = "TitleSwitch"
                ) { show ->
                    if (show) {
                        Column {
                            BespokeTopBar(
                                title = program.title ?: "",
                                canNavigateBack = true,
                                onBackClick = { navController.navigateUp() }
                            )
                            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)
                        }
                    } else {
                        Spacer(Modifier.width(0.dp))
                    }
                }
            }
        }
    }
}