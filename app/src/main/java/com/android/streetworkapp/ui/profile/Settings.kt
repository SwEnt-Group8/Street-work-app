package com.android.streetworkapp.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.streetworkapp.model.preferences.PreferencesViewModel
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.theme.ColorPalette
import com.android.streetworkapp.ui.utils.CustomDialog
import com.android.streetworkapp.ui.utils.DialogType
import com.android.streetworkapp.utils.GoogleAuthService

@Composable
fun SettingsContent(
    navigationActions: NavigationActions,
    userViewModel: UserViewModel,
    authService: GoogleAuthService,
    showParentDialog: MutableState<Boolean>
) {

  val currentUser = remember { userViewModel.currentUser.value }
  val showConfirmUserDelete = remember { mutableStateOf(false) }
  val context = LocalContext.current

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
                Toast.makeText(context, "Not yet implemented", Toast.LENGTH_SHORT).show()
              },
              colors = ColorPalette.BUTTON_COLOR,
              modifier = Modifier.testTag("LogOutButton")) {
                Text("Log-out")
              }

          Button(
              onClick = { showConfirmUserDelete.value = true },
              colors = ColorPalette.BUTTON_COLOR,
              modifier = Modifier.testTag("DeleteAccountButton")) {
                Text("Delete account")
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
        // Friends - remove user from friends list of all friends.
        // Parks - remove user ratings for parks.
        // Parks - remove user in participants of each event.
        // Progression - delete user progression
        // userViewModel.deleteUser(currentUser)

        Toast.makeText(context, "Not yet implemented", Toast.LENGTH_SHORT).show()

        showParentDialog.value = false
        // navigationActions.navigateTo(Screen.AUTH)
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
  authService.signOut()
  userViewModel.setCurrentUser(null)
  preferencesViewModel.setLoginState(false)
  preferencesViewModel.setUid("")
  preferencesViewModel.setName("")
  preferencesViewModel.setScore(0)
}
