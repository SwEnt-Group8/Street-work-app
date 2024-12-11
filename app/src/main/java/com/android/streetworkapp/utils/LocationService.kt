package com.android.streetworkapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.core.app.ActivityCompat
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.android.streetworkapp.model.progression.ScoreIncrease
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.progress.updateAndDisplayPoints
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope

class LocationService(
    private val context: Context,
    private val userViewModel: UserViewModel,
    private val navigationActions: NavigationActions,
    private val scope: CoroutineScope,
    private val host: SnackbarHostState?= null
) {
  // Define the distance for when is in a park
  private val insidePark = 30

  // Define the FusedLocationProviderClient to get location
  private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

  fun startLocationUpdates(callback: LocationCallback) {

    // Define the location request process
    val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000) // update every 10s
            .setMinUpdateIntervalMillis(5000) // Minimum update interval (can't be faster)
            .build()
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED) {
      Log.d("Localisation", "activated location update actual")
      fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
    }
  }

  fun stopLocationUpdates(callback: LocationCallback) {
    fusedLocationClient.removeLocationUpdates(callback)
  }

  fun rewardParkDiscovery(user: User?, userLocation: LatLng, parksLocation: List<ParkLocation>) {

    val results = FloatArray(1)
    Log.d("Localisation", "check all park distance")
    for (park in parksLocation) {
      // Calculate the distance between userLocation and park location
      Location.distanceBetween(
          userLocation.latitude, userLocation.longitude, park.lat, park.lon, results)

      val distance = results[0]
      if (distance < insidePark) {
        Log.d("Localisation", "found close park")
        user?.let {
          // add new park in user list
          userViewModel.getParksByUid(it.uid)
          val currentParks = user.parks
          if (!currentParks.contains(park.id)) {
            Log.d("Localisation", "park is new")
            userViewModel.addNewPark(it.uid, park.id)

            // add point if first time
            if (host != null) {
              updateAndDisplayPoints(
                  userViewModel, navigationActions, ScoreIncrease.FIND_NEW_PARK.points, scope, host)
            }
          }
        }
      }
    }
  }
}
