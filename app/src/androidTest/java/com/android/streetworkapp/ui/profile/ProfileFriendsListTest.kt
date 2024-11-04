package com.android.streetworkapp.ui.profile

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.model.user.User
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileFriendsListTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun isFriendElementCorrectlyDisplayed() {
    val friend = User("uid-alice", "Alice", "alice@gmail.com", 42, emptyList())
    composeTestRule.setContent { DisplayFriend(friend) }

    composeTestRule.onNodeWithTag("friendProfilePicture")
      .assertExists()
      .assertIsDisplayed()

    composeTestRule.onNodeWithTag("friendUsername")
      .assertExists()
      .assertIsDisplayed()
      .assertTextEquals(friend.username)

    composeTestRule.onNodeWithTag("friendScore")
      .assertExists()
      .assertIsDisplayed()
      .assertTextEquals("Score: ${friend.score}")
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
    composeTestRule.onNodeWithTag("friendList")
      .assertExists()
      .assertIsDisplayed()
      .onChildren()
      .assertCountEquals(friends.size)
  }

  @Test
  fun isEmptyListCorrectlyDisplayed() {
    val friends = emptyList<User>()
    composeTestRule.setContent { DisplayFriendList(friends) }

    composeTestRule.onNodeWithTag("emptyFriendListText")
      .assertExists()
      .assertIsDisplayed()
      .assertTextEquals("You have no friends yet :(")

    composeTestRule.onNodeWithTag("friendList").assertIsNotDisplayed()
  }

  /*
   @Test
   fun emptyFriendListDisplaysCorrectly() {
     // Set up an empty friend list scenario

     // Instantiate fire store database and associated user repository :
     val firestoreDB = FirebaseFirestore.getInstance()
     val userRepository = UserRepositoryFirestore(firestoreDB)
     val userViewModel = UserViewModel(userRepository)

     /*
     val testUser = User(
         "emptyFriendListTestUser",
         "emptyFriendListTestUser",
         "emptyFriendListTestUserMail",
         0,
         EmptyList()
         )

     userViewModel.setUser(testUser)
      */

     composeTestRule.setContent {
       ProfileScreen(
           navigationActions, userViewModel // Mock UserViewModel to have an empty friend list
           )
     }

     composeTestRule.waitForIdle() // Wait for rendering to complete

     // Check if LazyColumn is not displayed as there are no friends
     composeTestRule.onNodeWithTag("friendList").assertIsNotDisplayed()

     // Check that the placeholder text is displayed
     composeTestRule
         .onNodeWithTag("emptyFriendListText")
         .assertExists()
         .assertIsDisplayed()
         .assertTextEquals("You have no friends yet :(")
   }

   @Test
   fun nonEmptyFriendListDisplaysCorrectly() {
     // Set up an empty friend list scenario

     // Instantiate fire store database and associated user repository :
     val firestoreDB = FirebaseFirestore.getInstance()
     val userRepository = UserRepositoryFirestore(firestoreDB)
     val userViewModel = UserViewModel(userRepository)
     // val user = User()
     /*
     val testUserFriend1 = User(
         "nonEmptyFriendListTestUserFriend1",
         "nonEmptyFriendListTestUserFriend1",
         "nonEmptyFriendListTestUserFriend1Mail",
         0,
         emptyList()
         )

     val testUserFriend2 = User(
         "nonEmptyFriendListTestUserFriend2",
         "nonEmptyFriendListTestUserFriend2",
         "nonEmptyFriendListTestUserFriend2Mail",
         0,
         emptyList()
         )

     val testUser = User(
         "nonEmptyFriendListTestUser",
         "nonEmptyFriendListTestUser",
         "nonEmptyFriendListTestUserMail",
         0,
         {"nonEmptyFriendListTestUserFriend1", "nonEmptyFriendListTestUserFriend2"}
         )

     userViewModel.setUser(testUser)
     userViewModel.setUser(testUserFriend1)
     userViewModel.setUser(testUserFriend2)
      */

     composeTestRule.setContent {
       ProfileScreen(
           navigationActions, userViewModel // Mock UserViewModel to have an empty friend list
           )
     }

     composeTestRule.waitForIdle() // Wait for rendering to complete

     // Check if LazyColumn is displayed as there are two friends
     composeTestRule.onNodeWithTag("friendList").assertIsDisplayed()

     // Verify that the two friends are displayed :
     composeTestRule.onNodeWithTag("friendList").onChildren().assertCountEquals(2)

     // Check that the first friend is displayed
     composeTestRule.onNodeWithTag("friendList").onChildren().get(0).assertIsDisplayed()

     // Check that the second friend is displayed
     composeTestRule.onNodeWithTag("friendList").onChildren().get(1).assertIsDisplayed()

     // Check that the placeholder text is displayed
     composeTestRule.onNodeWithTag("emptyFriendListText").assertExists().assertIsNotDisplayed()
   }

  */
}
