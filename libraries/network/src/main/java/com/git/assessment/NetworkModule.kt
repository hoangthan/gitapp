package com.git.assessment

import com.git.assessment.config.ConnectionConfig
import com.git.assessment.interceptor.AuthInterceptor
import com.git.libraries.network.BuildConfig
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    internal fun provideMoshiConverter(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    @Singleton
    internal fun provideConverterFactory(moshi: Moshi): Converter.Factory {
        return MoshiConverterFactory.create(moshi).asLenient()
    }

    @Provides
    @Singleton
    internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    @Provides
    @Singleton
    internal fun provideHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(httpLoggingInterceptor)
        }

        return clientBuilder
            .addInterceptor(authInterceptor)
            .connectTimeout(ConnectionConfig.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(ConnectionConfig.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(ConnectionConfig.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    internal fun provideCallAdapterFactory(): CallAdapter.Factory {
        return ApiResponseCallAdapterFactory.create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        callAdapterFactory: CallAdapter.Factory,
        converterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .baseUrl("https://api.github.com/")
            .build()
    }
}
