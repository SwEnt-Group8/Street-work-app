package com.android.streetworkapp.ui.authentication

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.utils.GoogleAuthService
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun SignInScreen(navigationActions: NavigationActions, userViewModel: UserViewModel) {

  val user by userViewModel.user.collectAsState()

  // This part of the code handles google sign-in :
  var firebaseUser by remember { mutableStateOf(Firebase.auth.currentUser) }

  val token = stringResource(R.string.default_web_client_id)
  val context = LocalContext.current

  // Create an instance of GoogleAuthService (helper class for authentication) :
  val authService = remember { GoogleAuthService(token, Firebase.auth) }

  // Instantiate the launcher for the sign-in process :
  val launcher =
      authService.rememberFirebaseAuthLauncher(
          onAuthComplete = { result ->
            firebaseUser = result.user
            firebaseUser?.let { firebaseUser ->
              userViewModel.getOrAddUserByUid(
                  firebaseUser.uid, createNewUserFromFirebaseUser(firebaseUser))
            }
          },
          onAuthError = {
            firebaseUser = null
            Log.d("SignInScreen", "Sign-in failed : $it")
            Toast.makeText(context, "Login failed! : $it", Toast.LENGTH_LONG).show()
          })

  // Wait for the user to be fetched from database before setting it
  // as the current user and navigating to the map screen
  LaunchedEffect(user) {
    user?.let {
      userViewModel.setCurrentUser(it)
      Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
      navigationActions.navigateTo(Screen.MAP)
    }
  }

  Box(modifier = Modifier.fillMaxSize().testTag("loginScreenBoxContainer")) {

    // Centralized content
    Column(
        modifier = Modifier.fillMaxSize().testTag("loginScreenColumnContainer"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
          // Welcome text in the center
          Text(
              text = "Welcome to the Street Work'App",
              style = TextStyle(fontSize = 24.sp),
              modifier = Modifier.testTag("loginTitle"))

          Spacer(modifier = Modifier.height(64.dp).testTag("loginScreenSpacer"))

          GoogleAuthButton(authService, context, launcher)
        }
  }
}

/**
 * Creates a new [User] object from the provided [FirebaseUser].
 *
 * @param firebaseUser The FirebaseUser object to create the User object from.
 * @return The User object created from the FirebaseUser.
 */
fun createNewUserFromFirebaseUser(firebaseUser: FirebaseUser): User {
  require(firebaseUser.uid.isNotEmpty()) { "UID must not be empty" }
  return User(
      uid = firebaseUser.uid,
      username = firebaseUser.displayName ?: "",
      email = firebaseUser.email ?: "",
      score = 0,
      friends = emptyList(),
      picture = "")
}
