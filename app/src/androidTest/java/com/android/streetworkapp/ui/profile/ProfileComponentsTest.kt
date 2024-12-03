package com.android.streetworkapp.ui.profile

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock

@RunWith(AndroidJUnit4::class)
class ProfileComponentsTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun isNullUserScoreCorrectlyDisplayed() {
    val user = null
    val UNKNOWN_SCORE_MESSAGE = "unknown score"
    composeTestRule.setContent { DisplayScore(user) }
    composeTestRule.onNodeWithTag("profileScore").assertTextEquals(UNKNOWN_SCORE_MESSAGE)
  }

  @Test
  fun isScoreCorrectlyDisplayed() {
    val user = User("uid-alice", "Alice", "alice@gmail.com", 42, emptyList(), "")
    composeTestRule.setContent { DisplayScore(user) }
    composeTestRule.onNodeWithTag("profileScore").assertTextEquals("Score: ${user.score}")
  }

  @Test
  fun isNullUsernameCorrectlyDisplayed() {
    val user = null
    val UNKNOWN_USER_MESSAGE = "unknown user"
    composeTestRule.setContent { DisplayUsername(user) }
    composeTestRule.onNodeWithTag("profileUsername").assertTextEquals(UNKNOWN_USER_MESSAGE)
  }

  @Test
  fun isUsernameCorrectlyDisplayed() {
    val user = User("uid-alice", "Alice", "alice@gmail.com", 42, emptyList(), "")
    composeTestRule.setContent { DisplayUsername(user) }
    composeTestRule.onNodeWithTag("profileUsername").assertTextEquals(user.username)
  }

  @Test
  fun isNullUserPictureCorrectlyDisplayed() {
    val user = null
    composeTestRule.setContent { DisplayUserPicture(user, 80.dp, "test") }
    composeTestRule.onNodeWithTag("test").assertExists().assertIsDisplayed()
  }

  @Test
  fun isFriendElementCorrectlyDisplayed() {
    val friend = User("uid-alice", "Alice", "alice@gmail.com", 42, emptyList(), "")
    val DEFAULT_USER_STATUS = "Definitely not a bot"

    val repository: UserRepository = mock()
    val userViewModel = UserViewModel(repository)

    composeTestRule.setContent { DisplayFriendItem(friend, userViewModel) }

    composeTestRule.onNodeWithTag("friendProfilePicture").assertExists().assertIsDisplayed()

    composeTestRule
        .onNodeWithTag("friendUsername")
        .assertExists()
        .assertIsDisplayed()
        .assertTextEquals(friend.username)

    composeTestRule
        .onNodeWithTag("friendScore")
        .assertExists()
        .assertIsDisplayed()
        .assertTextEquals("Score: ${friend.score}")

    composeTestRule
        .onNodeWithTag("friendStatus")
        .assertExists()
        .assertIsDisplayed()
        .assertTextEquals(DEFAULT_USER_STATUS)

    composeTestRule.onNodeWithTag("friendSettingButton").assertExists().assertIsDisplayed()
  }

  @Test
  fun isFriendListCorrectlyDisplayed() {
    val alice = User("uid-alice", "Alice", "alice@gmail.com", 42, emptyList(), "")
    val bob = User("uid-bob", "Bob", "bob@gmail.com", 64, emptyList(), "")
    val friends = listOf(alice, bob)

    val repository: UserRepository = mock()
    val userViewModel = UserViewModel(repository)

    composeTestRule.setContent { DisplayFriendList(friends, userViewModel) }

    // Verify that the emptyList text is not displayed :
    composeTestRule.onNodeWithTag("emptyFriendListText").assertIsNotDisplayed()

    // We also need to check that there are enough elements in the list
    composeTestRule
        .onNodeWithTag("friendList")
        .assertExists()
        .assertIsDisplayed()
        .onChildren()
        .filter(hasTestTag("friendItem"))
        .assertCountEquals(friends.size)
  }

  @Test
  fun isEmptyListCorrectlyDisplayed() {
    val friends = emptyList<User>()
    val NO_FRIENDS_MESSAGE = "You have no friends yet :("

    val repository: UserRepository = mock()
    val userViewModel = UserViewModel(repository)

    composeTestRule.setContent { DisplayFriendList(friends, userViewModel) }

    composeTestRule
        .onNodeWithTag("emptyFriendListText")
        .assertExists()
        .assertIsDisplayed()
        .assertTextEquals(NO_FRIENDS_MESSAGE)

    composeTestRule.onNodeWithTag("friendList").assertIsNotDisplayed()
  }
}
