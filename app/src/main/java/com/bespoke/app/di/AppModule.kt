package com.bespoke.app.di

import com.bespoke.app.BuildConfig
import com.bespoke.app.data.LoggingInterceptor
import com.bespoke.app.data.repository.MemberRepository
import com.bespoke.app.data.services.AuthService
import com.bespoke.app.data.services.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * A module for the application, provides most major services and structural components
 * of the application. The module[AppModule] defines the creation and behavior of some entities.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.ENDPOINT)
            .client(client)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(LoggingInterceptor.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFirebaseService(retrofit: Retrofit): FirebaseService {
        return retrofit.create(FirebaseService::class.java)
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideMemberStore(
        firebaseFirestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
    ): MemberRepository = MemberRepository(
        firebaseFirestore,
        AuthService(firebaseAuth)
    )

}