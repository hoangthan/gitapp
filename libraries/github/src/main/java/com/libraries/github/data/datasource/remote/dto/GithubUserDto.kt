package com.libraries.github.data.datasource.remote.dto

import com.libraries.github.domain.model.GithubUser
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GithubUserDto(
    @Json(name = "login") val login: String,
    @Json(name = "avatar_url") val avatarUrl: String
)

internal fun GithubUserDto.toDomain() = GithubUser(
    username = login,
    avatarUrl = avatarUrl
)
