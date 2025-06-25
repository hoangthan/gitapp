package com.libraries.github.data.datasource.remote.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SearchUserResponseDtoTest {

    @Test
    fun `SearchUserResponseDto properly holds response data`() {
        // Given
        val userDto1 = GithubUserDto(
            login = "username",
            avatarUrl = "https://google.com/username.png"
        )
        val userDto2 = GithubUserDto(
            login = "secondUsername",
            avatarUrl = "https://google.com/secondUsername.png"
        )
        val items = listOf(userDto1, userDto2)
        
        // When
        val responseDto = SearchUserResponseDto(
            totalCount = 2,
            incompleteResults = false,
            items = items
        )

        // Then
        assertEquals(2, responseDto.totalCount)
        assertFalse(responseDto.incompleteResults)
        assertEquals(2, responseDto.items.size)
        assertEquals("username", responseDto.items[0].login)
        assertEquals("https://google.com/username.png", responseDto.items[0].avatarUrl)
        assertEquals("secondUsername", responseDto.items[1].login)
        assertEquals("https://google.com/secondUsername.png", responseDto.items[1].avatarUrl)
    }

    @Test
    fun `SearchUserResponseDto items can be mapped to domain models`() {
        // Given
        val userDto1 = GithubUserDto(
            login = "username",
            avatarUrl = "https://google.com/username.png"
        )
        val userDto2 = GithubUserDto(
            login = "secondUsername",
            avatarUrl = "https://google.com/secondUsername.png"
        )
        val items = listOf(userDto1, userDto2)
        
        val responseDto = SearchUserResponseDto(
            totalCount = 2,
            incompleteResults = false,
            items = items
        )

        // When
        val domainUsers = responseDto.items.map { it.toDomain() }

        // Then
        assertEquals(2, domainUsers.size)
        assertEquals("username", domainUsers[0].username)
        assertEquals("https://google.com/username.png", domainUsers[0].avatarUrl)
        assertEquals("secondUsername", domainUsers[1].username)
        assertEquals("https://google.com/secondUsername.png", domainUsers[1].avatarUrl)
    }
}