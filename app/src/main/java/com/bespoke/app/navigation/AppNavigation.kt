package com.bespoke.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bespoke.app.ui.models.auth.AuthState
import com.bespoke.app.ui.screens.AccountScreen
import com.bespoke.app.ui.screens.EditMemberProfileScreen
import com.bespoke.app.ui.screens.MemberRootScreen
import com.bespoke.app.ui.screens.ProfileScreen
import com.bespoke.app.ui.screens.ProgramOverviewScreen
import com.bespoke.app.ui.screens.ProgramsScreen
import com.bespoke.app.ui.screens.SettingsScreen
import com.bespoke.app.ui.screens.WelcomeScreen
import com.bespoke.app.ui.screens.WorkoutDetailScreen
import com.bespoke.app.ui.screens.components.base.WebViewScreen
import com.bespoke.app.ui.screens.components.programs.details.MemberWorkoutGuidanceScreen
import com.bespoke.app.ui.viewmodel.AuthViewModel


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    when(authState) {
        is AuthState.Success -> {
            androidx.navigation.compose.NavHost(
                navController = navController,
                startDestination = Screen.MEMBER_ROOT
            ) {
                composable(Screen.MEMBER_ROOT) {
                    MemberRootScreen(navController = navController)
                }
                composable(Screen.PROFILE) {
                    ProfileScreen( navController = navController)
                }
                composable(Screen.SETTINGS) {
                    SettingsScreen( navController = navController)
                }
                composable(Screen.ACCOUNT) {
                    AccountScreen( navController = navController)
                }
                composable(Screen.EDIT_ACCOUNT) {
                    EditMemberProfileScreen( navController = navController)
                }
                composable(route = "${Screen.WEB_VIEW}?url={url}",
                    arguments = listOf(
                        navArgument("url") { defaultValue = ""; nullable = true }
                    )
                ) { backStackEntry ->
                    val url = backStackEntry.arguments?.getString("url") ?: ""
                    WebViewScreen(url = url, navController = navController)
                }
                composable(Screen.PROGRAMS) {
                    ProgramsScreen(navController = navController)
                }
                composable(
                    route = "${Screen.PROGRAM_OVERVIEW}?programId={programId}",
                    arguments = listOf(navArgument("programId") { defaultValue = ""; nullable = false })
                ) { backStackEntry ->
                    val programId = backStackEntry.arguments?.getString("programId") ?: ""
                    ProgramOverviewScreen(programId = programId, navController = navController)
                }
                composable(
                    route = "${Screen.EXERCISE}?exerciseId={exerciseId}",
                    arguments = listOf(navArgument("exerciseId") { defaultValue = ""; nullable = false })
                ) { backStackEntry ->
                    val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
                    MemberWorkoutGuidanceScreen(currentExerciseEntryId = exerciseId, navController = navController)
                }

                composable(
                    route = "${Screen.WORKOUT}?workoutId={workoutId}",
                    arguments = listOf(navArgument("workoutId") { defaultValue = ""; nullable = false })
                ) { backStackEntry ->
                    val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
                    WorkoutDetailScreen(workoutId = workoutId, navController = navController)
                }
            }
        }
        else -> {
            WelcomeScreen()
        }
    }
}

