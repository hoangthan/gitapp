package com.git.data.core.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ExtensionTest {

    @Test
    fun `toFormattedDate formats timestamp correctly`() {
        // Given - May 1, 2025 at 14:26:24
        val calendar = Calendar.getInstance()
        calendar.set(2025, Calendar.MAY, 1, 14, 26, 24)
        calendar.set(Calendar.MILLISECOND, 0)
        val timestamp = calendar.timeInMillis

        val expectedFormat = "yyyy/MM/dd HH:mm"
        val sdf = SimpleDateFormat(expectedFormat, Locale.getDefault())
        val expected = sdf.format(Date(timestamp))

        // When
        val result = timestamp.toFormattedDate()

        // Then
        assertEquals(expected, result)
        assertEquals("2025/05/01 14:26", result)
    }

    @Test
    fun `toFormattedDate handles zero timestamp`() {
        // Given
        val timestamp = 0L  // 1970/01/01 00:00:00 UTC
        val expectedFormat = "yyyy/MM/dd HH:mm"
        val sdf = SimpleDateFormat(expectedFormat, Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        val expected = sdf.format(Date(timestamp))

        // When
        val result = timestamp.toFormattedDate()

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun `toFormattedDate handles current time`() {
        // Given - Use today's date (May 1, 2025)
        val calendar = Calendar.getInstance()
        calendar.set(2025, Calendar.MAY, 1)
        // Keep the current time of day
        val timestamp = calendar.timeInMillis

        val expectedFormat = "yyyy/MM/dd HH:mm"
        val sdf = SimpleDateFormat(expectedFormat, Locale.getDefault())
        val expected = sdf.format(Date(timestamp))

        // When
        val result = timestamp.toFormattedDate()

        // Then
        assertEquals(expected, result)
        // The date part should be 2025/05/01 regardless of the time
        assert(result.startsWith("2025/05/01"))
    }
}
