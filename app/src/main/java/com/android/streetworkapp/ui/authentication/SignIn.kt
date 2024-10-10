package com.android.streetworkapp.ui.authentication

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.android.streetworkapp.utils.GoogleAuthService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@SuppressLint("RememberReturnType")
@Composable
fun SignInScreen() {

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
          },
          onAuthError = { user = null })

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

          // Google Sign-in button
          Button(
              onClick = {
                Log.d("SignInScreen", "Start sign-in")
                authService.launchSignIn(context, launcher)
              },
              modifier =
                  Modifier.width(250.dp)
                      .height(40.dp)
                      .border(
                          width = 1.dp,
                          color = Color(0xFFDADCE0),
                          shape = RoundedCornerShape(20.dp))
                      .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(20.dp))
                      .testTag("loginButton"),
              colors =
                  ButtonDefaults.buttonColors(
                      containerColor = Color.White, // Custom background color
                      contentColor = Color(0xFF3C4043) // Custom text color
                      )) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize().testTag("loginButtonRowContainer")) {
                      Image(
                          painter = painterResource(id = R.drawable.google_logo),
                          contentDescription = "Google Logo",
                          contentScale = ContentScale.Fit,
                          modifier =
                              Modifier.size(24.dp).padding(end = 8.dp).testTag("loginButtonIcon"))
                      Text(
                          modifier = Modifier.testTag("loginButtonText"),
                          text = "Sign in with Google",
                          style =
                              TextStyle(
                                  fontSize = 14.sp,
                                  lineHeight = 17.sp,
                                  fontWeight = FontWeight(500),
                                  color = Color(0xFF3C4043),
                                  textAlign = TextAlign.Center,
                                  letterSpacing = 0.25.sp,
                              ))
                    }
              }
        }
  }
}
