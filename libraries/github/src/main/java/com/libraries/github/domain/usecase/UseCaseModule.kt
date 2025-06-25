package com.libraries.github.domain.usecase

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun bindSearchUserUseCase(impl: SearchUserUseCaseImpl): SearchUserUseCase
    
    @Binds
    @Singleton
    abstract fun bindGetUserDetailsUseCase(impl: GetUserDetailsUseCaseImpl): GetUserDetailsUseCase
    
    @Binds
    @Singleton
    abstract fun bindGetUserRepoUseCase(impl: GetUserRepoUseCaseImpl): GetUserRepoUseCase
}
