package com.android.streetworkapp.ui.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.collectAsState

@Composable
fun ProfileScreen(
    navigationActions: NavigationActions,
    userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory),
    innerPaddingValues: PaddingValues = PaddingValues(0.dp)
) {

  val context = LocalContext.current

    // Handling the MVVM calls for user :
    val currentUser = userViewModel.currentUser.collectAsState().value

    val friendList = userViewModel.friends.collectAsState().value

    if (currentUser != null) {
        userViewModel.getFriendsByUid(currentUser.uid)
    }

  // fetch profile picture from firebase
  val photo = Firebase.auth.currentUser?.photoUrl

  Box(modifier = Modifier.fillMaxWidth().padding(innerPaddingValues).testTag("ProfileScreen")) {
    // Center Column for Profile Picture, Score, and Add Friend button
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 50.dp).testTag("profileColumn"),
        horizontalAlignment = Alignment.CenterHorizontally) {
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

        // username text
        if (currentUser != null) {
            Text(
                text = currentUser.username,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp).testTag("profileUsername"))
        } else {
            Text(
                text = "unknown user",
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp).testTag("profileUsername"))
        }
        // score text
        if (currentUser != null) {
          Text(
              text = "Score: ${currentUser.score}",
              fontSize = 18.sp,
              modifier = Modifier.padding(top = 4.dp).testTag("profileScore"))
        } else {
            Text(
                text = "Score: 0",
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 4.dp).testTag("profileScore"))
        }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            // Add Friend Button
            Button(
                onClick = { navigationActions.navigateTo(Screen.ADD_FRIEND) },
                modifier = Modifier.padding(horizontal = 4.dp).testTag("profileAddButton")) {
                  Text(text = "Add friend")
                }
          }
          DisplayFriendList(friendList)
        Log.d("SignInScreen", "friendList : ${friendList}")
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
    LazyColumn(modifier = Modifier.fillMaxSize().testTag("friendList")) {
      items(friends) { friend ->
        if (friend != null) {
          DisplayFriendItem(friend)
          HorizontalDivider(thickness = 1.dp, color = Color.Gray)
        }
      }
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
fun DisplayFriendItem(friend: User) {
  val context = LocalContext.current
  Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("friendItem"),
      verticalAlignment = Alignment.CenterVertically) {
        // Friend's avatar
        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "${friend.username}'s avatar",
            modifier =
                Modifier.size(80.dp)
                    .clip(CircleShape)
                    .padding(end = 16.dp)
                    .testTag("friendProfilePicture"),
            contentScale = ContentScale.Fit)

        // Friend's info (name, score, status)
        Column(modifier = Modifier.weight(1f)) {
          Text(
              text = friend.username,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.testTag("friendUsername"))
          Text(
              text = "Score: ${friend.score}",
              fontSize = 14.sp,
              color = Color.Gray,
              modifier = Modifier.testTag("friendScore"))
          Text(
              text = "Definitely not a bot",
              fontSize = 14.sp,
              color = Color.Gray,
              modifier = Modifier.testTag("friendStatus"))
        }

        // Three-dot menu icon (Overflow menu)
        IconButton(
            modifier = Modifier.testTag("friendSettingButton"),
            onClick = {
              Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show()
            }) {
              Icon(
                  painter = painterResource(id = R.drawable.more_vertical),
                  contentDescription = "More options")
            }
      }
}
