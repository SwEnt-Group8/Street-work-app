package com.android.streetworkapp.ui.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.android.sample.R
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.ui.theme.ButtonRed
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun ProfileScreen(
    navigationActions: NavigationActions,
    userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory),
    innerPaddingValues: PaddingValues = PaddingValues(0.dp)
) {

  val context = LocalContext.current

  // Fake data extraction from the MVVM :
  val alice =
      User("uid_Alice", "Alice", "alice@gmail.com", 42, listOf("Z8qgzNOoQOR09x6QQYahcyzVXfE2"))

  val bob = User("uid_Bob", "Bob", "bob@gmail.com", 64, emptyList())

  val currentUser =
      User("uid_current", "Current User", "user@gmail.com", 42424, listOf(alice.uid, bob.uid))

  val friendList = listOf(alice, bob)

  // fetch profile picture from firebase
  val photo = Firebase.auth.currentUser?.photoUrl

  // val friendList = emptyList()

  Box(modifier = Modifier.testTag("ProfileScreen")) {
    Column(
        modifier = Modifier.fillMaxSize().padding(innerPaddingValues).testTag("ProfileColumn"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)) {
          Row(
              modifier = Modifier.fillMaxWidth().padding(innerPaddingValues).testTag("profileRow"),
              horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
              verticalAlignment = Alignment.Top) {

                // display the profile picture
                AsyncImage(
                    model =
                        ImageRequest.Builder(LocalContext.current)
                            .data(photo ?: R.drawable.profile)
                            .placeholder(R.drawable.profile)
                            .build(),
                    contentDescription = "user_profile_picture",
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier.size(180.dp)
                            .clip(CircleShape)
                            .border(5.dp, Color.LightGray, CircleShape)
                            .testTag("profilePicture"))

                Column(modifier = Modifier.testTag("profileInfoColumn").padding(2.dp)) {

                  // name placeholder
                  Text(
                      text = currentUser.username,
                      fontSize = 30.sp,
                      modifier = Modifier.testTag("profileUsername").padding(2.dp))

                  // score placeholder
                  Text(
                      text = "Score: ${currentUser.score}",
                      fontSize = 20.sp,
                      modifier = Modifier.testTag("profileScore").padding(2.dp))

                  // button to add a new friend
                  Button(
                      onClick = { navigationActions.navigateTo(Screen.ADD_FRIEND) },
                      modifier =
                          Modifier.size(220.dp, 50.dp).testTag("profileAddButton").padding(2.dp)) {
                        Text(text = "Add a new friend", fontSize = 17.sp)
                      }

                  // button to train with a friend
                  Button(
                      onClick = {
                        Toast.makeText(context, "Not implemented yet", Toast.LENGTH_LONG).show()
                      },
                      colors = ButtonDefaults.buttonColors(ButtonRed),
                      modifier = Modifier.size(220.dp, 50.dp).testTag("profileTrainButton")) {
                        Text(text = "Train with a friend", fontSize = 17.sp)
                      }
                }
              }

          DisplayFriendList(friendList)
        }
  }
}

/**
 * This function displays the friends list.
 *
 * @param friends - The list of friends to display.
 */
@Composable
fun DisplayFriendList(friends: List<User>) {
  return if (friends.isNotEmpty()) {

    // LazyColumn is scrollable
    LazyColumn(
        contentPadding = PaddingValues(vertical = 0.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).testTag("friendList")) {
          items(friends) { friend -> DisplayFriend(friend) }
        }
  } else {
    Text(
        modifier = Modifier.testTag("emptyFriendListText"),
        fontSize = 20.sp,
        text = "You have no friends yet :(")
  }
}

/**
 * This function displays a friend (for the friend list).
 *
 * @param friend - The friend to display.
 */
@Composable
fun DisplayFriend(friend: User) {
  return Row(modifier = Modifier.fillMaxWidth().testTag("friendRow")) {

    // profile placeholder
    Image(
        painter = painterResource(id = R.drawable.profile),
        contentDescription = "profile picture",
        modifier = Modifier.size(75.dp).testTag("friendProfilePicture"))

    Column(modifier = Modifier.fillMaxWidth()) {

      // Small spacing between elements
      Spacer(modifier = Modifier.height(8.dp))

      // username
      Text(fontSize = 22.sp, text = friend.username, modifier = Modifier.testTag("friendUsername"))

      // score
      Text(
          text = "Score: ${friend.score}",
          fontSize = 22.sp,
          modifier = Modifier.testTag("friendScore"))
    }
  }
}
