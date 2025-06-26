package com.bespoke.app.data

import com.bespoke.app.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FirebaseService {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.ENDPOINT + "/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(LoggingInterceptor.create())
                    .build()
            )
            .build()
    }

    val api: FirebaseApi by lazy {
        retrofit.create(FirebaseApi::class.java)
    }
}
