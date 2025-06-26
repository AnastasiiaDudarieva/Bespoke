package com.bespoke.app.data.rest

data class RequestInviteRequest(
    val email: String
)

data class RequestInviteSuccessResponse(
    val status: Boolean,
    val message: String?
)
