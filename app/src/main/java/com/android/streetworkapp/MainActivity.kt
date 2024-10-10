package com.android.streetworkapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.streetworkapp.model.parks.OverpassParkLocationRepository
import com.android.streetworkapp.model.parks.ParkLocationViewModel
import com.android.streetworkapp.ui.map.MapScreen
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Route
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.ui.theme.SampleAppThemeWithoutDynamicColor
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SampleAppThemeWithoutDynamicColor {
        Surface(modifier = Modifier.fillMaxSize()) { StreetWorkAppMain() }
      }
    }
  }
}

// the testInvokation is super ugly but I have NOT found any other way to test the navigation from a
// ui perspective since we don't use fragments
@Composable
fun StreetWorkAppMain(testInvokation: NavigationActions.() -> Unit = {}) {
  val client = OkHttpClient()

  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  val overpassParkLocationRepository = OverpassParkLocationRepository(client)
  val parkLocationViewModel = ParkLocationViewModel(overpassParkLocationRepository)

  NavHost(
      navController = navController,
      startDestination =
          Route.MAP) { // TODO: change startDestination to Route.AUTH for final version
        navigation(
            startDestination = Screen.AUTH,
            route = Route.AUTH,
        ) {
          composable(Screen.AUTH) {}
        }

        navigation(
            startDestination = Screen.MAP,
            route = Route.MAP,
        ) {
          composable(Screen.MAP) { MapScreen(parkLocationViewModel, navigationActions) }
        }

        navigation(
            startDestination = Screen.PROFILE,
            route = Route.PROFILE,
        ) {
          composable(Screen.PROFILE) {}
        }
      }

  navigationActions.apply(testInvokation)
}
