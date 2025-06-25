package com.libraries.github.domain.usecase

import com.git.data.core.model.FailureError
import com.git.data.core.model.Result
import com.libraries.github.domain.model.GithubRepo
import com.libraries.github.domain.repository.GithubRepository
import javax.inject.Inject

sealed interface GetUserReposError : FailureError {
    object UserNotFound : GetUserReposError
    object ExceedRateLimit : GetUserReposError
}

data class GetUserRepoParam(
    val username: String,
    val limit: Int,
    val page: Int
)

interface GetUserRepoUseCase {
    suspend operator fun invoke(param: GetUserRepoParam): Result<GetUserReposError, List<GithubRepo>>
}

internal class GetUserRepoUseCaseImpl @Inject constructor(
    private val githubRepository: GithubRepository
) : GetUserRepoUseCase {

    override suspend operator fun invoke(param: GetUserRepoParam): Result<GetUserReposError, List<GithubRepo>> {
        if (param.username.isEmpty()) {
            return Result.Error(GetUserReposError.UserNotFound)
        }
        
        return githubRepository.getUserRepos(
            username = param.username,
            limit = param.limit,
            page = param.page,
            fork = false // By requirement, we will ignore the forked repository
        )
    }
}
