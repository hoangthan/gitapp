package com.features.github.searchuser

import androidx.paging.PagingSource
import com.git.data.core.model.Result
import com.libraries.github.domain.model.GithubUser
import com.libraries.github.domain.usecase.SearchUserError
import com.libraries.github.domain.usecase.SearchUserParam
import com.libraries.github.domain.usecase.SearchUserUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchUserPagingSourceTest {

    private lateinit var useCase: SearchUserUseCase
    private lateinit var pagingSource: SearchUserPagingSource
    private val query = "test"
    private val pageSize = 20

    @Before
    fun setup() {
        useCase = mockk()
        pagingSource = SearchUserPagingSource(useCase, query, pageSize)
    }

    @Test
    fun `load returns Page with data when useCase returns success`() = runTest {
        // Given
        val page = 1
        val users = listOf(
            GithubUser("user1", "https://example.com/avatar1.jpg"),
            GithubUser("user2", "https://example.com/avatar2.jpg")
        )
        coEvery { 
            useCase(SearchUserParam(query, pageSize, page)) 
        } returns Result.Success(users)

        // When
        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = page,
                loadSize = pageSize,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(loadResult is PagingSource.LoadResult.Page)
        loadResult as PagingSource.LoadResult.Page
        assertEquals(users, loadResult.data)
        assertEquals(page + 1, loadResult.nextKey)
        assertEquals(null, loadResult.prevKey) // First page has no previous key
    }

    @Test
    fun `load returns Page with empty data when useCase returns empty list`() = runTest {
        // Given
        val page = 1
        val users = emptyList<GithubUser>()
        coEvery { 
            useCase(SearchUserParam(query, pageSize, page)) 
        } returns Result.Success(users)

        // When
        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = page,
                loadSize = pageSize,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(loadResult is PagingSource.LoadResult.Page)
        loadResult as PagingSource.LoadResult.Page
        assertEquals(users, loadResult.data)
        assertEquals(null, loadResult.nextKey) // No next page for empty results
        assertEquals(null, loadResult.prevKey)
    }

    @Test
    fun `load returns Error when useCase returns error`() = runTest {
        // Given
        val page = 1
        coEvery { 
            useCase(SearchUserParam(query, pageSize, page)) 
        } returns Result.Error(SearchUserError.RateLimitExceeded)

        // When
        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = page,
                loadSize = pageSize,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(loadResult is PagingSource.LoadResult.Error)
        loadResult as PagingSource.LoadResult.Error
        assertTrue(loadResult.throwable is PagingFailure)
        assertEquals(SearchUserError.RateLimitExceeded, (loadResult.throwable as PagingFailure).failure)
    }

    @Test
    fun `load with null key starts from page 1`() = runTest {
        // Given
        val users = listOf(
            GithubUser("user1", "https://example.com/avatar1.jpg"),
            GithubUser("user2", "https://example.com/avatar2.jpg")
        )
        coEvery { 
            useCase(SearchUserParam(query, pageSize, 1)) 
        } returns Result.Success(users)

        // When
        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null, // Null key
                loadSize = pageSize,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(loadResult is PagingSource.LoadResult.Page)
        loadResult as PagingSource.LoadResult.Page
        assertEquals(users, loadResult.data)
        assertEquals(2, loadResult.nextKey) // Next page should be 2
        assertEquals(null, loadResult.prevKey)
    }
}