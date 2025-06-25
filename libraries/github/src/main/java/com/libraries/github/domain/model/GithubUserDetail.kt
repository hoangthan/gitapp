package com.libraries.github.domain.model

data class GithubUserDetail(
    val username: String,
    val name: String?,
    val avatarUrl: String,
    val followers: Int,
    val following: Int
)
