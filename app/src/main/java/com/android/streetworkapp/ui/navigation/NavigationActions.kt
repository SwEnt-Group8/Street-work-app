package com.android.streetworkapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Place
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.android.sample.R

object Route {
  const val AUTH = "Auth"
  const val MAP = "Map"
  const val PROFILE = "Profile"
  const val PROGRESSION = "Progression"
  const val TRAIN_HUB = "TrainHub"
  const val TRAIN_PARAM = "TrainParam/{activity}/{isTimeDependent}/{type}"
  const val UNK = "TBD" // TODO: not yet defined
}

object Screen {
  const val AUTH = "Auth Screen"
  const val MAP = "Map Screen"
  const val PROFILE = "Profile Screen"
  const val ADD_FRIEND = "AddFriend Screen"
  const val PARK_OVERVIEW = "Park Overview Screen"
  const val ADD_EVENT = "Add Event Screen"
  const val EVENT_OVERVIEW = "Event Overview Screen"
  const val PROGRESSION = "Progression Screen"
  const val TRAIN_HUB = "Train Hub Screen"
  const val TRAIN_SOLO =
      "TrainSolo/{activity}/{isTimeDependent}?time={time}&sets={sets}&reps={reps}"
  const val TRAIN_COACH =
      "TrainCoach/{activity}/{isTimeDependent}?time={time}&sets={sets}&reps={reps}"
  const val TRAIN_CHALLENGE =
      "TrainChallenge/{activity}/{isTimeDependent}?time={time}&sets={sets}&reps={reps}"
  const val TRAIN_PARAM = "TrainParam/{activity}/{isTimeDependent}/{type}"
  const val UNK = "TBD Screen" // TODO: not yet defined
  const val TUTO_EVENT = "Tutorial event Screen"
}

data class ScreenParams(
    val screenName: String,
    val isBottomBarVisible: Boolean = true,
    val bottomBarType: BottomNavigationMenuType = BottomNavigationMenuType.DEFAULT,
    val isTopBarVisible: Boolean = true,
    val topAppBarManager: TopAppBarManager?,
    val hasSearchBar: Boolean = false
) {
  companion object {
    val AUTH =
        ScreenParams(
            Screen.AUTH,
            isBottomBarVisible = false,
            BottomNavigationMenuType.NONE,
            isTopBarVisible = false,
            null)
    val MAP =
        ScreenParams(
            Screen.MAP,
            topAppBarManager =
                TopAppBarManager(
                    "Map",
                    actions =
                        listOf(
                            TopAppBarManager.TopAppBarAction.SEARCH,
                            TopAppBarManager.TopAppBarAction.INFO)),
            hasSearchBar = true)
    val PROFILE =
        ScreenParams(
            Screen.PROFILE,
            topAppBarManager =
                TopAppBarManager(
                    "My Profile",
                    actions =
                        listOf(
                            TopAppBarManager.TopAppBarAction.SETTINGS,
                            TopAppBarManager.TopAppBarAction.INFO)))
    val ADD_FRIEND =
        ScreenParams(
            Screen.ADD_FRIEND,
            topAppBarManager =
                TopAppBarManager(
                    "Add a new Friend",
                    hasNavigationIcon = true,
                    navigationIcon = TopAppBarManager.DEFAULT_TOP_APP_BAR_NAVIGATION_ICON,
                    actions = listOf(TopAppBarManager.TopAppBarAction.INFO)))
    val PARK_OVERVIEW =
        ScreenParams(
            Screen.PARK_OVERVIEW,
            topAppBarManager =
                TopAppBarManager(
                    "Park Overview",
                    hasNavigationIcon = true,
                    navigationIcon = TopAppBarManager.DEFAULT_TOP_APP_BAR_NAVIGATION_ICON,
                    actions = listOf(TopAppBarManager.TopAppBarAction.INFO)))
    val ADD_EVENT =
        ScreenParams(
            Screen.ADD_EVENT,
            topAppBarManager =
                TopAppBarManager(
                    "Event Creation",
                    hasNavigationIcon = true,
                    navigationIcon = TopAppBarManager.DEFAULT_TOP_APP_BAR_NAVIGATION_ICON,
                    actions = listOf(TopAppBarManager.TopAppBarAction.INFO)))
    val EVENT_OVERVIEW =
        ScreenParams(
            Screen.EVENT_OVERVIEW,
            bottomBarType = BottomNavigationMenuType.EVENT_OVERVIEW,
            topAppBarManager =
                TopAppBarManager(
                    "Event Overview",
                    hasNavigationIcon = true,
                    navigationIcon = TopAppBarManager.DEFAULT_TOP_APP_BAR_NAVIGATION_ICON,
                    actions = listOf(TopAppBarManager.TopAppBarAction.INFO)))
    val PROGRESSION =
        ScreenParams(
            Screen.PROGRESSION,
            topAppBarManager =
                TopAppBarManager(
                    "My Progress",
                    hasNavigationIcon = false,
                    actions = listOf(TopAppBarManager.TopAppBarAction.INFO)))
    val TRAIN_HUB =
        ScreenParams(
            screenName = Screen.TRAIN_HUB,
            topAppBarManager = TopAppBarManager("Training hub", hasNavigationIcon = true))
    val TRAIN_SOLO =
        ScreenParams(
            screenName = Screen.TRAIN_SOLO,
            topAppBarManager = TopAppBarManager("Train Solo", hasNavigationIcon = true))
    val TRAIN_COACH =
        ScreenParams(
            screenName = Screen.TRAIN_COACH,
            topAppBarManager =
                TopAppBarManager("Train with a friends as coach", hasNavigationIcon = true))
    val TRAIN_CHALLENGE =
        ScreenParams(
            screenName = Screen.TRAIN_CHALLENGE,
            isBottomBarVisible = true,
            bottomBarType = BottomNavigationMenuType.DEFAULT,
            isTopBarVisible = true,
            TopAppBarManager("Challenge with your friend", hasNavigationIcon = true))
    val TRAIN_PARAM =
        ScreenParams(
            screenName = Screen.TRAIN_PARAM,
            isBottomBarVisible = true,
            bottomBarType = BottomNavigationMenuType.DEFAULT,
            isTopBarVisible = true,
            TopAppBarManager("Parameters", hasNavigationIcon = true))
    val TUTO_EVENT =
        ScreenParams(
            Screen.TUTO_EVENT,
            isBottomBarVisible = false,
            BottomNavigationMenuType.NONE,
            isTopBarVisible = false,
            null)
  } // TODO UPDATE AND REMOVE THE UNNECESSARY DEFAULT PARAMS
}

val LIST_OF_SCREENS =
    listOf(
        ScreenParams.AUTH,
        ScreenParams.MAP,
        ScreenParams.PROFILE,
        ScreenParams.ADD_FRIEND,
        ScreenParams.PARK_OVERVIEW,
        ScreenParams.ADD_EVENT,
        ScreenParams.EVENT_OVERVIEW,
        ScreenParams.PROGRESSION,
        ScreenParams.TRAIN_HUB,
        ScreenParams.TRAIN_SOLO,
        ScreenParams.TRAIN_COACH,
        ScreenParams.TRAIN_CHALLENGE,
        ScreenParams.TRAIN_PARAM,
        ScreenParams.TUTO_EVENT)

/**
 * Represents a top-level destination in the app's navigation.
 *
 * @property route The route associated with this destination.
 * @property icon The icon to display for this destination.
 * @property textId an identifier for the destination.
 */
data class TopLevelDestination(
    val route: String,
    val icon: ImageVector?,
    val imagePainter: Int?,
    val textId: String
)

// all data classes here are reachable through user actions (i.e not auth)
object TopLevelDestinations {
  val MAP =
      TopLevelDestination(
          route = Route.MAP, icon = Icons.Outlined.Place, imagePainter = null, textId = "Map")
  val PROFILE =
      TopLevelDestination(
          route = Route.PROFILE,
          icon = Icons.Outlined.AccountCircle,
          imagePainter = null,
          textId = "Profile")
  val PROGRESSION =
      TopLevelDestination(
          route = Route.PROGRESSION,
          icon = null,
          imagePainter = R.drawable.trophy_24px,
          textId = "Progression")
  val TRAIN_HUB =
      TopLevelDestination(
          route = Route.TRAIN_HUB,
          icon = null,
          imagePainter = R.drawable.training,
          textId = "Train Hub")
}

val LIST_TOP_LEVEL_DESTINATION =
    listOf(
        TopLevelDestinations.PROGRESSION,
        TopLevelDestinations.MAP,
        TopLevelDestinations.PROFILE,
        TopLevelDestinations.TRAIN_HUB)

fun getScreens(): Screen {
  return Screen
}

data class TrainNavigationParams(
    val activity: String,
    val isTimeDependent: Boolean,
    val time: Int? = 0,
    val sets: Int? = null,
    val reps: Int? = null
)

open class NavigationActions(
    private val navController: NavHostController,
) {

  /**
   * Navigate to the specified [TopLevelDestination]
   *
   * @param destination The top level destination to navigate to.
   *
   * Clear the back stack when navigating to a new destination.
   */
  open fun navigateTo(
      destination: TopLevelDestination
  ) { // https://developer.android.com/guide/navigation/backstack
    try {
      navController.navigate(destination.route) {
        popUpTo(navController.graph.startDestinationId) {
          saveState = true
          inclusive = true
        }

        // don't re-add the destination if we're already there
        launchSingleTop = true

        // restore state when reselecting a previously selected item
        if (destination.route != Route.AUTH) restoreState = true
      }
    } catch (
        e: IllegalArgumentException) { // we're here if we get an invalid route as param (one not
      // registered in our navigation)
      this.navigateTo(TopLevelDestinations.MAP) // fallback to default Route
    }
  }

  /**
   * Navigate to the specified screen.
   *
   * @param screen The screen to navigate to
   */
  open fun navigateTo(screen: String) {
    navController.navigate(screen)
  }

  /** Navigate back to the previous screen. */
  open fun goBack() {
    navController.popBackStack()
  }

  /**
   * Get the current route of the navigation controller.
   *
   * @return The current route
   */
  open fun currentRoute(): String {
    return navController.currentDestination?.route.orEmpty()
  }

  /** Will update the currentScreenName to the screen name on each destination change */
  open fun registerStringListenerOnDestinationChange(currentScreenName: MutableState<String?>) {
    navController.addOnDestinationChangedListener { _, dest, _ ->
      currentScreenName.value = dest.route
    }
  }

  /**
   * Navigate to the TrainSolo screen.
   *
   * @param params The parameters for the navigation.
   */
  fun navigateToSoloScreen(params: TrainNavigationParams) {
    val (activity, isTimeDependent, time, sets, reps) = params
    navController.navigate(buildRoute("TrainSolo", activity, isTimeDependent, time, sets, reps))
  }

  /**
   * Navigate to the TrainCoach screen.
   *
   * @param params The parameters for the navigation.
   */
  fun navigateToCoachScreen(params: TrainNavigationParams) {
    val (activity, isTimeDependent, time, sets, reps) = params
    navController.navigate(buildRoute("TrainCoach", activity, isTimeDependent, time, sets, reps))
  }

  /**
   * Navigate to the TrainChallenge screen.
   *
   * @param params The parameters for the navigation.
   */
  fun navigateToChallengeScreen(params: TrainNavigationParams) {
    val (activity, isTimeDependent, time, sets, reps) = params
    navController.navigate(
        buildRoute("TrainChallenge", activity, isTimeDependent, time, sets, reps))
  }

  /**
   * Build a route for a training screen.
   *
   * @param baseRoute The base route for the training screen.
   * @param activity The activity to train.
   * @param isTimeDependent Whether the activity is time dependent.
   * @param time The time to train.
   * @param sets The number of sets to train.
   * @param reps The number of reps to train.
   * @return The built route.
   */
  internal fun buildRoute(
      baseRoute: String,
      activity: String,
      isTimeDependent: Boolean,
      time: Int?,
      sets: Int?,
      reps: Int?
  ): String {
    val queryParams = mutableListOf<String>()

    // Add query parameters only if they exist
    time?.let { queryParams.add("time=$it") }
    sets?.let { queryParams.add("sets=$it") }
    reps?.let { queryParams.add("reps=$it") }

    // Construct the query string
    val queryString = if (queryParams.isNotEmpty()) "?${queryParams.joinToString("&")}" else ""

    // Build the final route
    return "$baseRoute/$activity/$isTimeDependent$queryString"
  }

  /**
   * Navigate to the TrainParam screen.
   *
   * @param activity The activity to train.
   * @param isTimeDependent Whether the activity is time dependent.
   * @param type The type of training.
   */
  fun navigateToTrainParam(activity: String, isTimeDependent: Boolean, type: String) {
    val route = "TrainParam/$activity/$isTimeDependent/$type"
    navController.navigate(route)
  }
}
