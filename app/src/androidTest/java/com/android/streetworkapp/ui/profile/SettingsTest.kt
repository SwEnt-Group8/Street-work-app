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
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class SettingsTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val navigationActions = Mockito.mock(NavigationActions::class.java)
  private val userRepository = Mockito.mock(UserRepository::class.java)
  private val userViewModel = UserViewModel(userRepository)

  @Test
  fun isSettingsContentDisplayedForNullUser() {
    val showSettingDialog = mutableStateOf(false)
    userViewModel.setCurrentUser(null)
    var context: Context? = null

    composeTestRule.setContent {
      SettingsContent(navigationActions, userViewModel, showSettingDialog)
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
    val alice =
        User("uid-alice", "Alice", "alice@gmail.com", 42, emptyList(), "", parks = emptyList())
    userViewModel.setCurrentUser(alice)

    composeTestRule.setContent {
      SettingsContent(navigationActions, userViewModel, showSettingDialog)
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
}
