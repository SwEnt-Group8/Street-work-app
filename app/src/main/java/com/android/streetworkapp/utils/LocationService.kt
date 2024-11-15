package com.android.streetworkapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class LocationService(private val context: Context) {

  // Define the FusedLocationProviderClient to get location
  private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

  fun startLocationUpdates(callback: LocationCallback) {

    // Define the location request process
    val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000) // update every 10s
            .setMinUpdateIntervalMillis(5000) // Minimum update interval (can't be faster)
            .build()
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
        PackageManager.PERMISSION_GRANTED) {
      fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
    }
  }

  fun stopLocationUpdates(callback: LocationCallback) {
    fusedLocationClient.removeLocationUpdates(callback)
  }
}
