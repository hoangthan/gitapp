package com.git.assessment.configuration

import com.git.assessment.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {

    @Provides
    @Named("github_token")
    fun provideGithubToken(): String = BuildConfig.GITHUB_TOKEN
}
