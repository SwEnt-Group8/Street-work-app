@file:Suppress("DEPRECATION")

package com.android.streetworkapp.utils

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GoogleAuthService(
    private val token: String,
    private val auth: FirebaseAuth,
    private val context: Context
) : AuthService {

  private val mGoogleSignInClient: GoogleSignInClient

  init {
    val gso =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(token)
            .requestEmail()
            .build()
    mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
  }

  override fun launchSignIn(
      context: android.content.Context,
      launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
  ) {
    val gso =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(token)
            .requestEmail()
            .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    launcher.launch(googleSignInClient.signInIntent)
  }

  override fun signOut() {
    auth.signOut()
  }

  override fun getCurrentUser(): FirebaseUser? {
    return auth.currentUser
  }

  @Composable
  fun rememberFirebaseAuthLauncher(
      onAuthComplete: (AuthResult) -> Unit,
      onAuthError: (ApiException) -> Unit
  ): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
      val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
      try {
        val account = task.getResult(ApiException::class.java)!!
        val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
        scope.launch {
          val authResult = Firebase.auth.signInWithCredential(credential).await()
          onAuthComplete(authResult)
        }
      } catch (e: ApiException) {
        onAuthError(e)
      }
    }
  }
}
