package com.android.streetworkapp.ui.miscellaneous

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

/** Display a splash screen. */
@Composable
fun SplashScreen() {
  Box(
      modifier = Modifier.fillMaxSize().testTag("splashScreen"),
      contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.testTag("splashScreenCircularProgressIndicator"))
      }
}
