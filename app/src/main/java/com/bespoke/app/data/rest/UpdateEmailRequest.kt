package com.bespoke.app.data.rest

data class UpdateEmailRequest(
    val currentEmail: String,
    val newEmail: String,
    val newPhoneNumber: String,
    val firstName: String,
    val lastName: String,
    val location: String,
    val gender: String,
    val dob: Int
)