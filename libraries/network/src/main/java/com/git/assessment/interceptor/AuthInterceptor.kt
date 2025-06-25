package com.git.assessment.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named

internal class AuthInterceptor @Inject constructor(
    @Named("github_token") private val githubToken: String?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val modifiedRequest = if (githubToken.isNullOrBlank().not()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $githubToken")
                .header("Accept", "application/vnd.github+json")
                .build()
        } else {
            // Use unauthenticated access
            originalRequest
        }

        return chain.proceed(modifiedRequest)
    }
}
