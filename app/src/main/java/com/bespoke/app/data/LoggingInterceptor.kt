package com.bespoke.app.data
import com.bespoke.app.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

class LoggingInterceptor : Interceptor {
    private var mLoggingInterceptor: Interceptor? = null

    init {
        mLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
    }

    companion object{
        fun create(): LoggingInterceptor {
            return LoggingInterceptor()
        }
    }


    override fun intercept(chain: Interceptor.Chain): Response {
        return mLoggingInterceptor!!.intercept(chain)
    }
}