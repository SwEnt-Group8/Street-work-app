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
