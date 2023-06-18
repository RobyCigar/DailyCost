package com.dcns.dailycost.data.datasource.remote.di

import com.dcns.dailycost.BuildConfig
import com.dcns.dailycost.data.datasource.remote.BalanceHandlerImpl
import com.dcns.dailycost.data.datasource.remote.DepoHandlerImpl
import com.dcns.dailycost.data.datasource.remote.LoginRegisterHandlerImpl
import com.dcns.dailycost.data.datasource.remote.ShoppingHandlerImpl
import com.dcns.dailycost.data.datasource.remote.handlers.BalanceHandler
import com.dcns.dailycost.data.datasource.remote.handlers.DepoHandler
import com.dcns.dailycost.data.datasource.remote.handlers.LoginRegisterHandler
import com.dcns.dailycost.data.datasource.remote.handlers.ShoppingHandler
import com.dcns.dailycost.data.datasource.remote.services.BalanceService
import com.dcns.dailycost.data.datasource.remote.services.LoginRegisterService
import com.dcns.dailycost.data.datasource.remote.services.ShoppingService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteModule {

    @Provides
    fun provideBaseUrl(): String = BuildConfig.API_BASE_URL

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }

            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        } else {
            OkHttpClient
                .Builder()
                .build()
        }
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        baseUrl :String
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideLoginRegisterService(
        retrofit: Retrofit
    ): LoginRegisterService = retrofit.create(LoginRegisterService::class.java)

    @Provides
    @Singleton
    fun provideShoppingService(
        retrofit: Retrofit
    ): ShoppingService = retrofit.create(ShoppingService::class.java)

    @Provides
    @Singleton
    fun provideBalanceService(
        retrofit: Retrofit
    ): BalanceService = retrofit.create(BalanceService::class.java)

    @Provides
    @Singleton
    fun provideLoginRegisterHandler(
        impl: LoginRegisterHandlerImpl
    ): LoginRegisterHandler = impl

    @Provides
    @Singleton
    fun provideShoppingHandler(
        impl: ShoppingHandlerImpl
    ): ShoppingHandler = impl

    @Provides
    @Singleton
    fun provideBalanceHandler(
        impl: BalanceHandlerImpl
    ): BalanceHandler = impl

    @Provides
    @Singleton
    fun provideDepoHandler(
        impl: DepoHandlerImpl
    ): DepoHandler = impl

}