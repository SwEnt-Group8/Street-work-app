package com.android.streetworkapp.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.ui.navigation.BottomNavigationMenu
import com.android.streetworkapp.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.streetworkapp.ui.navigation.NavigationActions
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
fun MapScreen(parkLocationViewModel: ParkLocationViewModel, navigationActions: NavigationActions) {

  // hardcoded initial location values are used instead of the user's current location for now
  val initialLatLng = LatLng(46.518659400000004, 6.566561505148001)

  LaunchedEffect(initialLatLng) {
    parkLocationViewModel.findNearbyParks(initialLatLng.latitude, initialLatLng.longitude)
  }

  val parks = parkLocationViewModel.parks.collectAsState().value

  Scaffold(
      modifier = Modifier.testTag("mapScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATION)
      }) { innerPadding ->

        // Create a CameraPositionState to control the camera position
        val cameraPositionState = rememberCameraPositionState {
          position = CameraPosition.Builder().target(initialLatLng).zoom(12f).build()
        }

        // Display the Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(innerPadding).testTag("googleMap"),
            cameraPositionState = cameraPositionState) {
              parks.forEach { park ->
                Marker(
                    contentDescription = "Marker",
                    state = MarkerState(position = LatLng(park.lat, park.lon)),
                    /** onClick = { navigationActions.navigateTo(Screen.UNK) true } */
                )
              }
            }
      }
}
