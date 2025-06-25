package com.git.assessment.utils

import com.git.data.core.model.FailureException
import com.skydoves.sandwich.ApiResponse
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException

fun ApiResponse.Failure.Exception.toFailureException(): FailureException {
    return when (throwable) {
        // Handle network exception
        is SSLHandshakeException, is SSLPeerUnverifiedException, is SSLException, is HttpException,
        is SocketException, is SocketTimeoutException, is UnknownHostException,
        is UnknownServiceException -> FailureException.NetworkException

        // Handle to IO Exception
        is EOFException, is IOException -> FailureException.IoException

        // Default exception
        else -> FailureException.UnknownException
    }
}
