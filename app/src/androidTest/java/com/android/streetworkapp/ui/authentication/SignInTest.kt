package com.android.streetworkapp.ui.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.MainActivity
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.utils.GoogleAuthService
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.capture

@RunWith(AndroidJUnit4::class)
class LoginTest : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  // The IntentsTestRule simply calls Intents.init() before the @Test block
  // and Intents.release() after the @Test block is completed. IntentsTestRule
  // is deprecated, but it was MUCH faster than using IntentsRule in our tests
  @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

  private lateinit var repository: UserRepository
  private lateinit var viewModel: UserViewModel
  private lateinit var navigationActions: NavigationActions

  private val testDispatcher = StandardTestDispatcher()

  private lateinit var authService: GoogleAuthService

  @Before
  fun setUp() {
    // Mock the repository
    repository = mock(UserRepository::class.java)
    // Set the repository in the view model
    viewModel = UserViewModel(repository)

    navigationActions = mock(NavigationActions::class.java)

    // Mock FirebaseAuth
    val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
    mockkStatic(FirebaseAuth::class)
    every { FirebaseAuth.getInstance() } returns firebaseAuth

    // Mock AuthResult and FirebaseUser
    val authResult = mockk<AuthResult>(relaxed = true)
    val firebaseUser = mockk<FirebaseUser>(relaxed = true)
    every { authResult.user } returns firebaseUser
    every { firebaseUser.uid } returns "testUid"

    // Mock the signInWithCredential method to invoke the onSuccess callback
    val onSuccessSlot = slot<(AuthResult) -> Unit>()
    every {
      firebaseAuth.signInWithCredential(any()).addOnSuccessListener(capture(onSuccessSlot))
    } answers
        {
          onSuccessSlot.captured.invoke(authResult)
          mockk()
        }
  }

  @Test
  fun uiComponentsDisplayed() {
    // Test uses useUnmerged = true for all children of containers,
    // otherwise will not be accessible for the test using testTags.

    // For Box, Text, Image, Buttons, List : check if displayed :
    composeTestRule.onNodeWithTag("loginScreenBoxContainer").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginTitle").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").assertExists().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("loginButtonIcon", useUnmergedTree = true)
        .assertExists()
        .assertIsDisplayed()

    composeTestRule.onNodeWithTag("loginButtonText", useUnmergedTree = true).assertIsDisplayed()

    // For columns / rows / spacers : check if exist :
    composeTestRule.onNodeWithTag("loginScreenColumnContainer").assertExists()
    composeTestRule.onNodeWithTag("loginScreenSpacer").assertExists()
    composeTestRule.onNodeWithTag("loginScreenSpacer").assertExists()
    composeTestRule.onNodeWithTag("loginButtonRowContainer", useUnmergedTree = true).assertExists()

    // UX elements :

    // UX - Text values :
    composeTestRule.onNodeWithTag("loginTitle").assertTextEquals("Welcome to the Street Work'App")
    composeTestRule.onNodeWithTag("loginButton").assertTextEquals("Sign in with Google")
    composeTestRule
        .onNodeWithTag("loginButtonText", useUnmergedTree = true)
        .assertTextEquals("Sign in with Google")

    // UX - Button click action :
    composeTestRule.onNodeWithTag("loginButton").assertHasClickAction()
  }

  @Test
  fun googleSignInReturnsValidActivityResult() {
    composeTestRule.onNodeWithTag("loginButton").performClick()

    // assert that an Intent resolving to Google Mobile Services has been sent (for sign-in)
    intended(toPackage("com.google.android.gms"))
  }

  @Test
  fun googleSigncallsrepository() = runTest {
    composeTestRule.onNodeWithTag("loginButton").performClick()

    composeTestRule.waitForIdle()

    verify(repository).getOrAddUserByUid(any(), any())
  }
}
