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
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.model.user.User
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileComponentsTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun isNullUserScoreCorrectlyDisplayed() {
    val user = null
    composeTestRule.setContent { DisplayScore(user) }
    composeTestRule.onNodeWithTag("profileScore").assertTextEquals("unknown score")
  }

  @Test
  fun isScoreCorrectlyDisplayed() {
    val user = User("uid-alice", "Alice", "alice@gmail.com", 42, emptyList())
    composeTestRule.setContent { DisplayScore(user) }
    composeTestRule.onNodeWithTag("profileScore").assertTextEquals("Score: ${user.score}")
  }

  @Test
  fun isNullUsernameCorrectlyDisplayed() {
    val user = null
    composeTestRule.setContent { DisplayUsername(user) }
    composeTestRule.onNodeWithTag("profileUsername").assertTextEquals("unknown user")
  }

  @Test
  fun isUsernameCorrectlyDisplayed() {
    val user = User("uid-alice", "Alice", "alice@gmail.com", 42, emptyList())
    composeTestRule.setContent { DisplayUsername(user) }
    composeTestRule.onNodeWithTag("profileUsername").assertTextEquals(user.username)
  }

  @Test
  fun isFriendElementCorrectlyDisplayed() {
    val friend = User("uid-alice", "Alice", "alice@gmail.com", 42, emptyList())
    composeTestRule.setContent { DisplayFriendItem(friend) }

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
        .assertTextEquals("Definitely not a bot")

    composeTestRule.onNodeWithTag("friendSettingButton").assertExists().assertIsDisplayed()
  }

  @Test
  fun isFriendListCorrectlyDisplayed() {
    val alice = User("uid-alice", "Alice", "alice@gmail.com", 42, emptyList())
    val bob = User("uid-bob", "Bob", "bob@gmail.com", 64, emptyList())
    val friends = listOf(alice, bob)

    composeTestRule.setContent { DisplayFriendList(friends) }

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
    composeTestRule.setContent { DisplayFriendList(friends) }

    composeTestRule
        .onNodeWithTag("emptyFriendListText")
        .assertExists()
        .assertIsDisplayed()
        .assertTextEquals("You have no friends yet :(")

    composeTestRule.onNodeWithTag("friendList").assertIsNotDisplayed()
  }
}
