package com.bespoke.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bespoke.app.data.repository.FirebaseRepository
import com.bespoke.app.data.repository.MemberRepository
import com.bespoke.app.ui.models.profile.EditProfileFormData
import com.bespoke.app.ui.models.profile.EditProfileUiState
import com.bespoke.app.utils.formatPhoneNumber
import com.bespoke.app.utils.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class EditMemberProfileViewModel @Inject constructor(
    private val memberRepository: MemberRepository,
    private val firebaseRepository: FirebaseRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        memberRepository.member.value?.let { member ->
            val calendar = member.dob?.let {
                Calendar.getInstance().apply { time = Date(it * 1000L) }
            }

            _uiState.value = _uiState.value.copy(
                data = EditProfileFormData(
                    firstName = member.firstName.orEmpty(),
                    lastName = member.lastName.orEmpty(),
                    phone = member.phoneNumber.orEmpty(),
                    email = member.email.orEmpty(),
                    address = member.location.orEmpty(),
                    gender = member.gender.orEmpty(),
                    dob = calendar?.let { formatDob(it.time) }.orEmpty(),
                    selectedDob = calendar
                )
            )
        }
    }

    // --- Field updates ---
    fun onFirstNameChanged(value: String) = update { it.copy(firstName = value) }
    fun onLastNameChanged(value: String) = update { it.copy(lastName = value) }
    fun onPhoneChanged(input: String) {
        val formatted = input.formatPhoneNumber()
        _uiState.update { it.copy(data = it.data.copy(phone = formatted)) }
    }
    fun onEmailChanged(value: String) = update { it.copy(email = value) }
    fun onAddressChanged(value: String) = update { it.copy(address = value) }
    fun onGenderChanged(value: String) = update { it.copy(gender = value) }

    private val today = System.currentTimeMillis()
    fun onDateSelected(selectedMillis: Long?) {
        if (selectedMillis != null && selectedMillis > today) {
            _uiState.value =
                _uiState.value.copy(message = "Invalid date selected. Please select a valid date.")
            return
        }
        val calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("UTC")
            timeInMillis = selectedMillis!!
        }
        update {
            it.copy(
                selectedDob = calendar,
                dob = formatDob(Date(selectedMillis!!))
            )
        }
    }

    // --- Save ---
    fun saveProfile() {
        val data = _uiState.value.data

        if (!isAllFieldsValid(data)) {
            _uiState.value =
                _uiState.value.copy(message = "Please fill in all required fields correctly")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null, error = null)

            try {
                val member = memberRepository.member.value ?: return@launch
                val response = firebaseRepository.updateUserProfile(
                    currentEmail = member.email.orEmpty(),
                    newEmail = data.email.lowercase(),
                    newPhoneNumber = data.phone,
                    firstName = data.firstName,
                    lastName = data.lastName,
                    location = data.address,
                    gender = data.gender,
                    dob = data.selectedDob?.timeInMillis?.div(1000)?.toInt() ?: 0
                )

                _uiState.value = if (response.first) {
                    _uiState.value.copy(
                        isLoading = false,
                        isUpdated = true,
                        message = response.second
                    )
                } else {
                    _uiState.value.copy(isLoading = false, error = response.second?:"Update failed")
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    // --- Helpers ---
    private fun isAllFieldsValid(data: EditProfileFormData): Boolean {
        return data.firstName.isNotBlank() &&
                data.lastName.isNotBlank() &&
                data.address.isNotBlank() &&
                data.dob.isNotBlank() &&
                data.email.isValidEmail()
    }

    private fun formatDob(date: Date): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(date)
    }

    private fun update(transform: (EditProfileFormData) -> EditProfileFormData) {
        _uiState.value = _uiState.value.copy(data = transform(_uiState.value.data))
    }
}
