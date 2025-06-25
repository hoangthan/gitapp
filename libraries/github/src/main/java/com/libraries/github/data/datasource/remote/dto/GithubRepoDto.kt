package com.libraries.github.data.datasource.remote.dto

import com.libraries.github.domain.model.GithubRepo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GithubRepoDto(
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String?,
    @Json(name = "language") val language: String?,
    @Json(name = "stargazers_count") val stargazersCount: Int,
    @Json(name = "fork") val fork: Boolean,
    @Json(name = "html_url") val htmlUrl: String
)

internal fun GithubRepoDto.toDomain() = GithubRepo(
    name = name,
    description = description,
    language = language,
    stars = stargazersCount,
    htmlUrl = htmlUrl
)
