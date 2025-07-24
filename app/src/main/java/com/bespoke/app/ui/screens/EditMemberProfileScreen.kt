package com.bespoke.app.ui.screens

import SetStatusBarIconsDark
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilterChipDefaults.filterChipColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bespoke.app.R
import com.bespoke.app.ui.components.base.BespokeDatePickerInput
import com.bespoke.app.ui.components.base.BespokeInput
import com.bespoke.app.ui.components.base.BespokeTopBar
import com.bespoke.app.ui.theme.BeatriceFontFamily
import com.bespoke.app.ui.theme.BespokeBlue
import com.bespoke.app.ui.theme.PlaceholderColor
import com.bespoke.app.ui.viewmodel.EditMemberProfileViewModel
import com.bespoke.app.utils.formatPhoneNumber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMemberProfileScreen(navController: NavHostController) {
    SetStatusBarIconsDark(darkIcons = true)
    val viewModel: EditMemberProfileViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val formData = state.data
    val snackbarHostState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = formData.selectedDob?.timeInMillis
    )

    val datePickerColors = DatePickerDefaults.colors(
        selectedDayContainerColor = BespokeBlue,
        todayDateBorderColor = BespokeBlue,
        selectedYearContainerColor = BespokeBlue,
        selectedYearContentColor = Color.White,
        weekdayContentColor = BespokeBlue,
        dayContentColor = Color.DarkGray,
        disabledDayContentColor = Color.LightGray,
        yearContentColor = BespokeBlue,
        navigationContentColor = BespokeBlue
    )

    if (showDatePicker) {
        DatePickerDialog(
            colors = datePickerColors,
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDateSelected(datePickerState.selectedDateMillis)
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("CANCEL") }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                BespokeTopBar(
                    title = "Account",
                    canNavigateBack = true,
                    onBackClick = { navController.popBackStack() },
                    actions = {
                        IconButton(onClick = { viewModel.saveProfile() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = "Save"
                            )
                        }
                    }
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .imePadding(),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        BespokeInput(
                            value = formData.firstName,
                            onValueChange = viewModel::onFirstNameChanged,
                            label = stringResource(R.string.first_name_),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )
                    }

                    item {
                        BespokeInput(
                            value = formData.lastName,
                            onValueChange = viewModel::onLastNameChanged,
                            label = stringResource(R.string.last_name_),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )
                    }

                    item {
                        BespokeInput(
                            value = formData.phone.formatPhoneNumber(),
                            onValueChange = viewModel::onPhoneChanged,
                            label = stringResource(R.string.phone_number),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            ),
                        )
                    }

                    item {
                        BespokeInput(
                            value = formData.address,
                            onValueChange = viewModel::onAddressChanged,
                            label = stringResource(R.string.address_),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )
                    }

                    item {
                        BespokeInput(
                            value = formData.email,
                            onValueChange = viewModel::onEmailChanged,
                            label = stringResource(R.string.email),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            )
                        )
                    }

                    item {
                        BespokeDatePickerInput(
                            value = formData.dob.ifEmpty { "MM/DD/YYYY" },
                            label = stringResource(R.string.date_of_birth_),
                            onClick = {
                                showDatePicker = true
                                Log.d("EditMemberProfileScreen", "Date Picker Clicked")
                            }
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Male", "Female").forEach { gender ->
                                val isSelected = formData.gender == gender
                                FilterChip(
                                    modifier = Modifier.weight(1f),
                                    selected = isSelected,
                                    onClick = { viewModel.onGenderChanged(gender) },
                                    label = {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = gender,
                                                style = TextStyle(
                                                    fontFamily = BeatriceFontFamily,
                                                    fontWeight = FontWeight.Normal,
                                                    fontSize = 16.sp,
                                                ),
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )
                                        }
                                    },
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = PlaceholderColor,
                                        selectedBorderColor = BespokeBlue
                                    ),
                                    colors = filterChipColors(
                                        selectedContainerColor = BespokeBlue,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.Transparent,
                                        labelColor = PlaceholderColor,
                                    )
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .pointerInput(Unit) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 32.dp)
        )
    }

    // Show snackbars for messages
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(state.isUpdated) {
        if (state.isUpdated) {
            navController.popBackStack()
        }
    }
}

