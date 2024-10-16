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
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.utils.GoogleAuthService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun SignInScreen(navigationActions: NavigationActions) {

  // This part of the code handles google sign-in :
  var user by remember { mutableStateOf(Firebase.auth.currentUser) }

  val token = stringResource(R.string.default_web_client_id)
  val context = LocalContext.current

  // Create an instance of GoogleAuthService (helper class for authentication) :
  val authService = remember { GoogleAuthService(token, Firebase.auth) }

  // Instantiate the launcher for the sign-in process :
  val launcher =
      authService.rememberFirebaseAuthLauncher(
          onAuthComplete = { result ->
            user = result.user
            Log.d("SignInScreen", "Sign-in successful user : $user")
            Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
            navigationActions.navigateTo(Screen.MAP)
          },
          onAuthError = {
            user = null
            Log.d("SignInScreen", "Sign-in failed")
            Toast.makeText(context, "Login failed!", Toast.LENGTH_LONG).show()
          })

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
/*
   Tried to abstract even more by moving launcher instantiation away in a function in the same file
   but it was not possible due to user argument not being modifiable in a function,
   even as a mutableStateOf. Potential improvement for later.
*/

/*
@Composable
fun instantiateAuthLauncher(authService: GoogleAuthService,
    onAuthSuccess:() -> Unit, onAuthError:() -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
  return authService.rememberFirebaseAuthLauncher(
      onAuthComplete = { onAuthSuccess()
      },
      onAuthError = { onAuthError() })
}
*/
