package com.android.streetworkapp.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun Map(requestPermissionLauncher: ActivityResultLauncher<String>) {
  val context = LocalContext.current
  val hasLocationPermission by remember {
    mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED)
  }

  // Request location permission if not granted
  LaunchedEffect(hasLocationPermission) {
    if (!hasLocationPermission) {
      Log.d("Map", "Requesting permission: permission.ACCESS_FINE_LOCATION")
      requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
  }
}
