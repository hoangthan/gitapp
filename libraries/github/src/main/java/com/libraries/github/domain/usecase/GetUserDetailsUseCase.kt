package com.libraries.github.domain.usecase

import com.git.data.core.model.FailureError
import com.git.data.core.model.Result
import com.libraries.github.domain.model.GithubUserDetail
import com.libraries.github.domain.repository.GithubRepository
import javax.inject.Inject

sealed interface GetUserDetailError : FailureError {
    object UserNotFound : GetUserDetailError
    object ExceedRateLimit : GetUserDetailError
}

data class GetUserDetailParam(
    val username: String
)

interface GetUserDetailsUseCase {
    suspend operator fun invoke(param: GetUserDetailParam): Result<GetUserDetailError, GithubUserDetail>
}

internal class GetUserDetailsUseCaseImpl @Inject constructor(
    private val githubRepository: GithubRepository
) : GetUserDetailsUseCase {

    override suspend operator fun invoke(param: GetUserDetailParam): Result<GetUserDetailError, GithubUserDetail> {
        if (param.username.isEmpty()) {
            return Result.Error(GetUserDetailError.UserNotFound)
        }
        
        return githubRepository.getUserDetail(
            username = param.username
        )
    }
}
