package com.libraries.github.data.datasource.remote.dto

import com.libraries.github.domain.model.GithubUser
import org.junit.Assert.assertEquals
import org.junit.Test

class GithubUserDtoTest {

    @Test
    fun `toDomain maps DTO fields to domain model correctly`() {
        // Given
        val dto = GithubUserDto(
            login = "username",
            avatarUrl = "https://google.com/image.png"
        )

        // When
        val domain = dto.toDomain()

        // Then
        assertEquals(GithubUser::class.java, domain::class.java)
        assertEquals("username", domain.username)
        assertEquals("https://google.com/image.png", domain.avatarUrl)
    }
}
