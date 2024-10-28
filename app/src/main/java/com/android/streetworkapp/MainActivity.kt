package com.android.streetworkapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.streetworkapp.model.event.EventRepositoryFirestore
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.park.ParkRepositoryFirestore
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.OverpassParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.user.UserRepositoryFirestore
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.authentication.SignInScreen
import com.android.streetworkapp.ui.event.AddEventScreen
import com.android.streetworkapp.ui.map.MapScreen
import com.android.streetworkapp.ui.navigation.BottomNavigationMenu
import com.android.streetworkapp.ui.navigation.LIST_OF_SCREENS
import com.android.streetworkapp.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Route
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.ui.navigation.ScreenParams
import com.android.streetworkapp.ui.navigation.TopLevelDestination
import com.android.streetworkapp.ui.navigation.TopLevelDestinations
import com.android.streetworkapp.ui.park.ParkOverview
import com.android.streetworkapp.ui.profile.AddFriendScreen
import com.android.streetworkapp.ui.profile.ProfileScreen
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.distinctUntilChanged
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

  // Instantiate fire store database and associated user repository :
  val firestoreDB = FirebaseFirestore.getInstance()
  val userRepository = UserRepositoryFirestore(firestoreDB)
  val userViewModel = UserViewModel(userRepository)

  // Instantiate park repository :
  val parkRepository = ParkRepositoryFirestore(firestoreDB)
  val parkViewModel = ParkViewModel(parkRepository)

  // Instantiate event repository :
  val eventRepository = EventRepositoryFirestore(firestoreDB)
  val eventViewModel = EventViewModel(eventRepository)

  StreetWorkApp(
      parkLocationViewModel, testInvokation, {}, userViewModel, parkViewModel, eventViewModel)
}

@Composable
fun StreetWorkApp(
    parkLocationViewModel: ParkLocationViewModel,
    navTestInvokation: NavigationActions.() -> Unit = {},
    mapCallbackOnMapLoaded: () -> Unit = {},
    userViewModel: UserViewModel,
    parkViewModel: ParkViewModel,
    eventViewModel: EventViewModel
) {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  val currentScreenName = remember { mutableStateOf<String?>(null) } //not using by here since I want to pass the mutableState to a fn
  var screenParams by remember { mutableStateOf<ScreenParams?>(null) }

  navigationActions.registerStringListenerOnDestinationChange(currentScreenName)

  screenParams = LIST_OF_SCREENS.find { currentScreenName.value == it.screenName }

  // Park with no events
  val testPark =
      Park(
          pid = "123",
          name = "Sample Park",
          location = ParkLocation(0.0, 0.0, "321"),
          imageReference = "parks/sample.png",
          rating = 4.0f,
          nbrRating = 2,
          capacity = 10,
          occupancy = 5,
          events = emptyList())
  Scaffold(
    bottomBar = {
        screenParams?.isBottomBarVisible?.takeIf { it }?.let {
            BottomNavigationMenu(
                onTabSelect = { destination -> navigationActions.navigateTo(destination) },
                tabList = LIST_TOP_LEVEL_DESTINATION
            )
        }
    }
  ){ innerPadding ->
      NavHost(
          navController = navController,
          startDestination = Route.MAP //TODO: change to SignIn
      ) { // TODO: handle start destination based on signIn logic
          navigation(
              startDestination = Screen.AUTH,
              route = Route.AUTH,
          ) {
              composable(Screen.AUTH) { SignInScreen(navigationActions, userViewModel) }
          }

          navigation(
              startDestination = Screen.MAP,
              route = Route.MAP,
          ) {
              composable(Screen.MAP) {
                  MapScreen(parkLocationViewModel, navigationActions, mapCallbackOnMapLoaded, innerPadding)
              }
              composable(Screen.PARK_OVERVIEW) {
                  ParkOverview(navigationActions, testPark, eventViewModel)
              }
              composable(Screen.ADD_EVENT) {
                  AddEventScreen(navigationActions, parkViewModel, eventViewModel, userViewModel)
              }
          }

          navigation(
              startDestination = Screen.PROFILE,
              route = Route.PROFILE,
          ) {
              // profile screen + list of friend
              composable(Screen.PROFILE) { ProfileScreen(navigationActions, userViewModel) }
              // screen for adding friend
              composable(Screen.ADD_FRIEND) { AddFriendScreen(navigationActions, userViewModel) }
          }
      }
      navigationActions.apply(navTestInvokation)
  }

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
