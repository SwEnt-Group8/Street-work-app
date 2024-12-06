package com.android.streetworkapp.ui.profile

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Route
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var userViewModel: UserViewModel
  private lateinit var repository: UserRepository // Mocking interface, not concrete class

  private val mockCurrentUser =
      MutableStateFlow<User?>(
          User("user123", "John Doe", "john@example.com", 42424, emptyList(), "",
            parks = listOf("")))
  private val mockFriends =
      MutableStateFlow<List<User>>(
          listOf(
              User("friend1", "Friend One", "friend1@example.com", 123, emptyList(), "",
                parks = listOf("")),
              User("friend2", "Friend Two", "friend2@example.com", 456, emptyList(), "",
                parks = listOf(""))))

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    // Mock navigation and UserRepository (as an interface)
    navigationActions = mock(NavigationActions::class.java)
    repository = mock(UserRepository::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Route.PROFILE)

    // Create an instance of UserViewModel with the mocked repository
    userViewModel = UserViewModel(repository)

    // Stub behavior of UserRepository methods
    runBlocking {
      whenever(repository.getUserByUid("user123")).thenReturn(mockCurrentUser.value)
      whenever(repository.getFriendsByUid("user123")).thenReturn(mockFriends.value)
    }

    composeTestRule.setContent { ProfileScreen(navigationActions, userViewModel) }
  }

  @Test
  fun hasRequiredComponents() {
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("ProfileScreen").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileScore").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileAddButton").assertExists().assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileColumn").assertExists()

    // test profile picture
    composeTestRule.onNodeWithTag("profilePicture").assertExists().assertIsDisplayed()
  }

  @Test
  fun textCorrectlyDisplayed() {
    composeTestRule.waitForIdle()
    // useless to test hard coded score value
    composeTestRule.onNodeWithTag("profileAddButton").assertTextEquals("Add friend")
  }

  @Test
  fun buttonWork() {
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("profileAddButton").assertHasClickAction()
  }
}
