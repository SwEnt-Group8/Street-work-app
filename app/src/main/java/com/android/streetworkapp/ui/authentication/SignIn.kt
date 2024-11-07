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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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

  // This part of the code handles google sign-in :
  var firebaseUser by remember { mutableStateOf(Firebase.auth.currentUser) }

  val mvvm_user by userViewModel.currentUser.observeAsState()
  val user_friends by userViewModel.friends.observeAsState()

  val token = stringResource(R.string.default_web_client_id)
  val context = LocalContext.current

  // Create an instance of GoogleAuthService (helper class for authentication) :
  val authService = remember { GoogleAuthService(token, Firebase.auth) }

  // Instantiate the launcher for the sign-in process :
  val launcher =
      authService.rememberFirebaseAuthLauncher(
          onAuthComplete = { result ->
            firebaseUser = result.user
            checkAndAddUser(firebaseUser, userViewModel)
            firebaseUser?.let { firebaseUser -> userViewModel.getUserByUid(firebaseUser.uid) }
            Log.d("SignInScreen", "Sign-in successful user : $firebaseUser")
            Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
            Log.d("SignInScreen", "current user : ${firebaseUser!!.displayName}")
            Log.d("SignInScreen", "current user (mvvm) : $mvvm_user")
            Log.d("SignInScreen", "current user friends : $user_friends")
            navigationActions.navigateTo(Screen.MAP)
          },
          onAuthError = {
            firebaseUser = null
            Log.d("SignInScreen", "Sign-in failed : $it")
            Toast.makeText(context, "Login failed! : $it", Toast.LENGTH_LONG).show()
          })

  // Observe the user data to check if the user already exists in the database
  // observeAndSetCurrentUser(user, userViewModel)

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
 * Checks if the user is already in the database, and if not, adds them.
 *
 * @param user The user to check and add.
 * @param userViewModel The [UserViewModel] to use for database operations.
 */
fun checkAndAddUser(user: FirebaseUser?, userViewModel: UserViewModel) {
  Log.d("SignInScreen", "Entered checkAndAddUser")
  if (user == null) return
  // Observe _user for the result of fetchUserByUid
  val observer =
      object : androidx.lifecycle.Observer<User?> {
        override fun onChanged(value: User?) {
          if (value == null) {
            // User doesn't exist, so add them
            Log.d("SignInScreen", "User ${user.displayName} doesn't exist, adding user to database")
            val newUser = User(user.uid, user.displayName!!, user.email!!, 0, emptyList())
            userViewModel.addUser(newUser)
          }
          // Remove the observer after one-time use
          userViewModel.user.removeObserver(this)
        }
      }
  userViewModel.user.observeForever(observer)
  Log.d("SignInScreen", "MVVM - getting user by uid for user ${user.displayName}")
  userViewModel.getUserByUid(user.uid)
  Log.d("SignInScreen", "MVVM - getting friends by uid for user ${user.displayName}")
  userViewModel.getFriendsByUid(user.uid)
}

/**
 * Observes the user data and adds the user if they don't exist.
 *
 * @param user The user to observe and add.
 * @param userViewModel The [UserViewModel] to use for database operations.
 */
fun observeAndSetCurrentUser(user: FirebaseUser?, userViewModel: UserViewModel) {
  Log.d("SignInScreen", "Entered observeAndSetCurrentUser")
  val currentUser = userViewModel.user.value
  user?.let { firebaseUser ->
    if (currentUser == null) {
      // If no existing data, set loggedInUser with default values and add the user
      Log.d(
          "SignInScreen",
          "[observeAndSet] User ${user.displayName} doesn't exist, adding user to database")
      val newUser =
          User(
              uid = firebaseUser.uid,
              username = firebaseUser.displayName ?: "Unknown",
              email = firebaseUser.email ?: "No Email",
              score = 0,
              friends = emptyList())
      userViewModel.addUser(newUser)
      userViewModel.setCurrentUser(newUser)
      Log.d("SignInScreen", "New user added: $newUser")
    } else {
      // Set loggedInUser with existing data
      userViewModel.setCurrentUser(currentUser)
      userViewModel.getFriendsByUid(currentUser.uid)
      Log.d("SignInScreen", "Existing user loaded: $currentUser")
    }
  }
}
