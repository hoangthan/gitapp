package com.libraries.github.data.datasource.remote.dto

import com.libraries.github.domain.model.GithubRepo
import org.junit.Assert.assertEquals
import org.junit.Test

class GithubRepoDtoTest {

    @Test
    fun `toDomain maps DTO fields to domain model correctly with non-null optional fields`() {
        // Given
        val dto = GithubRepoDto(
            name = "username",
            description = "GitHub API Client",
            language = "Kotlin",
            stargazersCount = 1500,
            fork = false,
            htmlUrl = "https://google.com"
        )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals(GithubRepo::class.java, domain::class.java)
        assertEquals("username", domain.name)
        assertEquals("GitHub API Client", domain.description)
        assertEquals("Kotlin", domain.language)
        assertEquals(1500, domain.stars)
        assertEquals("https://google.com", domain.htmlUrl)
    }

    @Test
    fun `toDomain maps DTO fields to domain model correctly with null optional fields`() {
        // Given
        val dto = GithubRepoDto(
            name = "username",
            description = null,
            language = null,
            stargazersCount = 1500,
            fork = false,
            htmlUrl = "https://google.com"
        )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals(GithubRepo::class.java, domain::class.java)
        assertEquals("username", domain.name)
        assertEquals(null, domain.description)
        assertEquals(null, domain.language)
        assertEquals(1500, domain.stars)
        assertEquals("https://google.com", domain.htmlUrl)
    }
}
