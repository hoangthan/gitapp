package com.features.github.searchuser

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.git.data.core.model.Failure
import com.git.data.core.model.Result
import com.libraries.github.domain.model.GithubUser
import com.libraries.github.domain.usecase.SearchUserError
import com.libraries.github.domain.usecase.SearchUserParam
import com.libraries.github.domain.usecase.SearchUserUseCase

// Custom exception to wrap Failure objects for use with LoadResult.Error
class PagingFailure(val failure: Failure) : Exception(failure.toString())

class SearchUserPagingSource(
    private val searchUserUseCase: SearchUserUseCase,
    private val query: String,
    private val pageSize: Int = 20
) : PagingSource<Int, GithubUser>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GithubUser> {
        // Start from page 1 if undefined
        val page = params.key ?: 1

        val searchParam = SearchUserParam(keyword = query, limit = pageSize, page = page)

        return when (val result = searchUserUseCase(searchParam)) {
            is Result.Success -> {
                val users = result.value
                val nextKey = if (users.isEmpty()) null else page + 1

                LoadResult.Page(
                    data = users,
                    prevKey = if (page > 1) page - 1 else null,
                    nextKey = nextKey
                )
            }

            is Result.Error -> {
                when (result.failure) {
                    // For invalid query, we stop pagination
                    SearchUserError.InvalidQuery -> {
                        LoadResult.Page(
                            data = emptyList(),
                            prevKey = if (page > 1) page - 1 else null,
                            nextKey = null
                        )
                    }

                    // For other errors, propagate the error
                    else -> LoadResult.Error(PagingFailure(result.failure))
                }
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GithubUser>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
