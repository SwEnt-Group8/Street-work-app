package com.android.streetworkapp.ui.profile

import android.content.Context
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
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
    userRepository = mock(UserRepository::class.java)
    userViewModel = UserViewModel(userRepository)

    // mock for toast
    MockitoAnnotations.openMocks(this)
    context = mock(Context::class.java)

    // Mock the current route to be the add profile screen
    composeTestRule.setContent { AddFriendScreen(userViewModel) }
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

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("addFriendScreen").assertExists()
    composeTestRule.onNodeWithTag("AddFriendColumn").assertExists()
    composeTestRule.onNodeWithTag("BluetoothButton").assertExists()
    composeTestRule.onNodeWithTag("InstructionsContainer").assertExists()
    composeTestRule.onNodeWithTag("InstructionsTitle").assertExists()
    composeTestRule.onNodeWithTag("InstructionsBox").assertExists()
    composeTestRule.onNodeWithTag("PhoneIcon").assertExists()
    composeTestRule.onNodeWithTag("InstructionsText").assertExists()
    composeTestRule.onNodeWithTag("BluetoothIcon").assertExists()

    composeTestRule.onNodeWithTag("addFriendScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AddFriendColumn").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BluetoothButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("InstructionsContainer").assertIsDisplayed()
    composeTestRule.onNodeWithTag("InstructionsTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("InstructionsBox").assertIsDisplayed()
    composeTestRule.onNodeWithTag("PhoneIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("InstructionsText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BluetoothIcon").assertIsDisplayed()
  }

  @Test
  fun textCorrectlyDisplayed() {
    composeTestRule.waitForIdle() // Wait for rendering

    composeTestRule.onNodeWithTag("BluetoothButton").assertTextEquals("Send request")
  }

  @Test
  fun buttonWork() {
    composeTestRule.waitForIdle() // Wait for rendering

    composeTestRule.onNodeWithTag("BluetoothButton").assertHasClickAction()
  }
}
