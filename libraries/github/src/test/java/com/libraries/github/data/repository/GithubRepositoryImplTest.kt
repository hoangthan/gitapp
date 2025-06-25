package com.libraries.github.data.repository

import com.git.data.core.model.FailureException
import com.git.data.core.model.FailureException.UnknownException
import com.libraries.github.data.datasource.remote.GithubApiService
import com.libraries.github.data.datasource.remote.dto.GithubRepoDto
import com.libraries.github.data.datasource.remote.dto.GithubUserDetailDto
import com.libraries.github.data.datasource.remote.dto.GithubUserDto
import com.libraries.github.data.datasource.remote.dto.SearchUserResponseDto
import com.libraries.github.domain.model.GithubRepo
import com.libraries.github.domain.model.GithubUser
import com.libraries.github.domain.model.GithubUserDetail
import com.libraries.github.domain.usecase.GetUserDetailError
import com.libraries.github.domain.usecase.GetUserReposError
import com.libraries.github.domain.usecase.SearchUserError
import com.skydoves.sandwich.ApiResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class GithubRepositoryImplTest {

    private lateinit var apiService: GithubApiService
    private lateinit var repository: GithubRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        repository = GithubRepositoryImpl(apiService)
    }

    @Test
    fun `searchUsers with successful response returns mapped domain models`() = runTest {
        // Given
        val query = "test"
        val limit = 10
        val page = 1
        val userDto =
            GithubUserDto(login = "testuser", avatarUrl = "https://google.com/avatar.jpg")
        val responseDto = SearchUserResponseDto(
            totalCount = 1,
            incompleteResults = false,
            items = listOf(userDto)
        )
        val expectedUser =
            GithubUser(username = "testuser", avatarUrl = "https://google.com/avatar.jpg")

        coEvery { apiService.searchUsers(query, limit, page) } returns ApiResponse.Success(
            responseDto
        )

        // When
        val result = repository.searchUsers(query, limit, page)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(listOf(expectedUser), result.getOrNull)
    }

    @Test
    fun `searchUsers with rate limit error returns RateLimitExceeded error`() = runTest {
        // Given
        val query = "test"
        val limit = 10
        val page = 1
        val errorResponse = Response.error<SearchUserResponseDto>(
            403,
            ResponseBody.create(null, "")
        )

        coEvery { apiService.searchUsers(query, limit, page) } returns ApiResponse.Failure.Error(
            errorResponse
        )

        // When
        val result = repository.searchUsers(query, limit, page)

        // Then
        assertTrue(result.isError)
        assertEquals(SearchUserError.RateLimitExceeded, result.failureOrNull)
    }

    @Test
    fun `searchUsers with unprocessable entity error returns InvalidQuery error`() = runTest {
        // Given
        val query = "test"
        val limit = 10
        val page = 1
        val errorResponse = Response.error<SearchUserResponseDto>(
            422,
            ResponseBody.create(null, "")
        )

        coEvery { apiService.searchUsers(query, limit, page) } returns ApiResponse.Failure.Error(
            errorResponse
        )

        // When
        val result = repository.searchUsers(query, limit, page)

        // Then
        assertTrue(result.isError)
        assertEquals(SearchUserError.InvalidQuery, result.failureOrNull)
    }

    @Test
    fun `searchUsers with exception returns FailureException`() = runTest {
        // Given
        val query = "test"
        val limit = 10
        val page = 1
        val exception = RuntimeException("Network error")

        coEvery {
            apiService.searchUsers(
                query,
                limit,
                page
            )
        } returns ApiResponse.Failure.Exception(exception)

        // When
        val result = repository.searchUsers(query, limit, page)

        // Then
        assertTrue(result.isError)
        assertEquals(FailureException.UnknownException, result.failureOrNull)
    }
    
    // Tests for getUserDetail
    
    @Test
    fun `getUserDetail with successful response returns mapped domain model`() = runTest {
        // Given
        val username = "testuser"
        val userDetailDto = GithubUserDetailDto(
            login = "testuser",
            name = "Test User",
            avatarUrl = "https://google.com/avatar.jpg",
            followers = 100,
            following = 50
        )
        val expectedUserDetail = GithubUserDetail(
            username = "testuser",
            name = "Test User",
            avatarUrl = "https://google.com/avatar.jpg",
            followers = 100,
            following = 50
        )

        coEvery { apiService.getUserDetail(username) } returns ApiResponse.Success(userDetailDto)

        // When
        val result = repository.getUserDetail(username)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedUserDetail, result.getOrNull)
    }

    @Test
    fun `getUserDetail with rate limit error returns ExceedRateLimit error`() = runTest {
        // Given
        val username = "testuser"
        val errorResponse = Response.error<GithubUserDetailDto>(
            403,
            ResponseBody.create(null, "")
        )

        coEvery { apiService.getUserDetail(username) } returns ApiResponse.Failure.Error(errorResponse)

        // When
        val result = repository.getUserDetail(username)

        // Then
        assertTrue(result.isError)
        assertEquals(GetUserDetailError.ExceedRateLimit, result.failureOrNull)
    }

    @Test
    fun `getUserDetail with not found error returns UserNotFound error`() = runTest {
        // Given
        val username = "testuser"
        val errorResponse = Response.error<GithubUserDetailDto>(
            404,
            ResponseBody.create(null, "")
        )

        coEvery { apiService.getUserDetail(username) } returns ApiResponse.Failure.Error(errorResponse)

        // When
        val result = repository.getUserDetail(username)

        // Then
        assertTrue(result.isError)
        assertEquals(GetUserDetailError.UserNotFound, result.failureOrNull)
    }

    @Test
    fun `getUserDetail with other error returns UnknownException`() = runTest {
        // Given
        val username = "testuser"
        val errorResponse = Response.error<GithubUserDetailDto>(
            500,
            ResponseBody.create(null, "")
        )

        coEvery { apiService.getUserDetail(username) } returns ApiResponse.Failure.Error(errorResponse)

        // When
        val result = repository.getUserDetail(username)

        // Then
        assertTrue(result.isError)
        assertEquals(UnknownException, result.failureOrNull)
    }

    @Test
    fun `getUserDetail with exception returns FailureException`() = runTest {
        // Given
        val username = "testuser"
        val exception = RuntimeException("Network error")

        coEvery { apiService.getUserDetail(username) } returns ApiResponse.Failure.Exception(exception)

        // When
        val result = repository.getUserDetail(username)

        // Then
        assertTrue(result.isError)
        assertEquals(FailureException.UnknownException, result.failureOrNull)
    }
    
    // Tests for getUserRepos
    
    @Test
    fun `getUserRepos with successful response returns mapped domain models`() = runTest {
        // Given
        val username = "testuser"
        val limit = 10
        val page = 1
        val repoDto = GithubRepoDto(
            name = "test-repo",
            description = "A test repository",
            language = "Kotlin",
            stargazersCount = 42,
            fork = false,
            htmlUrl = "https://google.com/testuser/test-repo"
        )
        val expectedRepo = GithubRepo(
            name = "test-repo",
            description = "A test repository",
            language = "Kotlin",
            stars = 42,
            htmlUrl = "https://google.com/testuser/test-repo"
        )

        coEvery { apiService.getUserRepos(username, limit, page, fork = false) } returns ApiResponse.Success(listOf(repoDto))

        // When
        val result = repository.getUserRepos(username, false, limit, page)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(listOf(expectedRepo), result.getOrNull)
    }

    @Test
    fun `getUserRepos with rate limit error returns ExceedRateLimit error`() = runTest {
        // Given
        val username = "testuser"
        val limit = 10
        val page = 1
        val errorResponse = Response.error<List<GithubRepoDto>>(
            403,
            ResponseBody.create(null, "")
        )

        coEvery { apiService.getUserRepos(username, limit, page, fork = false) } returns ApiResponse.Failure.Error(errorResponse)

        // When
        val result = repository.getUserRepos(username, false, limit, page)

        // Then
        assertTrue(result.isError)
        assertEquals(GetUserReposError.ExceedRateLimit, result.failureOrNull)
    }

    @Test
    fun `getUserRepos with not found error returns UserNotFound error`() = runTest {
        // Given
        val username = "testuser"
        val limit = 10
        val page = 1
        val errorResponse = Response.error<List<GithubRepoDto>>(
            404,
            ResponseBody.create(null, "")
        )

        coEvery { apiService.getUserRepos(username, limit, page, fork = false) } returns ApiResponse.Failure.Error(errorResponse)

        // When
        val result = repository.getUserRepos(username, false, limit, page)

        // Then
        assertTrue(result.isError)
        assertEquals(GetUserReposError.UserNotFound, result.failureOrNull)
    }

    @Test
    fun `getUserRepos with other error returns UnknownException`() = runTest {
        // Given
        val username = "testuser"
        val limit = 10
        val page = 1
        val errorResponse = Response.error<List<GithubRepoDto>>(
            500,
            ResponseBody.create(null, "")
        )

        coEvery { apiService.getUserRepos(username, limit, page, fork = false) } returns ApiResponse.Failure.Error(errorResponse)

        // When
        val result = repository.getUserRepos(username, false, limit, page)

        // Then
        assertTrue(result.isError)
        assertEquals(UnknownException, result.failureOrNull)
    }

    @Test
    fun `getUserRepos with exception returns FailureException`() = runTest {
        // Given
        val username = "testuser"
        val limit = 10
        val page = 1
        val exception = RuntimeException("Network error")

        coEvery { apiService.getUserRepos(username, limit, page, fork = false) } returns ApiResponse.Failure.Exception(exception)

        // When
        val result = repository.getUserRepos(username, false, limit, page)

        // Then
        assertTrue(result.isError)
        assertEquals(FailureException.UnknownException, result.failureOrNull)
    }
}
