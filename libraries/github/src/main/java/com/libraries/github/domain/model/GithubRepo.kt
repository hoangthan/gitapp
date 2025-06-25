package com.libraries.github.domain.model

data class GithubRepo(
    val name: String,
    val description: String?,
    val language: String?,
    val stars: Int,
    val htmlUrl: String
)
