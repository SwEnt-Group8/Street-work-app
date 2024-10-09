package com.android.streetworkapp

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun TinyComposable() {
  // This is a tiny composable
  Text("Hello, World!", modifier = Modifier.testTag("Hello, World!")) // Set the test tag here
}
