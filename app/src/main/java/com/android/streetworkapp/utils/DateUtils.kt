package com.android.streetworkapp.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

fun Timestamp.toFormattedString(): String {
  val pattern = "dd/MM/yyyy HH:mm"
  val sdf = SimpleDateFormat(pattern, Locale.getDefault())
  return sdf.format(this.toDate())
}
