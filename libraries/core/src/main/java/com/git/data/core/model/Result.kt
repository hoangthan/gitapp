package com.git.data.core.model

sealed class Result<E : Failure, out T> {

    data class Success<E : Failure, out T>(val value: T) : Result<E, T>()

    data class Error<E : Failure, out T>(val failure: Failure) : Result<E, T>()

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val getOrNull: T?
        get() = when (this) {
            is Success -> value
            is Error -> null
        }

    val failureOrNull: Failure?
        get() = when (this) {
            is Error -> failure
            is Success -> null
        }

    inline fun <R> fold(
        onSuccess: (T) -> R,
        onFailure: (Failure) -> R
    ): R = when (this) {
        is Success -> onSuccess(value)
        is Error -> onFailure(failure)
    }

    suspend inline fun <R> foldSuspend(
        crossinline onSuccess: suspend (T) -> R,
        crossinline onFailure: suspend (Failure) -> R
    ): R = when (this) {
        is Success -> onSuccess(value)
        is Error -> onFailure(failure)
    }

    inline fun <U> map(transform: (T) -> U): Result<E, U> =
        when (this) {
            is Success -> Success(transform(value))
            is Error -> Error(failure)
        }

    suspend inline fun <U> mapSuspend(crossinline transform: suspend (T) -> U): Result<E, U> =
        when (this) {
            is Success -> Success(transform(value))
            is Error -> Error(failure)
        }

    inline fun <U> flatMap(transform: (T) -> Result<E, U>): Result<E, U> =
        when (this) {
            is Success -> transform(value)
            is Error -> Error(failure)
        }

    suspend inline fun <U> flatMapSuspend(crossinline transform: suspend (T) -> Result<E, U>): Result<E, U> =
        when (this) {
            is Success -> transform(value)
            is Error -> Error(failure)
        }
}
