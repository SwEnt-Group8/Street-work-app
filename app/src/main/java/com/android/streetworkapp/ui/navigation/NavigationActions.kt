package com.android.streetworkapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Place
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController

object Route {
  const val AUTH = "Auth"
  const val MAP = "Map"
  const val PROFILE = "Profile"
  const val UNK = "TBD" // TODO: not yet defined
}

object Screen {
  const val AUTH = "Auth Screen"
  const val MAP = "Map Screen"
  const val PROFILE = "Profile Screen"
  const val ADD_FRIEND = "AddFriend Screen"
  const val PARK_OVERVIEW = "Park Overview Screen"
  const val ADD_EVENT = "Add Event Screen"
  const val UNK = "TBD Screen" // TODO: not yet defined
}

/**
 * Represents a top-level destination in the app's navigation.
 *
 * @property route The route associated with this destination.
 * @property icon The icon to display for this destination.
 * @property textId an identifier for this destination.
 */
data class TopLevelDestination(val route: String, val icon: ImageVector, val textId: String)

// all data classes here are reachable through user actions (i.e not auth)
object TopLevelDestinations {
  val MAP = TopLevelDestination(route = Route.MAP, icon = Icons.Outlined.Place, textId = "Map")
  val PROFILE =
      TopLevelDestination(
          route = Route.PROFILE, icon = Icons.Outlined.AccountCircle, textId = "Profile")
}

val LIST_TOP_LEVEL_DESTINATION = listOf(TopLevelDestinations.MAP, TopLevelDestinations.PROFILE)

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
}
