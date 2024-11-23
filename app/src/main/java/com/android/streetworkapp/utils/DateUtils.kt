package com.android.streetworkapp.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
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
 * Convert an epoch timestamp (Long) to a formatted string of format "dd/MM/yyyy HH:mm".
 *
 * @return The formatted string.
 */
fun Long.toFormattedString(): String {
  val pattern = "dd/MM/yyyy HH:mm"
  val sdf = SimpleDateFormat(pattern, Locale.getDefault())
  sdf.timeZone = TimeZone.getTimeZone("UTC")
  return sdf.format(this)
}

/**
 * Convert a [String] of format "dd/MM/yyyy HH:mm" to a long.
 *
 * @return The epoch timestamp.
 */
fun String.toEpochTimestamp(): Long {
  val pattern = "dd/MM/yyyy HH:mm"
  val sdf = SimpleDateFormat(pattern, Locale.getDefault())
  sdf.timeZone = TimeZone.getTimeZone("UTC")
  return sdf.parse(this)?.time ?: throw IllegalArgumentException("Invalid date format")
}
