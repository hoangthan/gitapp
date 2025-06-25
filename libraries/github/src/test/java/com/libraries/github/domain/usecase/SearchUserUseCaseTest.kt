package com.libraries.github.domain.usecase

import com.git.data.core.model.Result
import com.libraries.github.domain.model.GithubUser
import com.libraries.github.domain.repository.GithubRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchUserUseCaseTest {

    private lateinit var repository: GithubRepository
    private lateinit var useCase: SearchUserUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = SearchUserUseCaseImpl(repository)
    }

    @Test
    fun `invoke with empty keyword returns empty list`() = runTest {
        // Given
        val param = SearchUserParam(keyword = "", limit = 10, page = 1)

        // When
        val result = useCase(param)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(emptyList<GithubUser>(), result.getOrNull)
    }

    @Test
    fun `invoke with valid keyword returns success result`() = runTest {
        // Given
        val param = SearchUserParam(keyword = "test", limit = 10, page = 1)
        val users = listOf(GithubUser(username = "testuser", avatarUrl = "https://example.com/avatar.jpg"))
        coEvery { repository.searchUsers(param.keyword, param.limit, param.page) } returns Result.Success(users)

        // When
        val result = useCase(param)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(users, result.getOrNull)
    }

    @Test
    fun `invoke with valid keyword but repository returns error`() = runTest {
        // Given
        val param = SearchUserParam(keyword = "test", limit = 10, page = 1)
        coEvery { repository.searchUsers(param.keyword, param.limit, param.page) } returns Result.Error(SearchUserError.RateLimitExceeded)

        // When
        val result = useCase(param)

        // Then
        assertTrue(result.isError)
        assertEquals(SearchUserError.RateLimitExceeded, result.failureOrNull)
    }
}
