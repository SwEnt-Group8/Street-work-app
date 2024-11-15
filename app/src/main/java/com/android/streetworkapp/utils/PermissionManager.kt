package com.android.streetworkapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

class PermissionManager(private val context: Context) {
  fun hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED
  }

  fun requestLocationPermission(launcher: ActivityResultLauncher<String>) {
    launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
  }
}
