package com.android.streetworkapp.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.android.streetworkapp.model.parks.OverpassParkLocationRepository
import com.android.streetworkapp.model.parks.ParkLocationViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import okhttp3.OkHttpClient

@Composable
fun MapScreen(parkLocationViewModel: ParkLocationViewModel) {

    val initialLatLng = LatLng(46.518659400000004, 6.566561505148001)

    val parks = parkLocationViewModel.parks.collectAsState().value

    parkLocationViewModel.findNearbyParks(initialLatLng.latitude, initialLatLng.longitude)

    Scaffold(
        modifier = Modifier.testTag("mapScreen"),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottomNavigationMenu"), containerColor = Color.Gray
            ) {
                NavigationBarItem(
                    modifier = Modifier.testTag("parkIcon"),
                    icon = { Icon(Icons.Filled.LocationOn, contentDescription = "parks") },
                    selected = false,
                    onClick = {})

                NavigationBarItem(
                    modifier = Modifier.testTag("profileIcon"),
                    icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = "profile") },
                    selected = false,
                    onClick = {})
            }
        }) { innerPadding ->

        // Create a CameraPositionState to control the camera position
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.Builder().target(initialLatLng).zoom(12f).build()
        }

        // Display the Google Map
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("googleMap"),
            cameraPositionState = cameraPositionState,
        ) {
            parks.forEach { park ->
                Marker(state = MarkerState(position = LatLng(park.lat, park.lon)))
            }
        }
    }
}

@Preview
@Composable
fun MapScreenPreview() {
    val client = OkHttpClient()
    val parkLocationViewModel = ParkLocationViewModel(OverpassParkLocationRepository(client))
    MapScreen(parkLocationViewModel)
}
