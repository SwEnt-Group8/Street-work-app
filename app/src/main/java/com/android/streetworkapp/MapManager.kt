package com.android.streetworkapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.content.ContextCompat

@Composable
fun MapManager(requestPermissionLauncher: ActivityResultLauncher<String>) {
  val context = LocalContext.current
  val hasLocationPermission by remember {
    mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED)
  }

  // Request location permission if not granted
  LaunchedEffect(hasLocationPermission) {
    if (!hasLocationPermission) {
      requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
  }
  // Could be remove depends on the map implementation
  Button(
      onClick = { requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
      modifier = Modifier.testTag("requestPermissionButton")) {
        Text(text = "Test Permission")
      }
}