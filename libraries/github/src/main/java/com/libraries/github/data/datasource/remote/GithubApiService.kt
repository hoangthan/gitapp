package com.libraries.github.data.datasource.remote

import com.libraries.github.data.datasource.remote.dto.GithubRepoDto
import com.libraries.github.data.datasource.remote.dto.GithubUserDetailDto
import com.libraries.github.data.datasource.remote.dto.SearchUserResponseDto
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface GithubApiService {

    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int,
    ): ApiResponse<SearchUserResponseDto>

    @GET("users/{username}")
    suspend fun getUserDetail(
        @Path("username") username: String
    ): ApiResponse<GithubUserDetailDto>

    @GET("users/{username}/repos")
    suspend fun getUserRepos(
        @Path("username") username: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int,
        @Query("fork") fork: Boolean = false
    ): ApiResponse<List<GithubRepoDto>>
}
