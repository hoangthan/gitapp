package com.libraries.github.data.datasource.remote.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SearchUserResponseDto(
    @Json(name = "total_count") val totalCount: Int,
    @Json(name = "incomplete_results") val incompleteResults: Boolean,
    @Json(name = "items") val items: List<GithubUserDto>
)
