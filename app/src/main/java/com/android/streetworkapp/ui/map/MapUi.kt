package com.android.streetworkapp.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.ui.park.RatingComponent
import com.android.streetworkapp.ui.theme.ColorPalette
import com.android.streetworkapp.utils.LocationService
import com.android.streetworkapp.utils.PermissionManager
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.CoroutineScope

/**
 * The MapScreen composable displays a Google Map with markers for nearby parks.
 *
 * @param parkLocationViewModel The view model for park locations.
 * @param navigationActions The navigation actions to navigate to other screens.
 */
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(
    parkLocationViewModel: ParkLocationViewModel,
    parkViewModel: ParkViewModel,
    userViewModel: UserViewModel,
    navigationActions: NavigationActions = NavigationActions(rememberNavController()),
    searchQuery: MutableState<String>,
    callbackOnMapLoaded: () -> Unit = {},
    innerPaddingValues: PaddingValues = PaddingValues(0.dp),
    scope: CoroutineScope = rememberCoroutineScope(),
    host: SnackbarHostState? = null,
) {

  val context = LocalContext.current
  // call utils for location management
  val locationService = LocationService(context, userViewModel, navigationActions, scope, host)
  val permissionManager = PermissionManager(context)
  // Initiate initial fail-safe value
  var initialLatLng = remember { mutableStateOf(LatLng(46.518659400000004, 6.566561505148001)) }
  parkLocationViewModel.findNearbyParks(initialLatLng.value.latitude, initialLatLng.value.longitude)

  // Check the localisation permission and Update the current location
  MapManager(
      userLocation = initialLatLng,
      onUserLocationChange = { initialLatLng = it },
      permissionManager,
      locationService)

  // Update nearby park everytime initialLatLng changes
  LaunchedEffect(initialLatLng.value) {
    parkLocationViewModel.findNearbyParks(
        initialLatLng.value.latitude, initialLatLng.value.longitude)
    Log.d("Localisation", "fetch new park")
    locationService.rewardParkDiscovery(
        userViewModel.currentUser.value, initialLatLng.value, parkLocationViewModel.parks.value)
  }

  // Define how many event is considered a Hot place
  val hotPlace = 5.0f

  // Define the start and end colors for the gradient
  val startColor = ColorPalette.LOGO_BLUE
  val endColor = ColorPalette.LOGO_RED

  // variable for each park color
  var interpolatedColor: Color // set default color
  var markerIcon: BitmapDescriptor

  // Handling user MVVM
  val currentUser = userViewModel.currentUser.collectAsState().value

  if (currentUser != null) {
    userViewModel.getParksByUid(currentUser.uid)
  }

  // Handle parks MVVM
  val parks = parkLocationViewModel.parks.collectAsState().value

  val parkList = parkViewModel.parkList.collectAsState()

  LaunchedEffect(parks) { parkViewModel.getOrCreateAllParksByLocation(parks) }

  Box(modifier = Modifier.testTag("mapScreen")) {
    // Create a CameraPositionState to control the camera position
    val cameraPositionState = rememberCameraPositionState {
      position = CameraPosition.Builder().target(initialLatLng.value).zoom(12f).build()
    }

    var markerIndex = 0
    // Display the Google Map
    GoogleMap(
        onMapLoaded = callbackOnMapLoaded,
        modifier = Modifier.fillMaxSize().padding(innerPaddingValues).testTag("googleMap"),
        cameraPositionState = cameraPositionState,
        contentDescription = "GoogleMap") {

          // Add a marker for the user's current location
          MapEffect { map ->
            if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
              map.isMyLocationEnabled = true
            }
          }

          // marker for parks
          parkList.value
              .filterNotNull()
              .filter { it.name.contains(searchQuery.value, ignoreCase = true) }
              .forEach { park ->
                ++markerIndex

                // define park location
                val markerState =
                    rememberMarkerState(position = LatLng(park.location.lat, park.location.lon))

                // Interpolate color based on number of event (make the gradient)
                interpolatedColor =
                    gradientColor(startColor, endColor, (park.events.size) / hotPlace)

                markerIcon = BitmapDescriptorFactory.defaultMarker(colorToHue(interpolatedColor))

                // default marker for discovered park
                MarkerInfoWindow(
                    tag = "Marker$markerIndex",
                    state = markerState,
                    icon = markerIcon,
                    onClick = {
                      markerState.showInfoWindow()
                      true
                    },
                    onInfoWindowClick = {
                      parkViewModel.setPark(park)
                      parkViewModel.setParkLocation(park.location)
                      navigationActions.navigateTo(Screen.PARK_OVERVIEW)
                    }) {
                      MarkerInfoWindowContent(park)
                    }
              }
        }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapSearchBar(query: MutableState<String>, onCancel: () -> Unit) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
    SearchBar(
        query = query.value,
        onQueryChange = { query.value = it },
        placeholder = { "Search for parks" },
        modifier = Modifier.testTag("searchBar").padding(bottom = 8.dp).padding(horizontal = 4.dp),
        active = false,
        onActiveChange = {},
        onSearch = {},
        leadingIcon = {
          IconButton(onClick = onCancel, modifier = Modifier.testTag("cancelSearchButton")) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Search",
            )
          }
        },
        colors = SearchBarDefaults.colors(containerColor = ColorPalette.INTERACTION_COLOR_LIGHT)) {}
  }
}

@Composable
fun MarkerInfoWindowContent(park: Park) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier =
          Modifier.background(Color.White, shape = RoundedCornerShape(10.dp))
              .padding(10.dp)
              .testTag("markerInfoWindow")) {
        Text(
            text = park.name,
            color = Color.Black,
            fontSize = 16.sp,
            modifier = Modifier.padding(5.dp).testTag("parkName"))

        RatingComponent(park.rating.toInt(), park.nbrRating)

        Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(
              Icons.Filled.Event,
              contentDescription = "eventIcon",
              modifier = Modifier.padding(horizontal = 5.dp).testTag("eventIcon"))
          Text(
              text = "${park.events.size} event(s) planned",
              modifier = Modifier.testTag("eventsPlanned"))
        }
      }
}

/**
 * Returns a color that is a gradient between two colors based on a fraction.
 *
 * @param startColor The starting color of the gradient
 * @param endColor The ending color of the gradient
 * @param fraction A value between 0 and 1 indicating the position between the start and end colors
 * @return A Color that represents the gradient at the given fraction
 */
fun gradientColor(startColor: Color, endColor: Color, fraction: Float): Color {
  val startR = startColor.red
  val startG = startColor.green
  val startB = startColor.blue
  val startA = startColor.alpha

  val endR = endColor.red
  val endG = endColor.green
  val endB = endColor.blue
  val endA = endColor.alpha

  val r = startR + fraction * (endR - startR)
  val g = startG + fraction * (endG - startG)
  val b = startB + fraction * (endB - startB)
  val a = startA + fraction * (endA - startA)

  return Color(r, g, b, a)
}

/**
 * Converts a color to its hue value needed for BitMap
 *
 * @param color The color to be converted
 * @return The hue value of the color, in degrees (0-360)
 */
fun colorToHue(color: Color): Float {
  // Convert the Color to ARGB values (0-255)
  val r = (color.red * 255).toInt()
  val g = (color.green * 255).toInt()
  val b = (color.blue * 255).toInt()

  // Use the Android Color class to convert to HSV
  val hsv = FloatArray(3)
  android.graphics.Color.RGBToHSV(r, g, b, hsv)

  // Return the hue value (0-360 degrees)
  return hsv[0]
}
