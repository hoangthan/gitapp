package com.libraries.github.data.datasource.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RemoteDataSourceModule {

    @Provides
    @Singleton
    internal fun provideGithubApiService(retrofit: Retrofit): GithubApiService {
        return retrofit.create(GithubApiService::class.java)
    }
}
