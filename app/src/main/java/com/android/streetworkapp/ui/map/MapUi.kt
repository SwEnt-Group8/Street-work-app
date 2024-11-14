package com.android.streetworkapp.ui.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Screen
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * The MapScreen composable displays a Google Map with markers for nearby parks.
 *
 * @param parkLocationViewModel The view model for park locations.
 * @param navigationActions The navigation actions to navigate to other screens.
 */
@Composable
fun MapScreen(
    parkLocationViewModel: ParkLocationViewModel,
    parkViewModel: ParkViewModel,
    navigationActions: NavigationActions,
    callbackOnMapLoaded: () -> Unit = {},
    innerPaddingValues: PaddingValues = PaddingValues(0.dp)
) {

  // hardcoded initial location values are used instead of the user's current location for now
  val initialLatLng = LatLng(46.518659400000004, 6.566561505148001)

  LaunchedEffect(initialLatLng) {
    parkLocationViewModel.findNearbyParks(initialLatLng.latitude, initialLatLng.longitude)
  }

  val parks = parkLocationViewModel.parks.collectAsState().value

  Box(modifier = Modifier.testTag("mapScreen")) {
    // Create a CameraPositionState to control the camera position
    val cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.Builder().target(initialLatLng).zoom(12f).build()
    }
    var markerIndex = 0
    // Display the Google Map
    GoogleMap(
        onMapLoaded = callbackOnMapLoaded,
        modifier = Modifier.fillMaxSize().padding(innerPaddingValues).testTag("googleMap"),
        cameraPositionState = cameraPositionState) {
          parks.forEach { park ->
            ++markerIndex
            Marker(
                contentDescription = "Marker$markerIndex",
                state = MarkerState(position = LatLng(park.lat, park.lon)),
                onClick = {
                  parkViewModel.getOrCreateParkByLocation(park)
                  parkViewModel.setParkLocation(park)
                  navigationActions.navigateTo(Screen.PARK_OVERVIEW)
                  true
                })
          }
        }
  }
}
