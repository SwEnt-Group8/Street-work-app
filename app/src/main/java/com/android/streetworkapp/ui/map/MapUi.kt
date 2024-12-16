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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.getValue
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
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.ui.park.InteractiveRatingComponent
import com.android.streetworkapp.ui.park.RatingComponent
import com.android.streetworkapp.ui.theme.ColorPalette
import com.android.streetworkapp.ui.theme.Typography as Type
import com.android.streetworkapp.ui.utils.CustomDialog
import com.android.streetworkapp.ui.utils.DialogType
import com.android.streetworkapp.utils.EventDensity
import com.android.streetworkapp.utils.FilterSettings
import com.android.streetworkapp.utils.LocationService
import com.android.streetworkapp.utils.ParkFilter
import com.android.streetworkapp.utils.PermissionManager
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
@OptIn(MapsComposeExperimentalApi::class, ExperimentalMaterial3Api::class)
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
    showFilterSettings: MutableState<Boolean> = mutableStateOf(false)
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

  val parks = parkLocationViewModel.parks.collectAsState().value

  val parkList = parkViewModel.parkList.collectAsState()

  var selectedPark by remember { mutableStateOf(Park()) }

  LaunchedEffect(parks) { parkViewModel.getOrCreateAllParksByLocation(parks) }

  // Set values for park filtering :
  val filter = FilterSettings()
  val parkFilter = ParkFilter(filter)
  val userFilterInput = FilterSettings()

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
        cameraPositionState = cameraPositionState) {

          // Add a marker for the user's current location
          MapEffect { map ->
            if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
              map.isMyLocationEnabled = true
            }
          }

          Log.d("Parklist", parkList.toString())

          // marker for parks
          parkList.value
              .filterNotNull()
              .filter { it.name.contains(searchQuery.value, ignoreCase = true) }
              .filter { parkFilter.filter(it) }
              .forEach { park ->
                ++markerIndex

                val markerState =
                    rememberMarkerState(position = LatLng(park.location.lat, park.location.lon))

                MarkerInfoWindow(
                    tag = "Marker$markerIndex",
                    state = markerState,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                    onClick = {
                      markerState.showInfoWindow()
                      if (selectedPark == park) {
                        selectedPark = Park()
                        markerState.hideInfoWindow()
                        parkViewModel.getOrCreateParkByLocation(park.location)
                        parkViewModel.setParkLocation(park.location)
                        navigationActions.navigateTo(Screen.PARK_OVERVIEW)
                      }
                      selectedPark = park
                      true
                    }) {
                      MarkerInfoWindowContent(park)
                    }
              }
        }
  }
  // Settings variable defined beforehand :
  // Affected by the filter settings => changes confirmed when confirming (onSubmit).

  // Display the Filter component :
  CustomDialog(
      showFilterSettings,
      dialogType = DialogType.CONFIRM,
      title = "Filter parks",
      Content = { ParkFilterSettings(userFilterInput) },
      onSubmit = { filter.set(userFilterInput) },
      onDismiss = { userFilterInput.set(filter) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkFilterSettings(userFilterInput: FilterSettings) {

  val filterChipColors =
      FilterChipDefaults.filterChipColors(
          selectedLabelColor = ColorPalette.PRINCIPLE_BACKGROUND_COLOR,
          selectedContainerColor = ColorPalette.INTERACTION_COLOR_DARK)

  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    // Park rating filter :
    Text(
        "Minimum park rating : ${userFilterInput.minRating.value} stars",
        fontSize = Type.bodyLarge.fontSize)
    InteractiveRatingComponent(userFilterInput.minRating)

    HorizontalDivider()

    // Event quantity filter :
    Text(
        "Minimum density of events :",
        fontSize = Type.bodyLarge.fontSize)

    Row(modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(0.825f)) {
      FilterChip(
          selected = userFilterInput.eventDensity.contains(EventDensity.LOW),
          onClick = { userFilterInput.updateDensity(EventDensity.LOW) },
          label = { Text(EventDensity.LOW.name) },
          colors = filterChipColors,
          modifier = Modifier.padding(end = 2.dp))

      FilterChip(
          selected = userFilterInput.eventDensity.contains(EventDensity.MEDIUM),
          onClick = { userFilterInput.updateDensity(EventDensity.MEDIUM) },
          label = { Text(EventDensity.MEDIUM.name) },
          colors = filterChipColors,
          modifier = Modifier.padding(end = 2.dp))

      FilterChip(
          selected = userFilterInput.eventDensity.contains(EventDensity.HIGH),
          onClick = { userFilterInput.updateDensity(EventDensity.HIGH) },
          label = { Text(EventDensity.HIGH.name) },
          colors = filterChipColors,
          modifier = Modifier.padding(end = 2.dp))
    }

    HorizontalDivider()

    Button(
        onClick = { userFilterInput.reset() },
        colors = ColorPalette.BUTTON_COLOR,
        modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 4.dp)) {
          Text("Reset filters")
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
