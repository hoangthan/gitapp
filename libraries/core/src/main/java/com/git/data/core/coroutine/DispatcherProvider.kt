package com.git.data.core.coroutine

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

interface DispatcherProvider {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unConfined: CoroutineDispatcher
}

internal class AppDispatcherProvider @Inject constructor() : DispatcherProvider {
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val unConfined: CoroutineDispatcher = Dispatchers.Unconfined
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DispatcherProviderModule {

    @Binds
    @Singleton
    abstract fun provideAppDispatcher(impl: AppDispatcherProvider): DispatcherProvider
}
