package com.bespoke.app.ui.models.profile

import java.util.Calendar

data class EditProfileFormData(
    val popupCanBeClosed: Boolean = false,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val gender: String = "Male",
    val dob: String = "",
    val selectedDob: Calendar? = null
)
