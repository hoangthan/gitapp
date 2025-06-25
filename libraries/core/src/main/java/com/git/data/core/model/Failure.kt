package com.git.data.core.model

sealed interface Failure

// Represents a logical or domain-specific error which is predictable, will be defined per use-case
interface FailureError : Failure

// Represents a  un-predictable exception, which is not defined per use-case.
sealed interface FailureException : Failure {
    object NetworkException : FailureException
    object IoException : FailureException
    object UnknownException : FailureException
}
