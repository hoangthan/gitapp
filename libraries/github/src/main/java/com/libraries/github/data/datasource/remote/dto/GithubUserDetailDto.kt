package com.libraries.github.data.datasource.remote.dto

import com.libraries.github.domain.model.GithubUserDetail
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GithubUserDetailDto(
    @Json(name = "login") val login: String,
    @Json(name = "name") val name: String?,
    @Json(name = "avatar_url") val avatarUrl: String,
    @Json(name = "followers") val followers: Int,
    @Json(name = "following") val following: Int
)

internal fun GithubUserDetailDto.toDomain() = GithubUserDetail(
    username = login,
    name = name,
    avatarUrl = avatarUrl,
    followers = followers,
    following = following
)
