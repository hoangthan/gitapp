package com.features.github.userdetail

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.git.data.core.model.Failure
import com.git.data.core.model.Result
import com.libraries.github.domain.model.GithubRepo
import com.libraries.github.domain.usecase.GetUserRepoParam
import com.libraries.github.domain.usecase.GetUserRepoUseCase

// Custom exception to wrap Failure objects for use with LoadResult.Error
class RepoPagingFailure(val failure: Failure) : Exception(failure.toString())

class UserRepoPagingSource(
    private val getUserRepoUseCase: GetUserRepoUseCase,
    private val username: String,
    private val pageSize: Int = 20
) : PagingSource<Int, GithubRepo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GithubRepo> {
        // Start from page 1 if undefined
        val page = params.key ?: 1

        val repoParam = GetUserRepoParam(username = username, limit = pageSize, page = page)

        return when (val result = getUserRepoUseCase(repoParam)) {
            is Result.Success -> {
                val repos = result.value
                val nextKey = if (repos.isEmpty()) null else page + 1

                LoadResult.Page(
                    data = repos,
                    prevKey = if (page > 1) page - 1 else null,
                    nextKey = nextKey
                )
            }

            is Result.Error -> {
                LoadResult.Error(RepoPagingFailure(result.failure))
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GithubRepo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
