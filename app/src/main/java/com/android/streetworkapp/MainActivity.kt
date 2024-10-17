package com.android.streetworkapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Route
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.ui.profile.AddFriendScreen
import com.android.streetworkapp.ui.profile.ProfileScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d("MainActivity", "Setup content")
    setContent(parent = null) {}
  }
}

// the testInvokation is super ugly but I have NOT found any other way to test the navigation from a
// ui perspective since we don't use fragments
@Composable
fun StreetWorkAppMain(testInvokation: NavigationActions.() -> Unit = {}) {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)
  // initialise the user viewmodel
  val userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory)

  NavHost(
      navController = navController,
      startDestination = Route.AUTH) { // TODO: handle start destination based on signIn logic
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
          composable(Screen.MAP) {}
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

  navigationActions.apply(testInvokation)
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
