package com.libraries.github.domain.usecase

import com.git.data.core.model.Result
import com.libraries.github.domain.model.GithubUserDetail
import com.libraries.github.domain.repository.GithubRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetUserDetailsUseCaseTest {

    private lateinit var repository: GithubRepository
    private lateinit var useCase: GetUserDetailsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetUserDetailsUseCaseImpl(repository)
    }

    @Test
    fun `invoke with empty username returns UserNotFound error without calling repository`() = runTest {
        // Given
        val param = GetUserDetailParam(username = "")

        // When
        val result = useCase(param)

        // Then
        assertTrue(result.isError)
        assertEquals(GetUserDetailError.UserNotFound, result.failureOrNull)
        
        // Verify repository was not called
        coVerify(exactly = 0) { repository.getUserDetail(any()) }
    }

    @Test
    fun `invoke with valid username returns success result`() = runTest {
        // Given
        val param = GetUserDetailParam(username = "testuser")
        val userDetail = GithubUserDetail(
            username = "testuser",
            name = "Test User",
            avatarUrl = "https://google.com/avatar.jpg",
            followers = 100,
            following = 50
        )
        coEvery { repository.getUserDetail(param.username) } returns Result.Success(userDetail)

        // When
        val result = useCase(param)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(userDetail, result.getOrNull)
        
        // Verify repository was called with correct parameter
        coVerify(exactly = 1) { repository.getUserDetail(param.username) }
    }

    @Test
    fun `invoke with valid username but repository returns UserNotFound error`() = runTest {
        // Given
        val param = GetUserDetailParam(username = "testuser")
        coEvery { repository.getUserDetail(param.username) } returns Result.Error(GetUserDetailError.UserNotFound)

        // When
        val result = useCase(param)

        // Then
        assertTrue(result.isError)
        assertEquals(GetUserDetailError.UserNotFound, result.failureOrNull)
        
        // Verify repository was called
        coVerify(exactly = 1) { repository.getUserDetail(param.username) }
    }
    
    @Test
    fun `invoke with valid username but repository returns ExceedRateLimit error`() = runTest {
        // Given
        val param = GetUserDetailParam(username = "testuser")
        coEvery { repository.getUserDetail(param.username) } returns Result.Error(GetUserDetailError.ExceedRateLimit)

        // When
        val result = useCase(param)

        // Then
        assertTrue(result.isError)
        assertEquals(GetUserDetailError.ExceedRateLimit, result.failureOrNull)
        
        // Verify repository was called
        coVerify(exactly = 1) { repository.getUserDetail(param.username) }
    }
    
    @Test
    fun `invoke with username containing special characters passes through to repository`() = runTest {
        // Given
        val param = GetUserDetailParam(username = "test-user_123")
        val userDetail = GithubUserDetail(
            username = "test-user_123",
            name = "Test User",
            avatarUrl = "https://google.com/avatar.jpg",
            followers = 100,
            following = 50
        )
        coEvery { repository.getUserDetail(param.username) } returns Result.Success(userDetail)

        // When
        val result = useCase(param)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(userDetail, result.getOrNull)
        
        // Verify repository was called with correct parameter
        coVerify(exactly = 1) { repository.getUserDetail("test-user_123") }
    }
}