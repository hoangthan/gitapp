package com.git.features.core.ui.utils

import android.content.Context
import com.git.data.core.model.Failure
import com.git.data.core.model.FailureError
import com.git.data.core.model.FailureException
import com.git.features.core.R


fun FailureException.defaultExceptionMapper(context: Context): String {
    return when (this) {
        FailureException.IoException -> context.getString(R.string.io_exception)
        FailureException.NetworkException -> context.getString(R.string.internet_connection)
        FailureException.UnknownException -> context.getString(R.string.unknown_exception)
    }
}

inline fun <reified T> Failure.toErrorString(
    context: Context,
    exceptionHandler: (FailureException) -> String = { it.defaultExceptionMapper(context) },
    errorHandler: (T) -> String,
): String {
    return when (this) {
        is FailureError -> errorHandler(this as T)
        is FailureException -> exceptionHandler(this)
    }
}
