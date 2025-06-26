package com.bespoke.app.data

import com.bespoke.app.data.rest.RequestInviteRequest

class FirebaseRepository {

    suspend fun requestInvite(email: String): Result<Pair<Boolean, String?>> {
        return try {
            val response = FirebaseService.api.requestInvite(RequestInviteRequest(email))
            if (response.isSuccessful) {
                val body = response.body()
                Result.success(Pair(body?.status ?: false, body?.message))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.success(Pair(false, "Invite request failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
