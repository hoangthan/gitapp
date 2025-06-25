package com.features.github.userdetail

import androidx.paging.PagingSource
import com.git.data.core.model.Result
import com.libraries.github.domain.model.GithubRepo
import com.libraries.github.domain.usecase.GetUserReposError
import com.libraries.github.domain.usecase.GetUserRepoParam
import com.libraries.github.domain.usecase.GetUserRepoUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepoPagingSourceTest {

    private lateinit var pagingSource: UserRepoPagingSource
    private lateinit var getUserRepoUseCase: GetUserRepoUseCase
    private val testUsername = "testuser"

    @Before
    fun setup() {
        getUserRepoUseCase = mockk()
        pagingSource = UserRepoPagingSource(getUserRepoUseCase, testUsername)
    }

    @Test
    fun `load returns Page when useCase returns success with data`() = runTest {
        // Given
        val pageSize = 20
        val pageIndex = 1
        val repos = listOf(
            GithubRepo(
                name = "repo1",
                description = "Test repository 1",
                language = "Kotlin",
                stars = 100,
                htmlUrl = "https://example.com/repo1"
            ),
            GithubRepo(
                name = "repo2",
                description = "Test repository 2",
                language = "Java",
                stars = 50,
                htmlUrl = "https://example.com/repo2"
            )
        )

        coEvery {
            getUserRepoUseCase(GetUserRepoParam(username = testUsername, limit = pageSize, page = pageIndex))
        } returns Result.Success(repos)

        // When
        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = pageIndex,
                loadSize = pageSize,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(loadResult is PagingSource.LoadResult.Page)
        loadResult as PagingSource.LoadResult.Page
        assertEquals(repos, loadResult.data)
        assertEquals(pageIndex + 1, loadResult.nextKey)
        assertEquals(null, loadResult.prevKey)
    }

    @Test
    fun `load returns Page with empty list when useCase returns success with empty data`() = runTest {
        // Given
        val pageSize = 20
        val pageIndex = 1
        val emptyRepos = emptyList<GithubRepo>()

        coEvery {
            getUserRepoUseCase(GetUserRepoParam(username = testUsername, limit = pageSize, page = pageIndex))
        } returns Result.Success(emptyRepos)

        // When
        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = pageIndex,
                loadSize = pageSize,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(loadResult is PagingSource.LoadResult.Page)
        loadResult as PagingSource.LoadResult.Page
        assertEquals(emptyRepos, loadResult.data)
        assertEquals(null, loadResult.nextKey) // No next page when empty
        assertEquals(null, loadResult.prevKey)
    }

    @Test
    fun `load returns Error when useCase returns error`() = runTest {
        // Given
        val pageSize = 20
        val pageIndex = 1
        val error = GetUserReposError.ExceedRateLimit

        coEvery {
            getUserRepoUseCase(GetUserRepoParam(username = testUsername, limit = pageSize, page = pageIndex))
        } returns Result.Error(error)

        // When
        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = pageIndex,
                loadSize = pageSize,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(loadResult is PagingSource.LoadResult.Error)
        loadResult as PagingSource.LoadResult.Error
        assertTrue(loadResult.throwable is RepoPagingFailure)
        assertEquals(error, (loadResult.throwable as RepoPagingFailure).failure)
    }

    @Test
    fun `load starts from page 1 when key is null`() = runTest {
        // Given
        val pageSize = 20
        val defaultPageIndex = 1
        val repos = listOf(
            GithubRepo(
                name = "repo1",
                description = "Test repository 1",
                language = "Kotlin",
                stars = 100,
                htmlUrl = "https://example.com/repo1"
            )
        )

        coEvery {
            getUserRepoUseCase(GetUserRepoParam(username = testUsername, limit = pageSize, page = defaultPageIndex))
        } returns Result.Success(repos)

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
        assertEquals(repos, loadResult.data)
        assertEquals(defaultPageIndex + 1, loadResult.nextKey)
        assertEquals(null, loadResult.prevKey)
    }
}
