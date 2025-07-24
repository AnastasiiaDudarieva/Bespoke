package com.bespoke.app.ui.components.programs.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bespoke.app.ui.components.base.BespokeTopBar
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.viewmodel.MediaManagerViewModel

@Composable
fun MemberWorkoutGuidanceScreen(
    currentExerciseEntryId: String,
    viewModel: MediaManagerViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
) {
    val exerciseEntry by viewModel.exercise.collectAsState()
    val displayMedia by viewModel.isMediaNotEmpty.collectAsState()

    LaunchedEffect(viewModel.exercise) {
        viewModel.loadExerciseData(currentExerciseEntryId)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BespokeTopBar(
            title = "Review",
            canNavigateBack = true,
            onBackClick = { navController.navigateUp() })

        // Scrollable content
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = exerciseEntry?.name ?: "No name",
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = BeatriceFontFamily
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    exerciseEntry?.let {
                        Text(
                            text = viewModel.parameterListString(it),
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = BeatriceFontFamily
                        )
                    }
                }
            }

            exerciseEntry?.comments?.let { comments ->
                item {
                    Text(
                        text = comments,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        fontFamily = BeatriceFontFamily
                    )
                }
            }

            if (displayMedia) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp)
                    ) {
                        Text(
                            text = "Attached Media",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        MediaManagerView(
                            viewModel = viewModel,
                            horizontalItemPadding = 24.dp
                        )
                    }
                }
            }
        }
    }
}
