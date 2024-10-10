package com.android.streetworkapp.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.streetworkapp.model.parks.ParkLocationViewModel
import com.android.streetworkapp.ui.navigation.BottomNavigationMenu
import com.android.streetworkapp.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.ui.navigation.TopLevelDestinations
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(parkLocationViewModel: ParkLocationViewModel, navigationActions: NavigationActions) {

  val initialLatLng = LatLng(46.518659400000004, 6.566561505148001)

  val parks = parkLocationViewModel.parks.collectAsState().value

  parkLocationViewModel.findNearbyParks(initialLatLng.latitude, initialLatLng.longitude)

  Scaffold(
      modifier = Modifier.testTag("mapScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { destination -> navigationActions.navigateTo(destination) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = TopLevelDestinations.MAP.textId)
      }) { innerPadding ->

        // Create a CameraPositionState to control the camera position
        val cameraPositionState = rememberCameraPositionState {
          position = CameraPosition.Builder().target(initialLatLng).zoom(12f).build()
        }

        // Display the Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(innerPadding).testTag("googleMap"),
            cameraPositionState = cameraPositionState,
        ) {
          parks.forEach { park ->
            Marker(
                state = MarkerState(position = LatLng(park.lat, park.lon)),
                title = park.id,
                onClick = {
                  navigationActions.navigateTo(Screen.UNK)
                  true
                })
          }
        }
      }
}
