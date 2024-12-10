package com.android.streetworkapp.utils

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import com.google.firebase.auth.FirebaseUser

// Interface to handle google sign-in :
interface AuthService {
  // Trigger the sign-in process :
  fun launchSignIn(
      launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
  )

  // Handle the sign-out process
  fun signOut()

  // Returns the current signed-in user (null if none)
  fun getCurrentUser(): FirebaseUser?
}
