package com.bespoke.app.data.rest

import com.google.gson.annotations.SerializedName

data class UpdateEmailResponse(
    @SerializedName("success") val success: Boolean,
    val message: String?,
    val oldEmail: String?,
    val newEmail: String?,
    val oldPhone: String?,
    val newPhone: String?
)