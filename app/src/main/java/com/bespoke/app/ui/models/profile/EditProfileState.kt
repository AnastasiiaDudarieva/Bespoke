package com.bespoke.app.ui.models.profile

data class EditProfileUiState(
    val data: EditProfileFormData = EditProfileFormData(),
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null,
    val isUpdated: Boolean = false
)

