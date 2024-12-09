package com.android.streetworkapp.ui.profile

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.R
import com.android.streetworkapp.model.preferences.PreferencesRepository
import com.android.streetworkapp.model.preferences.PreferencesViewModel
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Route
import com.android.streetworkapp.utils.GoogleAuthService
import com.google.firebase.auth.FirebaseAuth
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.RETURNS_DEFAULTS
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class SettingsTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val navigationActions = mock(NavigationActions::class.java)
  private val userRepository = mock(UserRepository::class.java)
  private val userViewModel = UserViewModel(userRepository)
  private val preferencesViewModel = PreferencesViewModel(mock(PreferencesRepository::class.java))

  @Test
  fun isSettingsContentDisplayedForNullUser() {
    val showSettingDialog = mutableStateOf(false)
    userViewModel.setCurrentUser(null)
    var context: Context? = null

    composeTestRule.setContent {
      val authService =
          GoogleAuthService(
              "abc", mock(FirebaseAuth::class.java, RETURNS_DEFAULTS), LocalContext.current)
      SettingsContent(
          navigationActions, userViewModel, preferencesViewModel, authService, showSettingDialog)
      context = LocalContext.current
    }

    composeTestRule.onNodeWithTag("SettingsContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("NullUserSettingsContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ConnectionSettingsContent").assertDoesNotExist()
    composeTestRule.onNodeWithTag("LogOutButton").assertDoesNotExist()
    composeTestRule.onNodeWithTag("DeleteAccountButton").assertDoesNotExist()
    composeTestRule.onNodeWithTag("deleteAccountDialog").assertIsNotDisplayed()

    showSettingDialog.value = true
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("SettingsContent").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("NullUserSettingsContent")
        .assertIsDisplayed()
        .assertTextEquals(context!!.getString(R.string.SettingsNullUserContent))
    composeTestRule.onNodeWithTag("ConnectionSettingsContent").assertDoesNotExist()
    composeTestRule.onNodeWithTag("LogOutButton").assertDoesNotExist()
    composeTestRule.onNodeWithTag("DeleteAccountButton").assertDoesNotExist()
    composeTestRule.onNodeWithTag("deleteAccountDialog").assertIsNotDisplayed()
  }

  @Test
  fun isSettingsContentDisplayedForUser() {
    val showSettingDialog = mutableStateOf(false)
    var context: Context? = null
    val alice = User("uid-alice", "Alice", "alice@gmail.com", 42, emptyList(), "")
    userViewModel.setCurrentUser(alice)

    composeTestRule.setContent {
      val authService =
          GoogleAuthService(
              "abc", mock(FirebaseAuth::class.java, RETURNS_DEFAULTS), LocalContext.current)
      SettingsContent(
          navigationActions, userViewModel, preferencesViewModel, authService, showSettingDialog)
      context = LocalContext.current
    }

    composeTestRule.onNodeWithTag("SettingsContent").assertIsDisplayed()
    composeTestRule.onNodeWithTag("NullUserSettingsContent").assertIsNotDisplayed()
    composeTestRule
        .onNodeWithTag("ConnectionSettingsContent")
        .assertIsDisplayed()
        .assertTextEquals(context!!.getString(R.string.SettingsConnectionContent, alice.username))
    composeTestRule.onNodeWithTag("LogOutButton").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithTag("DeleteAccountButton").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithTag("deleteAccountDialog").assertIsNotDisplayed()

    composeTestRule.onNodeWithTag("DeleteAccountButton").performClick()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("deleteAccountDialog").assertIsDisplayed()
  }

  @Test
  fun testLogoutButtonNavigatesToAuthScreen() {
    val showSettingDialog = mutableStateOf(false)
    var context: Context? = null
    val alice = User("uid-alice", "Alice", "alice@gmail.com", 42, emptyList(), "")
    userViewModel.setCurrentUser(alice)

    composeTestRule.setContent {
      val authService =
          GoogleAuthService(
              "abc", mock(FirebaseAuth::class.java, RETURNS_DEFAULTS), LocalContext.current)
      SettingsContent(
          navigationActions, userViewModel, preferencesViewModel, authService, showSettingDialog)
      context = LocalContext.current
    }

    // Click the logout button
    composeTestRule.onNodeWithTag("LogOutButton").performClick()
    composeTestRule.waitForIdle()

    // Verify that the navigation action to the AUTH screen was called
    verify(navigationActions).navigateTo(Route.AUTH)
  }
}
