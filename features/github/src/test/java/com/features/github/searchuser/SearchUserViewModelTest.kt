package com.features.github.searchuser

import androidx.paging.PagingSource
import app.cash.turbine.test
import com.features.github.common.TestDispatcherProvider
import com.git.data.core.model.FailureException
import com.git.data.core.model.Result
import com.libraries.github.domain.model.GithubUser
import com.libraries.github.domain.usecase.SearchUserError
import com.libraries.github.domain.usecase.SearchUserParam
import com.libraries.github.domain.usecase.SearchUserUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchUserViewModelTest {

    private lateinit var viewModel: SearchUserViewModel
    private lateinit var dispatcherProvider: TestDispatcherProvider
    private val searchUserUseCase: SearchUserUseCase = mockk()

    private val pageSize = 20
    private val query = "username"

    @Before
    fun setup() {
        dispatcherProvider = TestDispatcherProvider()
        viewModel = SearchUserViewModel(dispatcherProvider, searchUserUseCase)
    }

    @Test
    fun `given default ViewModel when initialized then state should be empty`() = runTest {
        // Then - query should be empty
        viewModel.viewState.test {
            assertEquals("", awaitItem().query)
        }
    }

    @Test
    fun `given Search event when dispatchEvent called then query state is updated`() = runTest {
        // When - Search event is dispatched
        viewModel.viewState.test {
            skipItems(1)
            viewModel.dispatchEvent(SearchUserViewEvent.Search(query))

            // Then - query should be updated
            assertEquals(query, awaitItem().query)
        }
    }

    @Test
    fun `given valid query when paging source loads then return success page`() = runTest {
        // Given - mocked use case returns valid users
        val page = 1
        val users = listOf(
            GithubUser(username = "user1", avatarUrl = "https://google.com"),
            GithubUser(username = "user2", avatarUrl = "https://google.com")
        )
        coEvery {
            searchUserUseCase(SearchUserParam(keyword = query, limit = pageSize, page = page))
        } returns Result.Success(users)

        val pagingSource = SearchUserPagingSource(searchUserUseCase, query, pageSize)

        // When - load is triggered
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = page,
                loadSize = pageSize,
                placeholdersEnabled = false
            )
        )

        // Then - success page is returned
        val expected = PagingSource.LoadResult.Page(
            data = users,
            prevKey = null,
            nextKey = page + 1
        )
        assertEquals(expected, result)
    }

    @Test
    fun `given invalid query when paging source loads then return empty page`() = runTest {
        // Given - mocked use case returns InvalidQuery error
        val page = 1
        coEvery {
            searchUserUseCase(SearchUserParam(keyword = query, limit = pageSize, page = page))
        } returns Result.Error(SearchUserError.InvalidQuery)

        val pagingSource = SearchUserPagingSource(searchUserUseCase, query, pageSize)

        // When - load is triggered
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = page,
                loadSize = pageSize,
                placeholdersEnabled = false
            )
        )

        // Then - empty page returned
        val expected = PagingSource.LoadResult.Page(
            data = emptyList(),
            prevKey = null,
            nextKey = null
        )
        assertEquals(expected, result)
    }

    @Test
    fun `given unexpected failure when paging source loads then return error result`() = runTest {
        // Given - mocked use case returns unexpected failure
        val page = 1
        val failure = FailureException.UnknownException

        coEvery {
            searchUserUseCase(SearchUserParam(keyword = query, limit = pageSize, page = page))
        } returns Result.Error(failure)

        val pagingSource = SearchUserPagingSource(searchUserUseCase, query, pageSize)

        // When - load is triggered
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = page,
                loadSize = pageSize,
                placeholdersEnabled = false
            )
        )

        // Then - error with PagingFailure is returned
        Assert.assertTrue(result is PagingSource.LoadResult.Error<Int, GithubUser>)
    }
}
