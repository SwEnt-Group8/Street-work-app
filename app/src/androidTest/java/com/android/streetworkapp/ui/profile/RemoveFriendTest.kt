package com.android.streetworkapp.ui.profile

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class RemoveFriendTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var repository: UserRepository
  private lateinit var userViewModel: UserViewModel

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    repository = Mockito.mock(UserRepository::class.java)
    userViewModel = UserViewModel(repository)
  }

  @Test
  fun isFriendDeletionDisplayingCorrectly() = runTest {
    val currentUser = User("uid-alice", "Alice", "alice@gmail.com", 42, listOf("uid-bob"), "")
    val friend = User("uid-bob", "Bob", "bob@gmail.com", 64, listOf("uid-alice"), "")

    val DEFAULT_USER_STATUS = "Definitely not a bot"

    userViewModel.setCurrentUser(currentUser) // called in friendMenu, inside FriendItem

    composeTestRule.setContent { DisplayFriendItem(friend, userViewModel) }

    composeTestRule.onNodeWithTag("friendSettingButton").performClick()
    composeTestRule.onNodeWithTag("friendMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RemoveFriendMenuItem").assertIsDisplayed().assertHasClickAction()
    composeTestRule.onNodeWithTag("RemoveFriendDialog").assertIsNotDisplayed()

    composeTestRule.onNodeWithTag("RemoveFriendMenuItem").performClick()
    composeTestRule.waitForIdle() // Confirmation Dialog should be displayed

    composeTestRule.onNodeWithTag("RemoveFriendDialog").assertIsDisplayed()
    // Need to verify that confirming call userViewModel.removeFriend(friend)
    composeTestRule.onNodeWithTag("RemoveFriendDialogConfirmButton").performClick()

    Mockito.verify(repository, Mockito.times(1)).removeFriend(currentUser.uid, friend.uid)

    // Menu is being hidden afterwards, which confirms that callback function is called.
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("RemoveFriendDialog").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("friendMenu").assertIsNotDisplayed()
    // + Create another test for dismissal (if possible to test).
  }
}
