package com.android.streetworkapp.ui.profile

import android.content.Context
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Screen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class AddFriendScreenTest : TestCase() {
  private lateinit var navigationActions: NavigationActions
  private lateinit var userRepository: UserRepository
  private lateinit var userViewModel: UserViewModel
  private lateinit var context: Context

  private val testDispatcher = StandardTestDispatcher()

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    // Mock the the navigation and User viewmodel
    navigationActions = mock(NavigationActions::class.java)
    userRepository = mock(UserRepository::class.java)
    userViewModel = UserViewModel(userRepository)

    // mock for toast
    MockitoAnnotations.openMocks(this)
    context = mock(Context::class.java)

    // Mock the current route to be the add profile screen
    `when`(navigationActions.currentRoute()).thenReturn(Screen.ADD_FRIEND)
    composeTestRule.setContent { AddFriendScreen(navigationActions, userViewModel) }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    // Reset the main dispatcher after each test
    Dispatchers.resetMain()
  }

  @Test
  fun hasRequiredComponents() {
    composeTestRule.waitForIdle() // Wait for rendering

    composeTestRule.onNodeWithTag("addFriendScreen").assertExists()
    composeTestRule.onNodeWithTag("AddFriendColumn").assertExists()
    composeTestRule.onNodeWithTag("NFCButton").assertExists()
    composeTestRule.onNodeWithTag("inputID").assertExists()
    composeTestRule.onNodeWithTag("RequestButton").assertExists()

    composeTestRule.onNodeWithTag("addFriendScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AddFriendColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("NFCButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("inputID").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RequestButton").assertIsDisplayed()
  }

  @Test
  fun textCorrectlyDisplayed() {
    composeTestRule.waitForIdle() // Wait for rendering

    composeTestRule.onNodeWithTag("NFCButton").assertTextEquals("Activate NFC")
    composeTestRule.onNodeWithTag("RequestButton").assertTextEquals("Send request")
  }

  @Test
  fun buttonWork() {
    composeTestRule.waitForIdle() // Wait for rendering

    composeTestRule.onNodeWithTag("NFCButton").assertHasClickAction()
    composeTestRule.onNodeWithTag("RequestButton").assertHasClickAction()
  }

  // added runBlocking to test suspend function
  // runBlocking allows you to execute and wait for coroutines to finish
  @Test
  fun requestButtonClick_withEmptyId() = runTest {
    composeTestRule.waitForIdle() // Wait for rendering

    // simulate clicking the RequestButton with empty ID
    composeTestRule.onNodeWithTag("RequestButton").performClick()

    // verify that addFriend is never called
    verify(userRepository, never()).addFriend(anyString(), anyString())
  }

  @Test
  fun testButtonClick_withValidId() = runTest {
    // create fake user id
    val uid = ""
    val fake = "validID"
    // Simulate entering a valid ID inside text field
    composeTestRule.onNodeWithTag("inputID").performTextInput(fake)

    // Simulate clicking the RequestButton
    composeTestRule.onNodeWithTag("RequestButton").performClick()

    testDispatcher.scheduler.advanceUntilIdle()
    // Verify that addFriend has the correct parameters
    verify(userRepository).addFriend(uid, fake)
  }
}
