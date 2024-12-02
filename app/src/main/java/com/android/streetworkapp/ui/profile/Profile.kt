package com.android.streetworkapp.ui.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
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
import com.android.streetworkapp.ui.theme.ColorPalette
import com.android.streetworkapp.ui.utils.CustomDialog
import com.android.streetworkapp.ui.utils.DialogType

@Composable
fun ProfileScreen(
    navigationActions: NavigationActions,
    userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory),
    innerPaddingValues: PaddingValues = PaddingValues(0.dp),
) {
  // Handling the MVVM calls for user :
  val currentUser = userViewModel.currentUser.collectAsState().value

  val friendList = userViewModel.friends.collectAsState().value

  if (currentUser != null) {
    userViewModel.getFriendsByUid(currentUser.uid)
  }

  Box(modifier = Modifier.fillMaxWidth().padding(innerPaddingValues).testTag("ProfileScreen")) {
    // Center Column for Profile Picture, Score, and Add Friend button
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 50.dp).testTag("profileColumn"),
        horizontalAlignment = Alignment.CenterHorizontally) {

          // display the profile picture
          DisplayUserPicture(currentUser, 180.dp, "profilePicture")

          // username text
          DisplayUsername(currentUser)

          // score text
          DisplayScore(currentUser)

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            // Train Button
            Button(
                onClick = { navigationActions.navigateTo(Screen.TRAIN_HUB) },
                modifier = Modifier.padding(horizontal = 4.dp).testTag("profileTrainButton"),
                colors = ColorPalette.BUTTON_COLOR) {
                  Text(text = "Train", color = Color.White)
                }

            // Add Friend Button
            Button(
                onClick = { navigationActions.navigateTo(Screen.ADD_FRIEND) },
                modifier = Modifier.padding(horizontal = 4.dp).testTag("profileAddButton"),
                colors = ColorPalette.BUTTON_COLOR) {
                  Text(text = "Add friend", color = Color.White)
                }
          }
          DisplayFriendList(friendList, userViewModel)
          Log.d("SignInScreen", "friendList : ${friendList}")
        }
  }
}

/**
 * This function displays the user's username.
 *
 * @param user - The user whose username is to be displayed.
 */
@Composable
fun DisplayUsername(user: User?) {
  val UNKNOWN_USER_MESSAGE = "unknown user"
  if (user != null) {
    Text(
        text = user.username,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp).testTag("profileUsername"))
  } else {
    Text(
        text = UNKNOWN_USER_MESSAGE,
        fontSize = 18.sp,
        modifier = Modifier.padding(top = 8.dp).testTag("profileUsername"))
  }
}

/**
 * This function displays the user's score.
 *
 * @param user - The user whose score is to be displayed.
 */
@Composable
fun DisplayScore(user: User?) {
  val UNKNOWN_SCORE_MESSAGE = "unknown score"
  if (user != null) {
    Text(
        text = "Score: ${user.score}",
        fontSize = 18.sp,
        modifier = Modifier.padding(top = 4.dp).testTag("profileScore"))
  } else {
    Text(
        text = UNKNOWN_SCORE_MESSAGE,
        fontSize = 18.sp,
        modifier = Modifier.padding(top = 4.dp).testTag("profileScore"))
  }
}

/**
 * This function displays the friends list.
 *
 * @param friends - The list of friends to display.
 * @param userViewModel - The view model for the user.
 */
@Composable
fun DisplayFriendList(friends: List<User?>, userViewModel: UserViewModel) {
  val NO_FRIENDS_MESSAGE = "You have no friends yet :("

  return if (friends.isNotEmpty()) {
    LazyColumn(modifier = Modifier.fillMaxSize().testTag("friendList")) {
      items(friends) { friend ->
        if (friend != null) {
          DisplayFriendItem(friend, userViewModel)
          HorizontalDivider(thickness = 1.dp, color = Color.Gray)
        }
      }
    }
  } else {
    Text(
        modifier = Modifier.testTag("emptyFriendListText").padding(top = 25.dp),
        fontSize = 20.sp,
        text = NO_FRIENDS_MESSAGE)
  }
}

/**
 * This function displays a friend (for the friend list). Also displays actions to perform on them
 * through a dropdown menu.
 *
 * @param friend - The friend to display.
 * @param userViewModel - The view model for the user / the friend.
 */
@Composable
fun DisplayFriendItem(friend: User, userViewModel: UserViewModel) {
  val context = LocalContext.current
  val showMenu = remember { mutableStateOf(false) }
  val DEFAULT_USER_STATUS = "Definitely not a bot"

  Row(
      modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("friendItem"),
      verticalAlignment = Alignment.CenterVertically) {
        // Friend's avatar
        DisplayUserPicture(friend, 80.dp, "friendProfilePicture")

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
              text = DEFAULT_USER_STATUS,
              fontSize = 14.sp,
              color = Color.Gray,
              modifier = Modifier.testTag("friendStatus"))
        }

        Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
          // Three-dot menu icon (Overflow menu)
          IconButton(
              modifier = Modifier.testTag("friendSettingButton"),
              onClick = { showMenu.value = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.more_vertical),
                    contentDescription = "More options",
                    modifier = Modifier.size(24.dp))
              }

          // DropDownMenu
          FriendMenu(showMenu, friend, userViewModel, context)
        }
      }
}

/**
 * This function displays the user profile picture.
 *
 * @param user - The user to display.
 */
@Composable
fun DisplayUserPicture(user: User?, size: Dp, testTag: String) {
  val DEFAULT_PROFILE_PICTURE = R.drawable.profile
  if (user != null) {
    val photo = user.picture
    Log.d("ProfilePicture", photo)

    AsyncImage(
        model =
            ImageRequest.Builder(LocalContext.current)
                .data(photo)
                .placeholder(DEFAULT_PROFILE_PICTURE)
                .build(),
        contentDescription = "profile_picture",
        contentScale = ContentScale.Crop,
        modifier =
            Modifier.size(size)
                .padding(8.dp)
                .clip(CircleShape)
                .border(2.dp, Color.LightGray, CircleShape)
                .testTag(testTag))
  } else {
    // display the profile picture
    AsyncImage(
        model =
            ImageRequest.Builder(LocalContext.current)
                .data(DEFAULT_PROFILE_PICTURE)
                .placeholder(DEFAULT_PROFILE_PICTURE)
                .build(),
        contentDescription = "profile_picture",
        contentScale = ContentScale.Crop,
        modifier =
            Modifier.size(size)
                .clip(CircleShape)
                .border(2.dp, Color.LightGray, CircleShape)
                .testTag(testTag))
  }
}

/**
 * This function displays the friend menu, which contains actions to perform on a friend.
 *
 * @param showMenu - The state of the menu (expanded or not).
 * @param friend - The friend to perform actions on.
 * @param userViewModel - The view model for the user / the friend.
 * @param context - The context of the application.
 */
@Composable
fun FriendMenu(
    showMenu: MutableState<Boolean>,
    friend: User,
    userViewModel: UserViewModel,
    context: Context
) {
  DropdownMenu(
      expanded = showMenu.value,
      onDismissRequest = { showMenu.value = false },
      modifier = Modifier.testTag("friendMenu")) {
        val showConfirmDialog = remember { mutableStateOf(false) }
        var onConfirm = {}
        var onDismiss = {}

        CustomDialog(
            showDialog = showConfirmDialog,
            dialogType = DialogType.CONFIRM,
            tag = "RemoveFriend",
            title = context.getString(R.string.RemoveFriendTitle),
            verbose = false,
            onSubmit = { onConfirm() },
            onDismiss = { onDismiss() },
            Content = ({ Text(context.getString(R.string.RemoveFriendContent, friend.username)) }))

        DropdownMenuItem(
            modifier = Modifier.testTag("RemoveFriendMenuItem"),
            onClick = {
              val friendUID = friend.uid
              val friendName = friend.username

              val currentUID = userViewModel.currentUser.value?.uid

              if (currentUID != null) {
                // Set up the confirm function for the dialog
                onConfirm = {
                  userViewModel.removeFriend(currentUID, friendUID)
                  Toast.makeText(context, "Removed $friendName from friends", Toast.LENGTH_SHORT)
                      .show()
                  showMenu.value = false
                }

                onDismiss = { showMenu.value = false }

                showConfirmDialog.value = true
              } else {
                Log.d("Profile", "Cannot remove friend - Current user is null")
                Toast.makeText(context, "Error - could not remove friend", Toast.LENGTH_SHORT)
                    .show()
              }
            },
            text = {
              // Display content here
              Row() {
                Icon(
                    painter = painterResource(id = R.drawable.person_remove),
                    contentDescription = "Remove friend",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Red)
                Text("Remove friend", modifier = Modifier.padding(start = 4.dp), color = Color.Red)
              }
            })
      }
}
