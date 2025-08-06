package com.bespoke.app.data.repository

import com.bespoke.app.data.rest.BaseResponse
import com.bespoke.app.data.rest.ErrorResponse
import com.bespoke.app.data.rest.RequestInviteRequest
import com.bespoke.app.data.rest.RequestInviteSuccessResponse
import com.bespoke.app.data.rest.UpdateEmailRequest
import com.bespoke.app.data.services.FirebaseService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseRepository @Inject constructor(private val firebaseService: FirebaseService) :
    BaseRepository() {

    suspend fun requestInvite(email: String): BaseResponse<RequestInviteSuccessResponse?, Any?> =
        makeRequest {
            firebaseService.requestInvite(RequestInviteRequest(email))
        }

    suspend fun updateUserProfile(
        currentEmail: String,
        newEmail: String,
        newPhoneNumber: String,
        firstName: String,
        lastName: String,
        location: String,
        gender: String,
        dob: Int,
    ): Pair<Boolean, String?> =
        withContext(Dispatchers.IO) {
            val response = firebaseService.updateUser(
                UpdateEmailRequest(
                    currentEmail,
                    newEmail,
                    newPhoneNumber,
                    firstName,
                    lastName,
                    location,
                    gender,
                    dob
                )
            )
            if (response.isSuccessful) {
                val body = response.body() ?: return@withContext false to "Unknown error"
                return@withContext true to body.message
            } else {
                val errorJson = response.errorBody()?.string()
                val errorResponse = try {
                    Gson().fromJson(errorJson, ErrorResponse::class.java)
                } catch (e: Exception) {
                    null
                }
                return@withContext false to (
                        errorResponse?.message
                            ?: "Unknown server error"
                        )
            }
        }


}
