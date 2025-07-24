package com.bespoke.app.data.rest

open class BaseResponse<T, V>(
    val status: Boolean = true,
    val data: T? = null,
    val message: String? = null,
    val errorMessage: String? = null
)