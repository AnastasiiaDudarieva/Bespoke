package com.bespoke.app.data.repository

import com.bespoke.app.data.rest.BaseResponse
import com.bespoke.app.data.rest.ErrorResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

open class BaseRepository {

    suspend fun <T, V> makeRequest(request: suspend () -> Response<BaseResponse<T, V>>) =
        withContext(Dispatchers.IO) {
            val response = request.invoke()
            try {
                if (response.isSuccessful && response.body() != null)
                    return@withContext response.body()!!
                else
                    return@withContext processError(response)
            } catch (e: HttpException) {
                e.printStackTrace()
                return@withContext BaseResponse(
                    status = false,
                    errorMessage = e.message()
                )
            } catch (e: Throwable) {
                e.printStackTrace()
                return@withContext BaseResponse(status = false)
            }
        }

    private fun <T, V, W> processError(response: Response<W>): BaseResponse<T, V> {
        if (response.isSuccessful)
            return BaseResponse(
                true
            )
        val error = Gson().fromJson(
            response.errorBody()?.string(),
            ErrorResponse::class.java
        )

        return BaseResponse(
            false,
            null,
            null,
            error,
            error?.error
        )
    }
}