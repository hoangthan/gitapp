package com.libraries.github.data.repository

import com.git.assessment.utils.toFailureException
import com.git.data.core.model.FailureError
import com.git.data.core.model.FailureException.UnknownException
import com.git.data.core.model.Result
import com.libraries.github.data.datasource.remote.GithubApiService
import com.libraries.github.data.datasource.remote.dto.toDomain
import com.libraries.github.domain.model.GithubRepo
import com.libraries.github.domain.model.GithubUser
import com.libraries.github.domain.model.GithubUserDetail
import com.libraries.github.domain.repository.GithubRepository
import com.libraries.github.domain.usecase.GetUserDetailError
import com.libraries.github.domain.usecase.GetUserReposError
import com.libraries.github.domain.usecase.SearchUserError
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.retrofit.statusCode
import java.net.HttpURLConnection
import javax.inject.Inject

internal class GithubRepositoryImpl @Inject constructor(
    private val githubApiService: GithubApiService
) : GithubRepository {

    override suspend fun searchUsers(
        query: String, limit: Int, page: Int
    ): Result<SearchUserError, List<GithubUser>> {
        val response = githubApiService.searchUsers(query = query, perPage = limit, page = page)

        return response.toResult(
            successMapper = { it.items.map { it -> it.toDomain() } },
            errorMapper = {
                when (it.statusCode.code) {
                    HttpURLConnection.HTTP_FORBIDDEN -> SearchUserError.RateLimitExceeded
                    HTTP_UNPROCESS_ENTITY -> SearchUserError.InvalidQuery
                    else -> null
                }
            })
    }

    override suspend fun getUserDetail(
        username: String
    ): Result<GetUserDetailError, GithubUserDetail> {
        val response = githubApiService.getUserDetail(username = username)

        return response.toResult(successMapper = { it.toDomain() }, errorMapper = {
            when (it.statusCode.code) {
                HttpURLConnection.HTTP_NOT_FOUND -> GetUserDetailError.UserNotFound
                HttpURLConnection.HTTP_FORBIDDEN -> GetUserDetailError.ExceedRateLimit
                else -> null
            }
        })
    }

    override suspend fun getUserRepos(
        username: String, fork: Boolean, limit: Int, page: Int
    ): Result<GetUserReposError, List<GithubRepo>> {
        val response = githubApiService.getUserRepos(
            username = username,
            perPage = limit,
            page = page,
            fork = fork,
        )

        return response.toResult(
            successMapper = { repos -> repos.map { it.toDomain() } },
            errorMapper = {
                when (it.statusCode.code) {
                    HttpURLConnection.HTTP_NOT_FOUND -> GetUserReposError.UserNotFound
                    HttpURLConnection.HTTP_FORBIDDEN -> GetUserReposError.ExceedRateLimit
                    else -> null
                }
            })
    }

    private inline fun <T, V, E> ApiResponse<T>.toResult(
        crossinline successMapper: (T) -> V,
        crossinline errorMapper: (ApiResponse.Failure.Error) -> E?
    ): Result<E, V> where E : FailureError {
        return when (this) {
            is ApiResponse.Success -> Result.Success(successMapper(data))
            is ApiResponse.Failure.Exception -> Result.Error(toFailureException())
            is ApiResponse.Failure.Error -> Result.Error(errorMapper(this) ?: UnknownException)
        }
    }

    companion object {
        private const val HTTP_UNPROCESS_ENTITY = 422
    }
}
