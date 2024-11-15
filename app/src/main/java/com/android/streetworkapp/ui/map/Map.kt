package com.android.streetworkapp.ui.map

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.android.streetworkapp.utils.LocationService
import com.android.streetworkapp.utils.PermissionManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng

@Composable
fun MapManager(
    userLocation: MutableState<LatLng>,
    onUserLocationChange: (MutableState<LatLng>) -> Unit,
    permissionManager: PermissionManager,
    locationService: LocationService
) {
  val context = LocalContext.current

  val hasLocationPermission by remember {
    mutableStateOf(permissionManager.hasLocationPermission())
  }

  // Request permission launcher
  val requestPermissionLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.RequestPermission(),
          onResult = { isGranted ->
            if (isGranted) {
              // Permission granted, fetch user location
              Log.d("Map", "Location permission granted")
              Toast.makeText(context, "Permission granted.", Toast.LENGTH_SHORT).show()
            } else {
              // Permission denied
              Log.d("Map", "Location permission denied")
              Toast.makeText(context, "Location permission denied.", Toast.LENGTH_SHORT).show()
            }
          })

  // Request location permission if not granted
  LaunchedEffect(hasLocationPermission) {
    if (!hasLocationPermission) {
      permissionManager.requestLocationPermission(requestPermissionLauncher)
    }
  }

  // update userLocation each update
  val locationCallback = remember {
    object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult) {
        locationResult.lastLocation?.let { location ->
          userLocation.value = LatLng(location.latitude, location.longitude)
          onUserLocationChange(userLocation)
        }
      }
    }
  }

  // Start location updates when it have permission
  LaunchedEffect(hasLocationPermission) {
    if (hasLocationPermission) {
      locationService.startLocationUpdates(locationCallback)
    }
  }
  // Stop location updates
  DisposableEffect(Unit) { onDispose { locationService.stopLocationUpdates(locationCallback) } }
}
