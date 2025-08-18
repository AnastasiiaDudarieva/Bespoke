package com.bespoke.app.ui.screens

import SetStatusBarIconsDark
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bespoke.app.R
import com.bespoke.app.navigation.Screen
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.BespokeBlue
import com.bespoke.app.ui.theme.NavBarBackgroundColor
import com.bespoke.app.ui.theme.TextDark

sealed class TabItem(val label: String, @DrawableRes val iconRes: Int) {
    data object Home : TabItem("Home", R.drawable.ic_home)
    data object Schedule : TabItem("Schedule", R.drawable.ic_calendar)
    data object Programs : TabItem("Programs", R.drawable.ic_program)
    data object Guidance : TabItem("Guidance", R.drawable.ic_guidance)
}

@Composable
fun MemberRootScreen(
    navController: NavHostController,
) {
    SetStatusBarIconsDark(darkIcons = true)

    val tabNavController = rememberNavController()
    val currentTabBackstack by tabNavController.currentBackStackEntryAsState()
    val currentTabDestination = currentTabBackstack?.destination?.route

    var currentTab by remember { mutableStateOf<TabItem>(TabItem.Home) }

    // визначаємо активний таб на основі внутрішнього tabNavController
    LaunchedEffect(currentTabDestination) {
        currentTab = when (currentTabDestination) {
            Screen.HOME -> TabItem.Home
            Screen.SCHEDULE -> TabItem.Schedule
            Screen.PROGRAMS -> TabItem.Programs
            Screen.GUIDANCE -> TabItem.Guidance
            else -> currentTab
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = NavBarBackgroundColor) {
                listOf(
                    TabItem.Home,
                    TabItem.Schedule,
                    TabItem.Programs,
                    TabItem.Guidance
                ).forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = {
                            currentTab = tab
                            tabNavController.navigate(
                                when (tab) {
                                    TabItem.Home -> Screen.HOME
                                    TabItem.Schedule -> Screen.SCHEDULE
                                    TabItem.Programs -> Screen.PROGRAMS
                                    TabItem.Guidance -> Screen.GUIDANCE
                                }
                            ) {
                                launchSingleTop = true
                                popUpTo(tabNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = tab.iconRes),
                                contentDescription = tab.label,
                                tint = if (currentTab == tab) BespokeBlue else TextDark
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                color = if (currentTab == tab) BespokeBlue else TextDark,
                                fontFamily = BeatriceFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            androidx.navigation.compose.NavHost(
                navController = tabNavController,
                startDestination = Screen.HOME
            ) {
                composable(Screen.HOME) { HomeScreen(navController) }
                composable(Screen.SCHEDULE) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Schedule",
                            textAlign = TextAlign.Center
                        )
                    }
                }
                composable(Screen.PROGRAMS) { ProgramsScreen(navController) }
                composable(Screen.GUIDANCE) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Guidance",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

