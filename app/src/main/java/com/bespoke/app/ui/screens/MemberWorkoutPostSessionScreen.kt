package com.bespoke.app.ui.screens

import SetStatusBarIconsDark
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bespoke.app.R
import com.bespoke.app.ui.screens.components.base.BespokeButton
import com.bespoke.app.ui.screens.components.base.BespokeTopBar
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.InputBackgroundColor
import com.bespoke.app.ui.viewmodel.WorkoutPostSessionViewModel


@Composable
fun MemberWorkoutPostSessionScreen(
    navController: NavHostController,
    viewModel: WorkoutPostSessionViewModel = hiltViewModel(),
) {
    SetStatusBarIconsDark(darkIcons = true)
    val workout by viewModel.selectedWorkout.collectAsState()

    val sessionTime = workout?.sessionTimeSecs?.let { formatSessionTime(it) } ?: "00:00"
    val caloriesBurned = workout?.caloriesBurned?.toInt()?.toString()?.plus(" Cal") ?: "0 Cal"

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column{
            BespokeTopBar(
                title = stringResource(R.string.session_complete),
                canNavigateBack = true,
                onBackClick = { navController.navigateUp() },
                icon = Icons.Filled.Close
            )
            Column(modifier = Modifier.padding(24.dp).weight(1f, fill = true)) {
                Text(
                    text = stringResource(R.string.amazing_work),
                    fontSize = 28.sp,
                    fontFamily = BeatriceFontFamily
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoBlock(
                        modifier = Modifier.weight(1f),
                        title = "Session time",
                        value = sessionTime
                    )
                    InfoBlock(
                        modifier = Modifier.weight(1f),
                        title = "Calories burned",
                        value = caloriesBurned
                    )
                }

            }
            BespokeButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp),
                text = stringResource(R.string.submit)
            )
        }
    }
}

@Composable
private fun InfoBlock(modifier: Modifier = Modifier, title: String, value: String) {
    Column(
        modifier = modifier
            .height(84.dp)
            .background(InputBackgroundColor, RoundedCornerShape(4.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = BeatriceFontFamily
        )
        Text(
            text = value,
            fontSize = 24.sp,
            fontFamily = BeatriceFontFamily
        )
    }
}

private fun formatSessionTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%02d:%02d", minutes, secs)
    }
}
