package com.libraries.github.data.datasource.remote.dto

import com.libraries.github.domain.model.GithubUserDetail
import org.junit.Assert.assertEquals
import org.junit.Test

class GithubUserDetailDtoTest {

    @Test
    fun `toDomain maps DTO fields to domain model correctly with non-null name`() {
        // Given
        val dto = GithubUserDetailDto(
            login = "username",
            name = "The Username",
            avatarUrl = "https://google.com/username.png",
            followers = 1000,
            following = 5
        )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals(GithubUserDetail::class.java, domain::class.java)
        assertEquals("username", domain.username)
        assertEquals("The Username", domain.name)
        assertEquals("https://google.com/username.png", domain.avatarUrl)
        assertEquals(1000, domain.followers)
        assertEquals(5, domain.following)
    }

    @Test
    fun `toDomain maps DTO fields to domain model correctly with null name`() {
        // Given
        val dto = GithubUserDetailDto(
            login = "username",
            name = null,
            avatarUrl = "https://google.com/username.png",
            followers = 1000,
            following = 5
        )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals(GithubUserDetail::class.java, domain::class.java)
        assertEquals("username", domain.username)
        assertEquals(null, domain.name)
        assertEquals("https://google.com/username.png", domain.avatarUrl)
        assertEquals(1000, domain.followers)
        assertEquals(5, domain.following)
    }
}