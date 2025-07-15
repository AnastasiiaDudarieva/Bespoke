package com.bespoke.app.data.repository

import com.bespoke.app.data.rest.BaseResponse
import com.bespoke.app.data.rest.RequestInviteRequest
import com.bespoke.app.data.rest.RequestInviteSuccessResponse
import com.bespoke.app.data.rest.UpdateEmailRequest
import com.bespoke.app.data.rest.UpdateEmailResponse
import com.bespoke.app.data.services.FirebaseService
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
    ): BaseResponse<UpdateEmailResponse?, Any?> = makeRequest {
        firebaseService.updateUser(
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
    }

}
