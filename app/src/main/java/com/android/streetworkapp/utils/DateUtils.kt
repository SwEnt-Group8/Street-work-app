package com.android.streetworkapp.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Convert a [Timestamp] to a formatted string of format "dd/MM/yyyy HH:mm".
 *
 * @return The formatted string.
 */
fun Timestamp.toFormattedString(): String {
  val pattern = "dd/MM/yyyy HH:mm"
  val sdf = SimpleDateFormat(pattern, Locale.getDefault())
  return sdf.format(this.toDate())
}
