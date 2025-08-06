package com.bespoke.app.data.services

import com.bespoke.app.data.rest.BaseResponse
import com.bespoke.app.data.rest.RequestInviteRequest
import com.bespoke.app.data.rest.RequestInviteSuccessResponse
import com.bespoke.app.data.rest.UpdateEmailRequest
import com.bespoke.app.data.rest.UpdateEmailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FirebaseService {
    @POST("/requestInvite")
    suspend fun requestInvite(
        @Body request: RequestInviteRequest
    ): Response<BaseResponse<RequestInviteSuccessResponse?, Any?>>

    @POST("/updateUser")
    suspend fun updateUser(
        @Body request: UpdateEmailRequest
    ): Response<UpdateEmailResponse>
}
