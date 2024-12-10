package com.android.streetworkapp.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.rule.GrantPermissionRule
import com.android.streetworkapp.StreetWorkAppMain
import com.android.streetworkapp.model.preferences.PreferencesRepository
import com.android.streetworkapp.model.preferences.PreferencesViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class StreetWorkAppMainTest {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  @Test
  fun userNotLoggedInLoadAuthScreen() = runTest {
    val preferencesRepository = mock(PreferencesRepository::class.java)
    val preferencesViewModel = PreferencesViewModel(preferencesRepository)

    `when`(preferencesRepository.getLoginState()).thenReturn(false)
    `when`(preferencesRepository.getUid()).thenReturn("")
    `when`(preferencesRepository.getName()).thenReturn("")
    `when`(preferencesRepository.getScore()).thenReturn(0)

    composeTestRule.setContent {
      StreetWorkAppMain(preferencesViewModel, internetAvailable = false)
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("loginScreenBoxContainer").assertIsDisplayed()
  }

  @Test
  fun userLoggedInLoadMapScreenNoInternet() = runTest {
    val preferencesRepository = mock(PreferencesRepository::class.java)
    val preferencesViewModel = PreferencesViewModel(preferencesRepository)

    `when`(preferencesRepository.getLoginState()).thenReturn(true)
    `when`(preferencesRepository.getUid()).thenReturn("123")
    `when`(preferencesRepository.getName()).thenReturn("John")
    `when`(preferencesRepository.getScore()).thenReturn(120)

    composeTestRule.setContent {
      StreetWorkAppMain(preferencesViewModel, internetAvailable = false)
    }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
  }

  @Test
  fun userLoggedInLoadMapScreenWithInternet() = runTest {
    val preferencesRepository = mock(PreferencesRepository::class.java)
    val preferencesViewModel = PreferencesViewModel(preferencesRepository)

    `when`(preferencesRepository.getLoginState()).thenReturn(true)
    `when`(preferencesRepository.getUid()).thenReturn("123")
    `when`(preferencesRepository.getName()).thenReturn("John")
    `when`(preferencesRepository.getScore()).thenReturn(120)

    composeTestRule.setContent { StreetWorkAppMain(preferencesViewModel, internetAvailable = true) }

    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
  }
}
