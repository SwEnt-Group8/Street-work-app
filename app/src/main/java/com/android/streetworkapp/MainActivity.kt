package com.android.streetworkapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.streetworkapp.model.user.UserRepositoryFirestore
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.authentication.SignInScreen
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Route
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.ui.profile.ProfileScreen
import com.google.firebase.firestore.FirebaseFirestore

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
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  // Instantiate fire store database and associated user repository :
  val firestoreDB = FirebaseFirestore.getInstance()
  val userRepository = UserRepositoryFirestore(firestoreDB)
  val userViewModel = UserViewModel(userRepository)

  NavHost(
      navController = navController,
      startDestination = Route.AUTH) { // TODO: handle start destination based on signIn logic
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
          composable(Screen.MAP) {}
        }

        navigation(
            startDestination = Screen.PROFILE,
            route = Route.PROFILE,
        ) {
          composable(Screen.PROFILE) { ProfileScreen(navigationActions) }
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
