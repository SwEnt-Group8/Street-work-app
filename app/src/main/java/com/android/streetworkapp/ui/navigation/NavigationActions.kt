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
  const val UNK = "TBD Screen" // TODO: not yet defined
}

data class ScreenParams(
    val screenName: String,
    val isBottomBarVisible: Boolean,
    val bottomBarType: BottomNavigationMenuType,
    val isTopBarVisible: Boolean,
    val topAppBarManager: TopAppBarManager?
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
            isBottomBarVisible = true,
            BottomNavigationMenuType.DEFAULT,
            isTopBarVisible = true,
            TopAppBarManager("Map"))
    val PROFILE =
        ScreenParams(
            Screen.PROFILE,
            isBottomBarVisible = true,
            BottomNavigationMenuType.DEFAULT,
            isTopBarVisible = true,
            TopAppBarManager("My Profile"))
    val ADD_FRIEND =
        ScreenParams(
            Screen.ADD_FRIEND,
            isBottomBarVisible = true,
            BottomNavigationMenuType.DEFAULT,
            isTopBarVisible = true,
            TopAppBarManager(
                "Add a new Friend",
                hasNavigationIcon = true,
                navigationIcon = TopAppBarManager.DEFAULT_TOP_APP_BAR_NAVIGATION_ICON))
    val PARK_OVERVIEW =
        ScreenParams(
            Screen.PARK_OVERVIEW,
            isBottomBarVisible = true,
            BottomNavigationMenuType.DEFAULT,
            isTopBarVisible = true,
            TopAppBarManager(
                "Park Overview",
                hasNavigationIcon = true,
                navigationIcon = TopAppBarManager.DEFAULT_TOP_APP_BAR_NAVIGATION_ICON))
    val ADD_EVENT =
        ScreenParams(
            Screen.ADD_EVENT,
            isBottomBarVisible = true,
            BottomNavigationMenuType.DEFAULT,
            isTopBarVisible = true,
            TopAppBarManager(
                "Event Creation",
                hasNavigationIcon = true,
                navigationIcon = TopAppBarManager.DEFAULT_TOP_APP_BAR_NAVIGATION_ICON))
    val EVENT_OVERVIEW =
        ScreenParams(
            Screen.EVENT_OVERVIEW,
            isBottomBarVisible = false,
            BottomNavigationMenuType.NONE,
            isTopBarVisible = true,
            TopAppBarManager(
                "Event Overview",
                hasNavigationIcon = true,
                navigationIcon = TopAppBarManager.DEFAULT_TOP_APP_BAR_NAVIGATION_ICON))
    val PROGRESSION =
        ScreenParams(
            Screen.PROGRESSION,
            isBottomBarVisible = true,
            BottomNavigationMenuType.DEFAULT,
            isTopBarVisible = true,
            TopAppBarManager("My Progress", hasNavigationIcon = false))
  }
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
        ScreenParams.PROGRESSION)

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
}

val LIST_TOP_LEVEL_DESTINATION =
    listOf(TopLevelDestinations.PROGRESSION, TopLevelDestinations.MAP, TopLevelDestinations.PROFILE)

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
}
