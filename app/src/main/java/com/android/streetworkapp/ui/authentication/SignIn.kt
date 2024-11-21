package com.android.streetworkapp.ui.authentication

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.ConnectWithoutContact
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.ui.theme.ColorPalette
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
          Image(
              painter = painterResource(id = R.drawable.app_logo),
              contentDescription = "App Logo",
              modifier = Modifier.height(200.dp).width(200.dp).testTag("appLogo"))

          Spacer(modifier = Modifier.height(26.dp).testTag("loginScreenFirstSpacer"))

          Text(
              text = "Welcome to Street WorkApp!",
              style =
                  TextStyle(
                      fontSize = 26.sp,
                      lineHeight = 26.sp,
                      fontWeight = FontWeight(500),
                      color = Color(0xFF000000),
                      textAlign = TextAlign.Center,
                  ),
              modifier =
                  Modifier.height(32.dp)
                      .aspectRatio(308f / 24f)
                      .fillMaxWidth()
                      .testTag("loginTitle"))

          Spacer(modifier = Modifier.height(26.dp).testTag("loginScreenSecondSpacer"))

          IconAndTextRow(
              imageVector = Icons.Filled.LocationOn,
              contentDescription = "Location marker icon",
              text = "Find nearby parks and events to participate in or create",
              testName = "loginScreenFirstRow")

          IconAndTextRow(
              imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
              contentDescription = "Running person icon",
              text = "Track your activities and learn new skills",
              testName = "loginScreenSecondRow")

          IconAndTextRow(
              imageVector = Icons.Filled.ConnectWithoutContact,
              contentDescription = "People connecting icon",
              text = "Make new friends, train together and share activities",
              testName = "loginScreenThirdRow")

          Spacer(modifier = Modifier.height(26.dp).testTag("loginScreenThirdSpacer"))

          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .height(96.dp)
                      .background(ColorPalette.INTERACTION_COLOR_DARK)
                      .testTag("loginScreenGoogleAuthButtonContainer"),
              contentAlignment = Alignment.Center) {
                GoogleAuthButton(authService, context, launcher)
              }
        }
  }
}

/**
 * A composable function that displays an icon and a text in a row with specified padding and
 * alignment.
 *
 * @param imageVector The vector image to be used as the icon.
 * @param contentDescription The content description for the icon, used for accessibility.
 * @param text The text to be displayed next to the icon.
 * @param testName The test tag name to be used for testing purposes.
 */
@Composable
fun IconAndTextRow(
    imageVector: ImageVector,
    contentDescription: String,
    text: String,
    testName: String
) {
  Row(
      modifier = Modifier.padding(horizontal = 25.dp, vertical = 8.dp).testTag(testName),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.weight(0.7f).aspectRatio(26.4f / 33f).testTag("${testName}Icon"),
            tint = ColorPalette.INTERACTION_COLOR_DARK)
        Spacer(modifier = Modifier.width(16.dp).testTag("${testName}Spacer"))
        Text(
            text = text,
            style =
                TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.W500,
                    color = Color(0xFF000000)),
            modifier = Modifier.weight(5f).aspectRatio(269f / 44f).testTag("${testName}Text"))
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
