package com.android.streetworkapp.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

/**
 * Convert a [Timestamp] to a formatted string of format "dd/MM/yyyy HH:mm".
 *
 * @return The formatted string.
 */
fun Timestamp.toFormattedString(): String {
  val pattern = "dd/MM/yyyy HH:mm"
  val sdf = SimpleDateFormat(pattern, Locale.getDefault())
  sdf.timeZone = TimeZone.getTimeZone("UTC") // Set to UTC to avoid timezone issues
  return sdf.format(this.toDate())
}

/**
 * Convert a [String] of format "dd/MM/yyyy HH:mm" to a [Timestamp] object.
 *
 * @return The [Timestamp] object.
 */
fun String.toTimestamp(): Timestamp {
  val pattern = "dd/MM/yyyy HH:mm"
  val sdf = SimpleDateFormat(pattern, Locale.getDefault())
  sdf.timeZone = TimeZone.getTimeZone("UTC") // Set to UTC to avoid timezone issues
  val date = sdf.parse(this)
  return Timestamp(date!!)
}

/**
 * Calculate the difference between the current time and a given timestamp.
 *
 * @param endTimestamp The end timestamp as a string.
 * @return The difference in days or hours.
 */
fun dateDifference(endTimestamp: String): String {
  val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

  val end = LocalDateTime.parse(endTimestamp, formatter)

  val duration = Duration.between(LocalDateTime.now(), end)

  val days = duration.toDays()

  val hours = duration.toHours() % 24

  return if (days > 0) {
    "in $days day(s)"
  } else {
    "in $hours hour(s)"
  }
}
