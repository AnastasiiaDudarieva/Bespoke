package com.bespoke.app.data.repository

import com.bespoke.app.data.rest.BaseResponse
import com.bespoke.app.data.rest.RequestInviteRequest
import com.bespoke.app.data.rest.RequestInviteSuccessResponse
import com.bespoke.app.data.services.FirebaseService
import javax.inject.Inject

class FirebaseRepository @Inject constructor(private val firebaseService: FirebaseService):BaseRepository(){

    suspend fun requestInvite(email: String): BaseResponse<RequestInviteSuccessResponse?, Any?> = makeRequest {
        firebaseService.requestInvite(RequestInviteRequest(email))
    }
}
