package com.android.streetworkapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
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
import com.android.streetworkapp.ui.map.MapSearchBar
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
import com.android.streetworkapp.ui.train.TrainParamScreen
import com.android.streetworkapp.ui.train.TrainSoloScreen
import com.android.streetworkapp.ui.tutorial.TutorialEvent
import com.android.streetworkapp.ui.utils.CustomDialog
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import kotlinx.serialization.json.JsonNull.content
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
    content:
        @Composable
        (activity: String, isTimeDependent: Boolean, time: Int?, sets: Int?, reps: Int?) -> Unit
) {
  composable(
      route = route,
      arguments =
          listOf(
              navArgument("activity") { type = NavType.StringType },
              navArgument("isTimeDependent") { type = NavType.BoolType },
              navArgument("time") {
                type = NavType.IntType
                defaultValue = 0
              },
              navArgument("sets") {
                type = NavType.IntType
                defaultValue = 0
              },
              navArgument("reps") {
                type = NavType.IntType
                defaultValue = 0
              })) { backStackEntry ->
        val activity = backStackEntry.arguments?.getString("activity") ?: "Unknown"
        val isTimeDependent = backStackEntry.arguments?.getBoolean("isTimeDependent") ?: false
        val time = backStackEntry.arguments?.getInt("time")
        val sets = backStackEntry.arguments?.getInt("sets")
        val reps = backStackEntry.arguments?.getInt("reps")

        content(activity, isTimeDependent, time, sets, reps)
      }
}

@OptIn(ExperimentalMaterial3Api::class)
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
  val showSearchBar = remember { mutableStateOf(false) }

  // query for the search bar
  val searchQuery = remember { mutableStateOf("") }

  Log.d("InfoDialog", "Main - Instantiating the InfoDialogManager")
  val infoManager =
      InfoDialogManager(
          showInfoDialog, currentScreenName, topAppBarManager = screenParams?.topAppBarManager)

  Scaffold(
      containerColor = ColorPalette.PRINCIPLE_BACKGROUND_COLOR,
      topBar = {
        if (showSearchBar.value && screenParams?.hasSearchBar == true) {
          MapSearchBar(searchQuery) {
            searchQuery.value = ""
            showSearchBar.value = false
          }
        } else {
          screenParams
              ?.isTopBarVisible
              ?.takeIf { it }
              ?.let {
                TopAppBarWrapper(navigationActions, screenParams?.topAppBarManager)
                // setup the InfoDialogManager in topBar, because it relies on the topAppBarManager.
                Log.d("InfoDialog", "Main - Setting up the InfoDialogManager")
                infoManager.setUp()
              }
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
                  infoManager.Display(LocalContext.current)
                  ProgressScreen(
                      navigationActions, userViewModel, progressionViewModel, innerPadding)
                }
              }
              navigation(
                  startDestination = Screen.MAP,
                  route = Route.MAP,
              ) {
                composable(Screen.MAP) {
                  infoManager.Display(LocalContext.current)
                  MapScreen(
                      parkLocationViewModel,
                      parkViewModel,
                      navigationActions,
                      searchQuery,
                      mapCallbackOnMapLoaded,
                      innerPadding)
                  screenParams?.topAppBarManager?.setActionCallback(
                      TopAppBarManager.TopAppBarAction.SEARCH) {
                        showSearchBar.value = true
                      }
                }
                composable(Screen.PARK_OVERVIEW) {
                  infoManager.Display(LocalContext.current)
                  ParkOverviewScreen(
                      parkViewModel, innerPadding, navigationActions, eventViewModel, userViewModel)
                }
                composable(Screen.ADD_EVENT) {
                  infoManager.Display(LocalContext.current)
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
                  infoManager.Display(LocalContext.current)
                  EventOverviewScreen(
                      eventViewModel, parkViewModel, userViewModel, navigationActions, innerPadding)
                }
                composable(Screen.TUTO_EVENT) { TutorialEvent(navigationActions) }
              }

              navigation(
                  startDestination = Screen.PROFILE,
                  route = Route.PROFILE,
              ) {
                // profile screen + list of friend
                composable(Screen.PROFILE) {
                  infoManager.Display(LocalContext.current)
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
                  infoManager.Display(LocalContext.current)
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
                    innerPadding = innerPadding,
                    content = { activity, isTimeDependent, time, sets, reps ->
                      TrainSoloScreen(
                          activity,
                          isTimeDependent,
                          time,
                          sets,
                          reps,
                          workoutViewModel,
                          userViewModel,
                          innerPadding)
                    })

                trainComposable(
                    route = Screen.TRAIN_COACH,
                    workoutViewModel = workoutViewModel,
                    innerPadding = innerPadding,
                    content = { activity, isTimeDependent, time, sets, reps ->
                      TrainCoachScreen(
                          activity,
                          isTimeDependent,
                          time,
                          sets,
                          reps,
                          workoutViewModel,
                          innerPadding)
                    })

                trainComposable(
                    route = Screen.TRAIN_CHALLENGE,
                    workoutViewModel = workoutViewModel,
                    innerPadding = innerPadding,
                    content = { activity, isTimeDependent, time, sets, reps ->
                      TrainChallengeScreen(
                          activity,
                          isTimeDependent,
                          time,
                          sets,
                          reps,
                          workoutViewModel,
                          innerPadding)
                    })
                composable(
                    route = "TrainParam/{activity}/{isTimeDependent}/{type}",
                    arguments =
                        listOf(
                            navArgument("activity") { type = NavType.StringType },
                            navArgument("isTimeDependent") { type = NavType.BoolType },
                            navArgument("type") { type = NavType.StringType })) { backStackEntry ->
                      val activity =
                          backStackEntry.arguments?.getString("activity") ?: "defaultActivity"
                      val isTimeDependent =
                          backStackEntry.arguments?.getBoolean("isTimeDependent") ?: false
                      val type = backStackEntry.arguments?.getString("type") ?: "defaultType"

                      // Call TrainParamScreen with the parameters
                      TrainParamScreen(navigationActions, activity, isTimeDependent, type)
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
