package com.bespoke.app.ui.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.bespoke.app.data.viewmodel.AuthViewModel

@Composable
fun MemberRootScreen(viewModel: AuthViewModel,  onLogout: () -> Unit) {
    var selectedTab by remember { mutableStateOf(MemberTab.Home) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                MemberTab.entries.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab }
                    )
                }
            }
        }
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (selectedTab) {
                MemberTab.Home -> Text("Home Screen")
                MemberTab.Schedule -> Text("Schedule Screen")
                MemberTab.Programs -> Text("Programs Screen")
                MemberTab.Guidance -> Text("Guidance Screen")
            }
        }
    }
}

enum class MemberTab(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Filled.Home),
    Schedule("Schedule", Icons.Filled.CalendarToday),
    Programs("Programs", Icons.Filled.MenuBook),
    Guidance("Guidance", Icons.Filled.Person)
} 
