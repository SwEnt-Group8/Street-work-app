package com.android.streetworkapp.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
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
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

@Composable
fun MapManager(
    userLocation: MutableState<LatLng>,
    onUserLocationChange: (MutableState<LatLng>) -> Unit
) {
  val context = LocalContext.current

  // State whether the user has granted location permission
  val hasLocationPermission by remember {
    mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED)
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
      Log.d("Map", "requestPermissionLauncher.launch")
      requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
  }

  // Define the FusedLocationProviderClient to get location
  val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

  // Define the location request process
  val locationRequest =
      LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000) // update every 10s
          .setMinUpdateIntervalMillis(5000) // Minimum update interval (can't be faster)
          .build()

  // update userLocation each uppdate
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
      try {
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper())
      } catch (e: SecurityException) {
        Log.e("Map", "Location permission revoked.")
      }
    }
  }

  // Stop location updates
  DisposableEffect(Unit) {
    onDispose { fusedLocationClient.removeLocationUpdates(locationCallback) }
  }
}
