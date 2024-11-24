package com.android.streetworkapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventRepositoryFirestore
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.park.NominatimParkNameRepository
import com.android.streetworkapp.model.park.ParkRepositoryFirestore
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.OverpassParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.progression.ProgressionRepositoryFirestore
import com.android.streetworkapp.model.progression.ProgressionViewModel
import com.android.streetworkapp.model.user.UserRepositoryFirestore
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.authentication.SignInScreen
import com.android.streetworkapp.ui.event.AddEventScreen
import com.android.streetworkapp.ui.event.EventOverviewScreen
import com.android.streetworkapp.ui.map.MapScreen
import com.android.streetworkapp.ui.navigation.BottomNavigationMenu
import com.android.streetworkapp.ui.navigation.BottomNavigationMenuType
import com.android.streetworkapp.ui.navigation.EventBottomBar
import com.android.streetworkapp.ui.navigation.LIST_OF_SCREENS
import com.android.streetworkapp.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Route
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.ui.navigation.ScreenParams
import com.android.streetworkapp.ui.navigation.TopAppBarManager
import com.android.streetworkapp.ui.navigation.TopAppBarWrapper
import com.android.streetworkapp.ui.park.ParkOverviewScreen
import com.android.streetworkapp.ui.profile.AddFriendScreen
import com.android.streetworkapp.ui.profile.ProfileScreen
import com.android.streetworkapp.ui.progress.ProgressScreen
import com.android.streetworkapp.ui.theme.ColorPalette
import com.android.streetworkapp.ui.utils.CustomDialog
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
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
  val parkNameRepository = NominatimParkNameRepository(OkHttpClient())
  // viewmodels
  val parkLocationViewModel = ParkLocationViewModel(overpassParkLocationRepo)

  // Instantiate fire store database and associated user repository :
  val firestoreDB = FirebaseFirestore.getInstance()
  val userRepository = UserRepositoryFirestore(firestoreDB)
  val userViewModel = UserViewModel(userRepository)

  // Instantiate park repository :
  val parkRepository = ParkRepositoryFirestore(firestoreDB)
  val parkViewModel = ParkViewModel(parkRepository, parkNameRepository)

  // Instantiate event repository :
  val eventRepository = EventRepositoryFirestore(firestoreDB)
  val eventViewModel = EventViewModel(eventRepository)

  // Instantiate progression repository
  val progressionRepository = ProgressionRepositoryFirestore(firestoreDB)
  val progressionViewModel = ProgressionViewModel(progressionRepository)

  StreetWorkApp(
      parkLocationViewModel,
      testInvokation,
      {},
      userViewModel,
      parkViewModel,
      eventViewModel,
      progressionViewModel)
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun StreetWorkApp(
    parkLocationViewModel: ParkLocationViewModel,
    navTestInvokation: NavigationActions.() -> Unit = {},
    mapCallbackOnMapLoaded: () -> Unit = {},
    userViewModel: UserViewModel,
    parkViewModel: ParkViewModel,
    eventViewModel: EventViewModel,
    progressionViewModel: ProgressionViewModel,
    navTestInvokationOnEachRecompose: Boolean = false,
    e2eEventTesting: Boolean = false
) {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  // To display SnackBars
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  val currentScreenName = remember {
    mutableStateOf<String?>(null)
  } // not using by here since I want to pass the mutableState to a fn
  var screenParams by remember { mutableStateOf<ScreenParams?>(null) }

  var firstTimeLoaded by remember { mutableStateOf<Boolean>(true) }

  navigationActions.registerStringListenerOnDestinationChange(currentScreenName)
  screenParams = LIST_OF_SCREENS.find { currentScreenName.value == it.screenName }

  // Park with no events
  val sampleEvent =
      Event(
          eid = "event123",
          title = "Community Park Cleanup",
          // description = "Join us for a day of community service to clean up the local park!",
          description =
              "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed eget leo vitae enim facilisis fringilla. Morbi feugiat scelerisque nisl, vel vehicula sem malesuada et. Proin id arcu eget nisi congue facilisis. Integer feugiat, ex eu vestibulum sagittis, erat felis scelerisque dui, id varius turpis magna in nisi. Suspendisse potenti. Pellentesque quis posuere elit. Vivamus tincidunt dui vel risus dignissim, sit amet dignissim velit cursus. Nam sodales nulla non semper pharetra. Aliquam erat volutpat. Morbi pharetra odio id facilisis pulvinar. Mauris aliquet ipsum eu dolor ultrices, id sodales sapien dictum. Nam facilisis vestibulum viverra.\n" +
                  "\n" +
                  "Sed elementum risus in tempor accumsan. Integer egestas, eros at venenatis ultricies, quam nunc dictum urna, a aliquam odio erat at lacus. In lacinia mauris sit amet orci accumsan, in bibendum arcu condimentum. In ut lacus et ipsum tincidunt condimentum. Fusce non magna ut urna vestibulum gravida at ut felis. Nullam auctor dapibus sem, ut rhoncus turpis gravida non. Pellentesque elementum erat a libero luctus feugiat. Aenean tincidunt fermentum nisl, in rhoncus ex iaculis nec. Vestibulum gravida, est vel scelerisque varius, magna erat pharetra risus, a sollicitudin libero orci nec lectus. Fusce lobortis magna in arcu vehicula, sit amet fermentum leo interdum",
          participants = 15,
          maxParticipants = 50,
          date = Timestamp(Date()), // Current date and time
          owner = "ownerUserId",
          listParticipants = listOf("user1", "user2", "user3"),
          parkId = "park567")

  Scaffold(
      containerColor = ColorPalette.PRINCIPLE_BACKGROUND_COLOR,
      topBar = {
        screenParams
            ?.isTopBarVisible
            ?.takeIf { it }
            ?.let { TopAppBarWrapper(navigationActions, screenParams?.topAppBarManager) }
      },
      snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
      bottomBar = {
        screenParams
            ?.takeIf { it.isBottomBarVisible }
            ?.let {
              when (it.bottomBarType) {
                BottomNavigationMenuType.DEFAULT -> {
                  BottomNavigationMenu(
                      onTabSelect = { destination -> navigationActions.navigateTo(destination) },
                      tabList = LIST_TOP_LEVEL_DESTINATION)
                }
                BottomNavigationMenuType.EVENT_OVERVIEW -> {
                  EventBottomBar(eventViewModel, userViewModel, navigationActions)
                  // selected
                }
                BottomNavigationMenuType
                    .NONE -> {} // we shouldn't land here, tests will throw exception if this
              // happens. (Still need to handle the case to compile though)
              }
            }
      }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.AUTH) { // TODO: handle start destination based on signIn logic
              navigation(
                  startDestination = Screen.AUTH,
                  route = Route.AUTH,
              ) {
                composable(Screen.AUTH) { SignInScreen(navigationActions, userViewModel) }
              }
              navigation(startDestination = Screen.PROGRESSION, route = Route.PROGRESSION) {
                composable(Screen.PROGRESSION) {
                  ProgressScreen(
                      navigationActions, userViewModel, progressionViewModel, innerPadding)
                }
              }
              navigation(
                  startDestination = Screen.MAP,
                  route = Route.MAP,
              ) {
                composable(Screen.MAP) {
                  MapScreen(
                      parkLocationViewModel,
                      parkViewModel,
                      navigationActions,
                      mapCallbackOnMapLoaded,
                      innerPadding)
                }
                composable(Screen.PARK_OVERVIEW) {
                  ParkOverviewScreen(
                      parkViewModel, innerPadding, navigationActions, eventViewModel, userViewModel)
                }
                composable(Screen.ADD_EVENT) {
                  AddEventScreen(
                      navigationActions,
                      parkViewModel,
                      eventViewModel,
                      userViewModel,
                      scope,
                      snackbarHostState)
                }
                composable(Screen.EVENT_OVERVIEW) {
                  EventOverviewScreen(eventViewModel, parkViewModel, innerPadding)
                }
              }

              navigation(
                  startDestination = Screen.PROFILE,
                  route = Route.PROFILE,
              ) {
                // profile screen + list of friend
                composable(Screen.PROFILE) {
                  ProfileScreen(navigationActions, userViewModel, innerPadding)
                  val showSettingsDialog = remember { mutableStateOf(false) }

                  screenParams?.topAppBarManager?.setActionCallback(
                      TopAppBarManager.TopAppBarAction.SETTINGS) {
                        showSettingsDialog.value = true
                      }

                  // The settings "in" the profile screen
                  // TODO : Implement the dialog Content composable
                  CustomDialog(
                      showSettingsDialog,
                      "Settings",
                      Content = { Text("Settings to be implemented") },
                  )
                }
                // screen for adding friend
                composable(Screen.ADD_FRIEND) { AddFriendScreen(userViewModel, innerPadding) }
              }
            }

        if (e2eEventTesting) {
          LaunchedEffect(navTestInvokation) { navigationActions.apply(navTestInvokation) }
        } else if (firstTimeLoaded || navTestInvokationOnEachRecompose) {
          firstTimeLoaded = false
          navigationActions.apply(navTestInvokation)
        }
      }
}
