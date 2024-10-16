package com.android.streetworkapp.ui.authentication

import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.streetworkapp.ui.theme.*
import com.android.streetworkapp.utils.GoogleAuthService

@Composable
fun GoogleAuthButton(
    authService: GoogleAuthService,
    context: android.content.Context,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {

  Button(
      onClick = {
        Log.d("SignInScreen", "Start sign-in")
        authService.launchSignIn(context, launcher)
      },
      modifier =
          Modifier.width(250.dp)
              .height(40.dp)
              .border(width = 1.dp, color = LightGray, shape = RoundedCornerShape(20.dp))
              .background(color = White, shape = RoundedCornerShape(20.dp))
              .testTag("loginButton"),
      colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = DarkGray)) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize().testTag("loginButtonRowContainer")) {
              Image(
                  painter = painterResource(id = R.drawable.google_logo),
                  contentDescription = "Google Logo",
                  contentScale = ContentScale.Fit,
                  modifier = Modifier.size(24.dp).padding(end = 8.dp).testTag("loginButtonIcon"))
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
