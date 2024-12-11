package com.android.streetworkapp

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.android.sample.R
import com.android.streetworkapp.device.network.isInternetAvailable
import com.android.streetworkapp.model.event.EventRepositoryFirestore
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.image.ImageRepositoryFirestore
import com.android.streetworkapp.model.image.ImageViewModel
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
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepositoryFirestore
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.WorkoutRepositoryFirestore
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.authentication.SignInScreen
import com.android.streetworkapp.ui.event.AddEventScreen
import com.android.streetworkapp.ui.event.EventOverviewScreen
import com.android.streetworkapp.ui.map.MapScreen
import com.android.streetworkapp.ui.map.MapSearchBar
import com.android.streetworkapp.ui.miscellaneous.SplashScreen
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
import com.android.streetworkapp.ui.profile.SettingsContent
import com.android.streetworkapp.ui.progress.ProgressScreen
import com.android.streetworkapp.ui.theme.ColorPalette
import com.android.streetworkapp.ui.train.TrainChallengeScreen
import com.android.streetworkapp.ui.train.TrainCoachScreen
import com.android.streetworkapp.ui.train.TrainHubScreen
import com.android.streetworkapp.ui.train.TrainParamScreen
import com.android.streetworkapp.ui.train.TrainSoloScreen
import com.android.streetworkapp.ui.tutorial.TutorialEvent
import com.android.streetworkapp.ui.utils.CustomDialog
import com.android.streetworkapp.ui.utils.DialogType
import com.android.streetworkapp.ui.utils.trainComposable
import com.android.streetworkapp.utils.GoogleAuthService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {
  @SuppressLint("SourceLockedOrientationActivity")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val internetAvailable = isInternetAvailable(this)
    // Instantiate preferences view model here to avoid unauthorized multiple instantiations
    val preferencesRepository = PreferencesRepositoryDataStore(this)
    val preferencesViewModel = PreferencesViewModel(preferencesRepository)
    Log.d("MainActivity", "Setup content")
    this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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

  // Instantiate Park Images flow
  val imageRepository = ImageRepositoryFirestore(firestoreDB, parkRepository, userRepository)
  val imageViewModel = ImageViewModel(imageRepository)

  // Instantiate Google Auth Service
  val token = stringResource(R.string.default_web_client_id)
  val authService = GoogleAuthService(token, Firebase.auth, LocalContext.current)

  // Get the preferences cached parameters
  val loginState by preferencesViewModel.loginState.collectAsState()
  val uid by preferencesViewModel.uid.collectAsState()
  val name by preferencesViewModel.name.collectAsState()
  val score by preferencesViewModel.score.collectAsState()
  preferencesViewModel.getLoginState()
  preferencesViewModel.getUid()
  preferencesViewModel.getName()
  preferencesViewModel.getScore()

  // Ensure start destination and preferences parameters are resolved before displaying the app
  var resolvedStartDestination by remember { mutableStateOf<String?>(null) }
  var resolvedPreferencesParameters by remember { mutableStateOf<Boolean>(false) }

  // Determine start destination and be sure preferences parameters are correctly loaded
  if (loginState == true &&
      ((internetAvailable && !uid.isNullOrEmpty()) ||
          (!internetAvailable && !uid.isNullOrEmpty() && name != null && score != null))) {
    resolvedPreferencesParameters = true
    resolvedStartDestination = Route.MAP
  }
  if (loginState == false) {
    resolvedPreferencesParameters = true
    resolvedStartDestination = Route.AUTH
  }

  // Display splash screen while determining start destination and preferences
  if (resolvedStartDestination == null || !resolvedPreferencesParameters) {
    SplashScreen()
  } else {
    if (loginState == true) {
      if (internetAvailable && !uid.isNullOrEmpty()) {
        Log.d("MainActivity", "Internet available, fetching user $uid from database")
        userViewModel.getUserByUidAndSetAsCurrentUser(uid!!)
      } else {
        Log.d("MainActivity", "Internet not available, loading user $uid from cache")
        val offlineUser =
            User(uid.orEmpty(), name.orEmpty(), "", score ?: 0, emptyList(), "", emptyList())
        userViewModel.setCurrentUser(offlineUser)
      }
    }

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
        imageViewModel,
        preferencesViewModel,
        authService,
        startDestination = resolvedStartDestination!!)
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
    imageViewModel: ImageViewModel,
    preferencesViewModel: PreferencesViewModel,
    authService: GoogleAuthService,
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
        NavHost(navController = navController, startDestination = startDestination) {
          navigation(
              startDestination = Screen.AUTH,
              route = Route.AUTH,
          ) {
            composable(Screen.AUTH) {
              SignInScreen(navigationActions, userViewModel, preferencesViewModel, authService)
            }
          }
          navigation(startDestination = Screen.PROGRESSION, route = Route.PROGRESSION) {
            composable(Screen.PROGRESSION) {
              infoManager.Display(LocalContext.current)
              ProgressScreen(
                  navigationActions,
                  userViewModel,
                  progressionViewModel,
                  workoutViewModel,
                  innerPadding)
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
                  userViewModel,
                  navigationActions,
                  searchQuery,
                  mapCallbackOnMapLoaded,
                  innerPadding,
                  scope,
                  host)
              screenParams?.topAppBarManager?.setActionCallback(
                  TopAppBarManager.TopAppBarAction.SEARCH) {
                    showSearchBar.value = true
                  }
            }
            composable(Screen.PARK_OVERVIEW) {
              infoManager.Display(LocalContext.current)
              ParkOverviewScreen(
                  parkViewModel,
                  innerPadding,
                  navigationActions,
                  eventViewModel,
                  userViewModel,
                  imageViewModel,
                  scope,
                  host)
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
              CustomDialog(
                  showSettingsDialog,
                  dialogType = DialogType.INFO,
                  tag = "Settings",
                  title = "Settings",
                  Content = {
                    SettingsContent(
                        navigationActions,
                        userViewModel,
                        preferencesViewModel,
                        authService,
                        showSettingsDialog)
                  },
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
            ) { activity, isTimeDependent, time, sets, reps ->
              TrainSoloScreen(
                  activity,
                  isTimeDependent,
                  time,
                  sets,
                  reps,
                  workoutViewModel,
                  userViewModel,
                  innerPadding)
            }

            trainComposable(
                route = Screen.TRAIN_COACH,
                content = { activity, isTimeDependent, time, sets, reps ->
                  TrainCoachScreen(
                      activity, isTimeDependent, time, sets, reps, workoutViewModel, innerPadding)
                })
          }

          trainComposable(
              route = Screen.TRAIN_CHALLENGE,
              content = { activity, isTimeDependent, time, sets, reps ->
                TrainChallengeScreen(
                    activity, isTimeDependent, time, sets, reps, workoutViewModel, innerPadding)
              })
          composable(
              route = Route.TRAIN_PARAM,
              arguments =
                  listOf(
                      navArgument("activity") { type = NavType.StringType },
                      navArgument("isTimeDependent") { type = NavType.BoolType },
                      navArgument("type") { type = NavType.StringType })) { backStackEntry ->
                val activity = backStackEntry.arguments?.getString("activity") ?: "defaultActivity"
                val isTimeDependent =
                    backStackEntry.arguments?.getBoolean("isTimeDependent") ?: false
                val type = backStackEntry.arguments?.getString("type") ?: "defaultType"

                // Call TrainParamScreen with the parameters
                TrainParamScreen(navigationActions, activity, isTimeDependent, type)
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
