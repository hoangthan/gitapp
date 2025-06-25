package com.libraries.github.domain.usecase

import com.git.data.core.model.Result
import com.libraries.github.domain.model.GithubRepo
import com.libraries.github.domain.repository.GithubRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetUserRepoUseCaseTest {

    private lateinit var repository: GithubRepository
    private lateinit var useCase: GetUserRepoUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetUserRepoUseCaseImpl(repository)
    }

    @Test
    fun `invoke with empty username returns UserNotFound error`() = runTest {
        // Given
        val param = GetUserRepoParam(username = "", limit = 10, page = 1)

        // When
        val result = useCase(param)

        // Then
        assertTrue(result.isError)
        assertEquals(GetUserReposError.UserNotFound, result.failureOrNull)
    }

    @Test
    fun `invoke with valid username returns success result`() = runTest {
        // Given
        val param = GetUserRepoParam(username = "testuser", limit = 10, page = 1)
        val repos = listOf(
            GithubRepo(
                name = "test-repo",
                description = "A test repository",
                language = "Kotlin",
                stars = 42,
                htmlUrl = "https://google.com/testuser/test-repo"
            )
        )
        coEvery { repository.getUserRepos(param.username, false, param.limit, param.page) } returns Result.Success(repos)

        // When
        val result = useCase(param)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(repos, result.getOrNull)
    }

    @Test
    fun `invoke with valid username but repository returns error`() = runTest {
        // Given
        val param = GetUserRepoParam(username = "testuser", limit = 10, page = 1)
        coEvery { repository.getUserRepos(param.username, false, param.limit, param.page) } returns Result.Error(GetUserReposError.UserNotFound)

        // When
        val result = useCase(param)

        // Then
        assertTrue(result.isError)
        assertEquals(GetUserReposError.UserNotFound, result.failureOrNull)
    }
    
    @Test
    fun `invoke with valid username but repository returns UserNotFound error`() = runTest {
        // Given
        val param = GetUserRepoParam(username = "testuser", limit = 10, page = 1)
        coEvery { repository.getUserRepos(param.username, false, param.limit, param.page) } returns Result.Error(GetUserReposError.UserNotFound)

        // When
        val result = useCase(param)

        // Then
        assertTrue(result.isError)
        assertEquals(GetUserReposError.UserNotFound, result.failureOrNull)
    }
}