package com.android.streetworkapp.utils

import android.content.Context
import android.widget.Toast

interface ToastNotifier {
  fun showToast(context: Context, message: String)
}

class DefaultToastNotifier(private val context: Context) : ToastNotifier {
  override fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
  }

  fun showMessage(message: String) {
    showToast(context, message)
  }
}
