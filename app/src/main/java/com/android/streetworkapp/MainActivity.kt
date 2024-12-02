package com.android.streetworkapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventRepositoryFirestore
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.moderation.PerspectiveAPIRepository
import com.android.streetworkapp.model.moderation.TextModerationViewModel
import com.android.streetworkapp.model.park.NominatimParkNameRepository
import com.android.streetworkapp.model.park.ParkRepositoryFirestore
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.OverpassParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.progression.ProgressionRepositoryFirestore
import com.android.streetworkapp.model.progression.ProgressionViewModel
import com.android.streetworkapp.model.user.UserRepositoryFirestore
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.WorkoutRepositoryFirestore
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.authentication.SignInScreen
import com.android.streetworkapp.ui.event.AddEventScreen
import com.android.streetworkapp.ui.event.EventOverviewScreen
import com.android.streetworkapp.ui.map.MapScreen
import com.android.streetworkapp.ui.navigation.BottomNavigationMenu
import com.android.streetworkapp.ui.navigation.BottomNavigationMenuType
import com.android.streetworkapp.ui.navigation.EventBottomBar
import com.android.streetworkapp.ui.navigation.InfoDialogManager
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
import com.android.streetworkapp.ui.train.TrainChallengeScreen
import com.android.streetworkapp.ui.train.TrainCoachScreen
import com.android.streetworkapp.ui.train.TrainHubScreen
import com.android.streetworkapp.ui.train.TrainSoloScreen
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

  // Instantiate the workout repository
  val workoutRepository = WorkoutRepositoryFirestore(firestoreDB)
  val workoutViewModel = WorkoutViewModel(workoutRepository)

  // Instantiate Text Moderation
  val textModerationRepository = PerspectiveAPIRepository(OkHttpClient())
  val textModerationViewModel = TextModerationViewModel(textModerationRepository)

  StreetWorkApp(
      parkLocationViewModel,
      testInvokation,
      {},
      userViewModel,
      parkViewModel,
      eventViewModel,
      progressionViewModel,
      workoutViewModel,
      textModerationViewModel)
}

fun NavGraphBuilder.trainComposable(
    route: String,
    workoutViewModel: WorkoutViewModel,
    innerPadding: PaddingValues,
    content: @Composable (activity: String, isTimeDependent: Boolean) -> Unit
) {
  composable(
      route = route,
      arguments =
          listOf(
              navArgument("activity") { type = NavType.StringType },
              navArgument("isTimeDependent") { type = NavType.BoolType })) { backStackEntry ->
        val activity = backStackEntry.arguments?.getString("activity") ?: "Unknown"
        val isTimeDependent = backStackEntry.arguments?.getBoolean("isTimeDependent") ?: false
        content(activity, isTimeDependent)
      }
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
    workoutViewModel: WorkoutViewModel,
    textModerationViewModel: TextModerationViewModel,
    navTestInvokationOnEachRecompose: Boolean = false,
    e2eEventTesting: Boolean = false
) {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  // To display SnackBars
  val scope = rememberCoroutineScope()
  val host = remember { SnackbarHostState() }

  val currentScreenName = remember {
    mutableStateOf<String?>(null)
  } // not using by here since I want to pass the mutableState to a fn
  var screenParams by remember { mutableStateOf<ScreenParams?>(null) }

  var firstTimeLoaded by remember { mutableStateOf<Boolean>(true) }

  navigationActions.registerStringListenerOnDestinationChange(currentScreenName)
  screenParams = LIST_OF_SCREENS.find { currentScreenName.value == it.screenName }

  // Instantiate info manager and its components :
  val showInfoDialog = remember { mutableStateOf(false) }
  Log.d("InfoDialog", "Main - Instantiating the InfoDialogManager")
  val infoManager =
      InfoDialogManager(
          showInfoDialog, currentScreenName, topAppBarManager = screenParams?.topAppBarManager)

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
            ?.let {
              TopAppBarWrapper(navigationActions, screenParams?.topAppBarManager)
              // setup the InfoDialogManager in topBar, because it relies on the topAppBarManager.
              Log.d("InfoDialog", "Main - Setting up the InfoDialogManager")
              infoManager.setUp()
            }
      },
      snackbarHost = {
        SnackbarHost(
            hostState = host,
            snackbar = { data ->
              Snackbar(actionColor = ColorPalette.INTERACTION_COLOR_DARK, snackbarData = data)
            })
      },
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
                  EventBottomBar(eventViewModel, userViewModel, navigationActions, scope, host)
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
                  infoManager.Display()
                  ProgressScreen(
                      navigationActions, userViewModel, progressionViewModel, innerPadding)
                }
              }
              navigation(
                  startDestination = Screen.MAP,
                  route = Route.MAP,
              ) {
                composable(Screen.MAP) {
                  infoManager.Display()
                  MapScreen(
                      parkLocationViewModel,
                      parkViewModel,
                      navigationActions,
                      mapCallbackOnMapLoaded,
                      innerPadding)
                }
                composable(Screen.PARK_OVERVIEW) {
                  // val image =
                  // "iVBORw0KGgoAAAANSUhEUgAABaAAAAeACAIAAAC38WeAAAAAAXNSR0IArs4c6QAAAANzQklUCAgI2+FP4AAAIABJREFUeJzs3cFuHddhx+FzhqRlpQjQogGaF+hb5RnyBHkp2TDgF8nC9iarZhmjcGtLIud0IcpKEy4kmszc3+T7IPyh5bkQIV7+7sxwfvfT1wMAAACgbDv6AAAAAAC/lMABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA5AkcAAAAQJ7AAQAAAOQJHAAAAECewAEAAADkCRwAAABAnsABAAAA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"
                  // DisplayRawBase64ToImage(image)
                  infoManager.Display()
                  ParkOverviewScreen(
                      parkViewModel, innerPadding, navigationActions, eventViewModel, userViewModel)
                }
                composable(Screen.ADD_EVENT) {
                  infoManager.Display()
                  AddEventScreen(
                      navigationActions,
                      parkViewModel,
                      eventViewModel,
                      userViewModel,
                      textModerationViewModel,
                      scope,
                      host,
                      innerPadding)
                }
                composable(Screen.EVENT_OVERVIEW) {
                  infoManager.Display()
                  EventOverviewScreen(
                      eventViewModel, parkViewModel, userViewModel, navigationActions, innerPadding)
                }
              }

              navigation(
                  startDestination = Screen.PROFILE,
                  route = Route.PROFILE,
              ) {
                // profile screen + list of friend
                composable(Screen.PROFILE) {
                  infoManager.Display()
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
                      tag = "Settings",
                      Content = { Text("Settings to be implemented") },
                  )
                }
                // screen for adding friend
                composable(Screen.ADD_FRIEND) {
                  infoManager.Display()
                  AddFriendScreen(userViewModel, navigationActions, scope, host, innerPadding)
                }
              }

              navigation(
                  startDestination = Screen.TRAIN_HUB,
                  route = Route.TRAIN_HUB,
              ) {
                composable(Screen.TRAIN_HUB) {
                  TrainHubScreen(navigationActions, workoutViewModel, userViewModel, innerPadding)
                }
                trainComposable(
                    route = Screen.TRAIN_SOLO,
                    workoutViewModel = workoutViewModel,
                    innerPadding = innerPadding) { activity, isTimeDependent ->
                      TrainSoloScreen(activity, isTimeDependent, workoutViewModel, innerPadding)
                    }

                trainComposable(
                    route = Screen.TRAIN_COACH,
                    workoutViewModel = workoutViewModel,
                    innerPadding = innerPadding) { activity, isTimeDependent ->
                      TrainCoachScreen(activity, isTimeDependent, workoutViewModel, innerPadding)
                    }

                trainComposable(
                    route = Screen.TRAIN_CHALLENGE,
                    workoutViewModel = workoutViewModel,
                    innerPadding = innerPadding) { activity, isTimeDependent ->
                      TrainChallengeScreen(
                          activity, isTimeDependent, workoutViewModel, innerPadding)
                    }
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
