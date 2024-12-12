package com.android.streetworkapp.utils

import com.android.sample.R
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventStatus
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

// Define a constant for the date-time pattern
const val DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm"

/**
 * Convert a [Timestamp] to a formatted string of format "dd/MM/yyyy HH:mm".
 *
 * @return The formatted string.
 */
fun Timestamp.toFormattedString(): String {
  val sdf = SimpleDateFormat(DATE_TIME_PATTERN, Locale.getDefault())
  sdf.timeZone = TimeZone.getDefault()
  return sdf.format(this.toDate())
}

/**
 * Convert a [String] of format "dd/MM/yyyy HH:mm" to a [Timestamp] object.
 *
 * @return The [Timestamp] object.
 */
fun String.toTimestamp(): Timestamp {
  val sdf = SimpleDateFormat(DATE_TIME_PATTERN, Locale.getDefault())
  sdf.timeZone = TimeZone.getDefault()
  val date = sdf.parse(this)
  return Timestamp(date!!)
}

/**
 * Convert an epoch timestamp (Long) to a formatted string of format "dd/MM/yyyy HH:mm".
 *
 * @return The formatted string.
 */
fun Long.toFormattedString(): String {
  val sdf = SimpleDateFormat(DATE_TIME_PATTERN, Locale.getDefault())
  sdf.timeZone = TimeZone.getDefault()
  return sdf.format(this)
}

/**
 * Convert a [String] of format "dd/MM/yyyy HH:mm" to a long.
 *
 * @return The epoch timestamp.
 */
fun String.toEpochTimestamp(): Long {
  val sdf = SimpleDateFormat(DATE_TIME_PATTERN, Locale.getDefault())
  sdf.timeZone = TimeZone.getDefault()
  return sdf.parse(this)?.time ?: throw IllegalArgumentException("Invalid date format")
}

/**
 * Calculate the difference between the current time and a given timestamp and return a text
 * according to event status
 *
 * @param event the event to calculate the difference for.
 * @return The difference in days or hours.
 */
fun dateDifference(event: Event): String {
  val formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)

  val endTimestamp = event.date.toFormattedString()

  val end = LocalDateTime.parse(endTimestamp, formatter)

  val duration = Duration.between(LocalDateTime.now(), end)

  val days = duration.toDays()

  val hours = duration.toHours() % 24

  val statusText =
      when (event.status) {
        EventStatus.STARTED -> R.string.event_started.toString()
        EventStatus.ENDED -> R.string.event_ended.toString()
        EventStatus.CREATED -> R.string.event_soon.toString()
      }

  return if (days > 0) {
    "in $days day(s)"
  } else {
    if (hours <= 0) {
      statusText
    } else {
      "in $hours hour(s)"
    }
  }
}
