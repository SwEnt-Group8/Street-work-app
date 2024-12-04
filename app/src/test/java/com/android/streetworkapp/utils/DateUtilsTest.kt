package com.android.streetworkapp.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DateUtilsTest {

  @Before
  fun setUp() {
    TimeZone.setDefault(
        TimeZone.getTimeZone("UTC")) // The timezone is assumed to be in UTC in those tests
  }

  @Test
  fun `toEpochTimestamp should convert valid date string to epoch timestamp`() {
    // Arrange
    val dateString = "24/11/2024 15:30"
    val expectedTimestamp =
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .apply { timeZone = TimeZone.getDefault() }
            .parse(dateString)
            ?.time ?: throw IllegalArgumentException("Invalid date for test")

    // Act
    val actualTimestamp = dateString.toEpochTimestamp()

    // Assert
    assertEquals(expectedTimestamp, actualTimestamp)
  }

  @Test
  fun `toEpochTimestamp should handle leap years correctly`() {
    // Arrange
    val leapYearDate = "29/02/2024 00:00"
    val expectedTimestamp =
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .apply { timeZone = TimeZone.getDefault() }
            .parse(leapYearDate)
            ?.time ?: throw IllegalArgumentException("Invalid date for test")

    // Act
    val actualTimestamp = leapYearDate.toEpochTimestamp()

    // Assert
    assertEquals(expectedTimestamp, actualTimestamp)
  }

  @Test
  fun `toEpochTimestamp should handle different time zones correctly`() {
    // Arrange
    val dateString = "24/11/2024 15:30"
    val sdf =
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).apply {
          timeZone = TimeZone.getDefault()
        }
    val expectedTimestamp =
        sdf.parse(dateString)?.time ?: throw IllegalArgumentException("Invalid date for test")

    // Act
    val actualTimestamp = dateString.toEpochTimestamp()

    // Assert
    assertEquals(expectedTimestamp, actualTimestamp)
  }

  @Test
  fun `toTimestamp should convert valid date string to Firebase Timestamp`() {
    // Arrange
    val dateString = "24/11/2024 15:30"
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    val expectedDate = sdf.parse(dateString)
    val expectedTimestamp = Timestamp(expectedDate!!)

    // Act
    val actualTimestamp = dateString.toTimestamp()

    // Assert
    assertEquals(expectedTimestamp, actualTimestamp)
  }

  @Test
  fun `toTimestamp should handle leap years correctly`() {
    // Arrange
    val leapYearDate = "29/02/2024 00:00"
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    val expectedDate = sdf.parse(leapYearDate)
    val expectedTimestamp = Timestamp(expectedDate!!)

    // Act
    val actualTimestamp = leapYearDate.toTimestamp()

    // Assert
    assertEquals(expectedTimestamp, actualTimestamp)
  }

  @Test
  fun `toTimestamp should handle different time zones correctly`() {
    // Arrange
    val dateString = "24/11/2024 15:30"
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    val expectedDate = sdf.parse(dateString)
    val expectedTimestamp = Timestamp(expectedDate!!)

    // Act
    val actualTimestamp = dateString.toTimestamp()

    // Assert
    assertEquals(expectedTimestamp, actualTimestamp)
  }
}
