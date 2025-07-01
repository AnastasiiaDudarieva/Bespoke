package com.bespoke.app.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bespoke.app.R
import com.bespoke.app.data.model.dobFormatted
import com.bespoke.app.data.viewmodel.AccountViewModel
import com.bespoke.app.navigation.Screen
import com.bespoke.app.ui.components.base.BespokeTopBar
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.TextDark

@Composable
fun AccountScreen(
    navController: NavHostController,
) {
    val viewModel: AccountViewModel = hiltViewModel()
    val member by viewModel.member.collectAsState()
    val navigateToEdit by viewModel.navigateToEdit.collectAsState()

    LaunchedEffect(navigateToEdit) {
        if (navigateToEdit) {
            navController.navigate(Screen.EDIT_ACCOUNT)
            viewModel.onEditNavigated()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            BespokeTopBar(
                title = stringResource(R.string.account),
                canNavigateBack = true,
                onBackClick = { navController.popBackStack() },
            )

            member?.let { m ->
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        AccountCardItem(titleRes = R.string.first_name, value = m.firstName ?: "")
                        HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

                        AccountCardItem(titleRes = R.string.last_name, value = m.lastName ?: "")
                        HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

                        AccountCardItem(titleRes = R.string.dob, value = m.dobFormatted())
                        HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

                        AccountCardItem(titleRes = R.string.email, value = m.email ?: "")
                    }
                }
            }
        }
    }
}

@Composable
fun AccountCardItem(@StringRes titleRes: Int, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 24.dp)
    ) {
        Text(
            text = stringResource(titleRes).uppercase(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.W600,
            color = TextDark
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = TextDark,

            )
    }
}


