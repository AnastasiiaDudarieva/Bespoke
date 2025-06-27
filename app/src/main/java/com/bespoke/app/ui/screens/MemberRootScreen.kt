package com.bespoke.app.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bespoke.app.R
import com.bespoke.app.data.viewmodel.AuthViewModel
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.BespokeBlue
import com.bespoke.app.ui.theme.NavBarBackgroundColor
import com.bespoke.app.ui.theme.TextDark

sealed class TabItem(val label: String, @DrawableRes val iconRes: Int) {
    object Home : TabItem("Home", R.drawable.ic_home)
    object Schedule : TabItem("Schedule", R.drawable.ic_calendar)
    object Programs : TabItem("Programs", R.drawable.ic_program)
    object Guidance : TabItem("Guidance", R.drawable.ic_guidance)
}

@Composable
fun MemberRootScreen(
    onLogout: () -> Unit,
) {
    var currentTab: TabItem by remember { mutableStateOf(TabItem.Home) }
    val authViewModel: AuthViewModel = hiltViewModel()

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
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                painter = painterResource(id = tab.iconRes),
                                contentDescription = tab.label,
                                tint = if (currentTab == tab) {
                                   BespokeBlue
                                } else {
                                    TextDark
                                }
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                color =  if (currentTab == tab) {
                                    BespokeBlue
                                } else {
                                    TextDark
                                },
                                fontFamily = BeatriceFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp
                            )
                        }, colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentTab) {
                is TabItem.Home -> HomeScreen()
                is TabItem.Schedule -> Text("Schedule")
                is TabItem.Programs -> Text("Programs")
                is TabItem.Guidance -> Text("Guidance")
            }
        }
    }
}
