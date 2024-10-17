package com.android.streetworkapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventList
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.parklocation.OverpassParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.ui.map.MapScreen
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Route
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.ui.park.ParkOverview
import com.android.streetworkapp.ui.profile.ProfileScreen
import com.google.firebase.Timestamp
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d("MainActivity", "Setup content")
    setContent(parent = null) { StreetWorkAppMain() }
  }
}

// the testInvokation is super ugly but I have NOT found any other way to test the navigation from a
// ui perspective since we don't use fragments
@Composable
fun StreetWorkAppMain(testInvokation: NavigationActions.() -> Unit = {}) {

  // repositories
  val overpassParkLocationRepo = OverpassParkLocationRepository(OkHttpClient())
  // viewmodels
  val parkLocationViewModel = ParkLocationViewModel(overpassParkLocationRepo)

  StreetWorkApp(parkLocationViewModel, testInvokation)
}

@Composable
fun StreetWorkApp(
    parkLocationViewModel: ParkLocationViewModel,
    navTestInvokation: NavigationActions.() -> Unit = {},
    mapCallbackOnMapLoaded: () -> Unit = {}
) {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  val eventList =
      EventList(
          events =
              listOf(
                  Event(
                      eid = "1",
                      title = "Group workout",
                      description = "A fun group workout session to train new skills",
                      participants = 3,
                      maxParticipants = 5,
                      date = Timestamp(0, 0), // 01/01/1970 00:00
                      owner = "user123")))

  // Park with events
  val testPark =
      Park(
          pid = "1",
          name = "EPFL Esplanade",
          location = "EPFL",
          image = null,
          rating = 4.5f,
          nbrRating = 102,
          occupancy = 0.8f,
          events = eventList)

  NavHost(
      navController = navController,
      startDestination = Route.AUTH) { // TODO: handle start destination based on signIn logic
        navigation(
            startDestination = Screen.AUTH,
            route = Route.AUTH,
        ) {
          composable(Screen.AUTH) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                  Button(onClick = { navigationActions.navigateTo(Route.MAP) }) { Text("Start") }
                }
          }
        }

        navigation(
            startDestination = Screen.MAP,
            route = Route.MAP,
        ) {
          composable(Screen.MAP) {
            MapScreen(parkLocationViewModel, navigationActions, mapCallbackOnMapLoaded)
          }
          composable(Screen.PARK_OVERVIEW) { ParkOverview(navigationActions, testPark) }
        }

        navigation(
            startDestination = Screen.PROFILE,
            route = Route.PROFILE,
        ) {
          composable(Screen.PROFILE) { ProfileScreen(navigationActions) }
        }
      }

  navigationActions.apply(navTestInvokation)
}

@Composable
fun Streetworkapp(testing: Boolean) {
  Log.d("Empty composable", "This should be completed")
  if (testing) {
    Log.d("Empty composable", "Context is null")
  } else {
    Log.d("Empty composable", "Context is not null")
  }
}
