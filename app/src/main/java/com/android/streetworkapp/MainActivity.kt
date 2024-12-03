package com.android.streetworkapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.android.streetworkapp.device.network.isInternetAvailable
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
import com.android.streetworkapp.model.preferences.PreferencesRepositoryDataStore
import com.android.streetworkapp.model.preferences.PreferencesViewModel
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
import com.android.streetworkapp.ui.tutorial.TutorialEvent
import com.android.streetworkapp.ui.utils.CustomDialog
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val internetAvailable = isInternetAvailable(this)
    // Instantiate preferences view model here to avoid unauthorized multiple instantiations
    val preferencesRepository = PreferencesRepositoryDataStore(this)
    val preferencesViewModel = PreferencesViewModel(preferencesRepository)
    Log.d("MainActivity", "Setup content")
    setContent(parent = null) {
      StreetWorkAppMain(
          internetAvailable = internetAvailable, preferencesViewModel = preferencesViewModel)
    }
  }
}

// the testInvokation is super ugly but I have NOT found any other way to test the navigation from a
// ui perspective since we don't use fragments
@Composable
fun StreetWorkAppMain(
    preferencesViewModel: PreferencesViewModel,
    testInvokation: NavigationActions.() -> Unit = {},
    internetAvailable: Boolean = false
) {

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

  // Get the preferences cached parameters
  val loginState by preferencesViewModel.loginState.collectAsState()
  val uid by preferencesViewModel.uid.collectAsState()
  val name by preferencesViewModel.name.collectAsState()
  val score by preferencesViewModel.score.collectAsState()

  LaunchedEffect(Unit) {
    preferencesViewModel.getLoginState()
    preferencesViewModel.getUid()
    preferencesViewModel.getName()
    preferencesViewModel.getScore()
  }

  // Ensure login state is initialized before deciding the start destination
  var resolvedStartDestination by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(loginState) {
    resolvedStartDestination =
        when (loginState) {
          true -> Route.MAP
          false -> Route.AUTH
          else -> null
        }
  }

  // Display the splash screen while determining the start destination
  if (resolvedStartDestination == null) {
    SplashScreen()
  } else {
    StreetWorkApp(
        parkLocationViewModel,
        testInvokation,
        {},
        userViewModel,
        parkViewModel,
        eventViewModel,
        progressionViewModel,
        workoutViewModel,
        textModerationViewModel,
        preferencesViewModel,
        startDestination = resolvedStartDestination!!)
  }
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
    preferencesViewModel: PreferencesViewModel,
    navTestInvokationOnEachRecompose: Boolean = false,
    e2eEventTesting: Boolean = false,
    startDestination: String = Route.AUTH
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
        NavHost(navController = navController, startDestination = startDestination) {
          navigation(
              startDestination = Screen.AUTH,
              route = Route.AUTH,
          ) {
            composable(Screen.AUTH) {
              SignInScreen(navigationActions, userViewModel, preferencesViewModel)
            }
          }
          navigation(startDestination = Screen.PROGRESSION, route = Route.PROGRESSION) {
            composable(Screen.PROGRESSION) {
              infoManager.Display(LocalContext.current)
              ProgressScreen(navigationActions, userViewModel, progressionViewModel, innerPadding)
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
                  mapCallbackOnMapLoaded,
                  innerPadding)
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
                  TrainChallengeScreen(activity, isTimeDependent, workoutViewModel, innerPadding)
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

@Composable
fun SplashScreen() {
  // A simple loading screen while determining the start destination
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    CircularProgressIndicator()
  }
}
