package com.android.streetworkapp.ui.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.preferences.PreferencesViewModel
import com.android.streetworkapp.model.progression.ProgressionViewModel
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Route
import com.android.streetworkapp.ui.theme.ColorPalette
import com.android.streetworkapp.ui.utils.CustomDialog
import com.android.streetworkapp.ui.utils.DialogType
import com.android.streetworkapp.utils.GoogleAuthService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun SettingsContent(
    navigationActions: NavigationActions,
    userViewModel: UserViewModel,
    parkViewModel: ParkViewModel,
    eventViewModel: EventViewModel,
    progressionViewModel: ProgressionViewModel,
    workoutViewModel: WorkoutViewModel,
    preferencesViewModel: PreferencesViewModel,
    authService: GoogleAuthService,
    showParentDialog: MutableState<Boolean>
) {

  val currentUser = remember { userViewModel.currentUser.value }
  val showConfirmUserDelete = remember { mutableStateOf(false) }
  var firebaseUser by remember { mutableStateOf(Firebase.auth.currentUser) }
  val context = LocalContext.current

  // Firebase authentication launcher to manage the sign-in process
  val launcher =
      authService.rememberFirebaseAuthLauncher(
          onAuthComplete = { result -> firebaseUser = result.user },
          onAuthError = {
            firebaseUser = null
            Log.d("Settings", "Sign-in failed : $it")
          })

  Column(
      modifier = Modifier.fillMaxWidth().testTag("SettingsContent"),
      verticalArrangement = Arrangement.spacedBy(4.dp),
      horizontalAlignment = Alignment.CenterHorizontally) {

        // Settings content
        if (currentUser == null) {
          Text(
              text = context.getString(R.string.SettingsNullUserContent),
              modifier = Modifier.testTag("NullUserSettingsContent"))
        } else {
          Text(
              context.getString(R.string.SettingsConnectionContent, currentUser.username),
              modifier = Modifier.padding(bottom = 16.dp).testTag("ConnectionSettingsContent"))

          Button(
              onClick = {
                showParentDialog.value = false
                logout(authService, userViewModel, preferencesViewModel)
                Toast.makeText(
                        context, context.getString(R.string.LogoutSuccess), Toast.LENGTH_SHORT)
                    .show()
                navigationActions.navigateTo(Route.AUTH)
              },
              colors = ColorPalette.BUTTON_COLOR,
              modifier = Modifier.testTag("LogOutButton")) {
                Text(context.getString(R.string.LogoutTitle))
              }

          Button(
              onClick = { showConfirmUserDelete.value = true },
              colors = ColorPalette.BUTTON_COLOR,
              modifier = Modifier.testTag("DeleteAccountButton")) {
                Text(context.getString(R.string.DeleteAccountTitle))
              }
        }
      }

  CustomDialog(
      showConfirmUserDelete,
      DialogType.CONFIRM,
      tag = "deleteAccount",
      title = context.getString(R.string.DeleteAccountConfirmationTitle),
      Content = { Text(context.getString(R.string.DeleteAccountConfirmationContent)) },
      onSubmit = {
        // If the Firebase user is not saved make it sign in
        if (firebaseUser == null) {
          authService.launchSignIn(launcher)
        }

        val deletionSucceed =
            deleteAccount(
                authService,
                userViewModel,
                parkViewModel,
                eventViewModel,
                progressionViewModel,
                workoutViewModel)

        if (deletionSucceed) {
          logout(authService, userViewModel, preferencesViewModel)
          Toast.makeText(
                  context, context.getString(R.string.DeleteAccountSuccess), Toast.LENGTH_SHORT)
              .show()
          showParentDialog.value = false
          navigationActions.navigateTo(Route.AUTH)
        } else {
          Toast.makeText(
                  context, context.getString(R.string.DeleteAccountFailure), Toast.LENGTH_SHORT)
              .show()
          showParentDialog.value = false
        }
      })
}

/**
 * Logs out the user by signing out from Google and the app.
 *
 * @param authService the Google authentication service
 * @param userViewModel the user viewmodel
 * @param preferencesViewModel the preferences viewmodel
 */
fun logout(
    authService: GoogleAuthService,
    userViewModel: UserViewModel,
    preferencesViewModel: PreferencesViewModel
) {
  // Sign out and revoke access from Google Service (disable auto-login)
  authService.signOut()
  authService.revokeAccess()

  // Clear user viewmodel data
  userViewModel.setUser(null)
  userViewModel.setCurrentUser(null)

  // Clear preferences parameters
  preferencesViewModel.setLoginState(false)
  preferencesViewModel.setUid("")
  preferencesViewModel.setName("")
  preferencesViewModel.setScore(0)
}

/**
 * Deletes the user account and all associated data.
 *
 * @param authService the Google authentication service
 * @param userViewModel the user viewmodel
 * @param parkViewModel the park viewmodel
 * @param eventViewModel the event viewmodel
 * @param progressionViewModel the progression viewmodel
 * @param workoutViewModel the workout viewmodel
 */
fun deleteAccount(
    authService: GoogleAuthService,
    userViewModel: UserViewModel,
    parkViewModel: ParkViewModel,
    eventViewModel: EventViewModel,
    progressionViewModel: ProgressionViewModel,
    workoutViewModel: WorkoutViewModel
): Boolean {
  // Get the current UID and abort if it is empty
  val currentUserUid = userViewModel.currentUser.value?.uid ?: ""
  if (currentUserUid.isEmpty()) {
    Log.d("Settings", "User UID is empty, cannot delete account.")
    return false
  }

  // Delete the Firebase user authentication and abort if the user is null
  if (authService.getCurrentUser() == null) {
    Log.d("Settings", "Firebase user is null, cannot delete account.")
    return false
  }
  authService.deleteAuthUser()

  // Delete the user data from the User viewmodel
  userViewModel.deleteUserByUid(currentUserUid)
  userViewModel.removeUserFromAllFriendsLists(currentUserUid)

  // Delete the user data from the other viewmodels
  parkViewModel.deleteRatingFromAllParks(currentUserUid)
  eventViewModel.removeParticipantFromAllEvents(currentUserUid)
  progressionViewModel.deleteProgressionByUid(currentUserUid)
  workoutViewModel.deleteWorkoutDataByUid(currentUserUid)

  return true
}
