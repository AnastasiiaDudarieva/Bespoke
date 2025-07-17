package com.bespoke.app.ui.screens

import SetStatusBarIconsDark
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bespoke.app.R
import com.bespoke.app.ui.components.base.BespokeTopBar
import com.bespoke.app.ui.components.programs.NoUpcomingProgramsMessage
import com.bespoke.app.ui.components.programs.NotStartedProgramView
import com.bespoke.app.ui.components.programs.PastWorkoutRow
import com.bespoke.app.ui.components.programs.TodayWorkoutRow
import com.bespoke.app.ui.components.programs.UpcomingProgramRow
import com.bespoke.app.ui.theme.TextDark
import com.bespoke.app.ui.viewmodel.ProgramsViewModel

@Composable
fun ProgramsScreen(
    navController: NavHostController = rememberNavController(),
) {
    SetStatusBarIconsDark(darkIcons = true)

    val viewModel: ProgramsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberLazyListState()


    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            BespokeTopBar(
                title = stringResource(R.string.programs),
                canNavigateBack = false,
                onBackClick = {}
            )
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)


            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 24.dp, horizontal = 24.dp),
                state = scrollState
            ) {
                itemsIndexed(uiState.pastWorkouts) { _, pastWorkout ->
                    PastWorkoutRow(
                        pastWorkout,
                        onClick = { })
                    HorizontalDivider(
                        color = Color.Transparent,
                        thickness = 12.dp,
                    )
                }

                if (uiState.todayWorkouts.isNotEmpty()) {
                    itemsIndexed(uiState.todayWorkouts) { index, todayWorkouts ->
                        TodayWorkoutRow(
                            workout = todayWorkouts,
                            onResumeClick = { workout ->
                            }
                        )
                        if (index < uiState.todayWorkouts.lastIndex) {
                            HorizontalDivider(
                                color = Color.Transparent,
                                thickness = 48.dp,
                            )
                        }
                    }
                }

                if (uiState.todayPrograms.isNotEmpty()) {
                    itemsIndexed(uiState.todayPrograms) { index, todayPrograms ->
                        HorizontalDivider(
                            color = Color.Transparent,
                            thickness = 48.dp,
                        )
                        NotStartedProgramView(todayPrograms)
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                item {
                    Text(
                        text = stringResource(R.string.upcoming_programs_header),
                        style = MaterialTheme.typography.titleSmall, color = TextDark,
                        modifier = Modifier.padding(top = 28.dp, bottom = 32.dp)
                    )
                }

                if (uiState.upcomingPrograms.isEmpty()) {
                    item {
                        NoUpcomingProgramsMessage()
                    }
                } else {
                    itemsIndexed(uiState.upcomingPrograms) { index, upcomingPrograms ->
                        UpcomingProgramRow(upcomingPrograms, onClick = {})
                        HorizontalDivider(
                            color = Color.Transparent,
                            thickness = 12.dp,
                        )
                    }
                }
            }
        }
    }
}

