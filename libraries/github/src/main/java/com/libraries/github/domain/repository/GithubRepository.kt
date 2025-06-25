package com.libraries.github.domain.repository

import com.git.data.core.model.Result
import com.libraries.github.domain.model.GithubRepo
import com.libraries.github.domain.model.GithubUser
import com.libraries.github.domain.model.GithubUserDetail
import com.libraries.github.domain.usecase.GetUserDetailError
import com.libraries.github.domain.usecase.GetUserReposError
import com.libraries.github.domain.usecase.SearchUserError

interface GithubRepository {
    suspend fun searchUsers(
        query: String,
        limit: Int,
        page: Int
    ): Result<SearchUserError, List<GithubUser>>

    suspend fun getUserDetail(username: String): Result<GetUserDetailError, GithubUserDetail>

    suspend fun getUserRepos(
        username: String,
        fork: Boolean,
        limit: Int,
        page: Int
    ): Result<GetUserReposError, List<GithubRepo>>
}
