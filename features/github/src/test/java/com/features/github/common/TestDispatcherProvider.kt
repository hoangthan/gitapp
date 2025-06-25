package com.features.github.common

import com.git.data.core.coroutine.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher

class TestDispatcherProvider : DispatcherProvider {
    private val testDispatcher = StandardTestDispatcher()

    override val io: CoroutineDispatcher
        get() = testDispatcher

    override val main: CoroutineDispatcher
        get() = testDispatcher

    override val default: CoroutineDispatcher
        get() = testDispatcher

    override val unConfined: CoroutineDispatcher
        get() = testDispatcher
}