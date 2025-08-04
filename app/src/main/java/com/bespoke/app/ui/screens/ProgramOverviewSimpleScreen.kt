package com.bespoke.app.ui.screens

import SetStatusBarIconsDark
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bespoke.app.R
import com.bespoke.app.navigation.Screen
import com.bespoke.app.ui.screens.components.base.BespokeTopBar
import com.bespoke.app.ui.screens.components.programs.details.ProgramSectionBlock
import com.bespoke.app.ui.viewmodel.ProgramOverviewViewModel

@Composable
fun ProgramOverviewSimpleScreen(
    programId: String,
    navController: NavHostController,
    viewModel: ProgramOverviewViewModel = hiltViewModel(),
) {
    val program by viewModel.program.collectAsState()

    LaunchedEffect(programId) {
        viewModel.loadProgramData(programId)
    }

    val scrollState = rememberLazyListState()
    SetStatusBarIconsDark(darkIcons = true)
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            BespokeTopBar(
                title = stringResource(R.string.review),
                canNavigateBack = true,
                onBackClick = { navController.navigateUp() }
            )
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)


            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = scrollState
            ) {

                program?.let { program ->
                    itemsIndexed(program.sections) { index, section ->
                        ProgramSectionBlock(
                            section = section,
                            index = index + 1,
                            viewModel = viewModel,
                            onExerciseClick = { exercise ->
                                navController.navigate("${Screen.EXERCISE}?exerciseId=${exercise.id}")
                            }
                        )
                        if (index < program.sections.lastIndex) {
                            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}