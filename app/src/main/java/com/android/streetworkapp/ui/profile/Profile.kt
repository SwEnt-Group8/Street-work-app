package com.android.streetworkapp.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.BottomNavigationMenu
import com.android.streetworkapp.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.streetworkapp.ui.navigation.NavigationActions

@Composable
fun ProfileScreen(navigationActions: NavigationActions, userViewModel: UserViewModel) {
  Scaffold(
      modifier = Modifier.testTag("ProfileScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION)
      },
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).testTag("ProfileColumn"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)) {
              Spacer(modifier = Modifier.height(10.dp).testTag("profileSpacer"))

              Row(
                  modifier = Modifier.fillMaxWidth().padding(padding).testTag("profileRow"),
                  horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                  verticalAlignment = Alignment.Top) {

                    // profile placeholder
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "profile picture",
                        modifier = Modifier.size(200.dp).testTag("profilePicture"),
                    )

                    Column(modifier = Modifier.testTag("profileInfoColumn")) {
                      Spacer(modifier = Modifier.height(2.dp).testTag("profileInfoSpacer"))
                      // score placeholder
                      Text(
                          text = "Score: 42’424",
                          fontSize = 20.sp,
                          modifier = Modifier.testTag("profileScore"))

                      Spacer(modifier = Modifier.height(10.dp).testTag("profileInfoSpacer2"))

                      // button to add a new friend
                      Button(
                          onClick = {},
                          modifier = Modifier.size(220.dp, 50.dp).testTag("profileAddButton")) {
                            Text(text = "Add a new friend", fontSize = 17.sp)
                          }

                      Spacer(modifier = Modifier.height(10.dp).testTag("profileInfoSpacer3"))

                      // button to train with a friend
                      Button(
                          onClick = {},
                          colors = ButtonDefaults.buttonColors(Color(0xFFA53A36)),
                          modifier = Modifier.size(220.dp, 50.dp).testTag("profileTrainButton")) {
                            Text(text = "Train with a friend", fontSize = 17.sp)
                          }
                    }
                  }

              // currentUser = userViewModel.currentUser
              // val friends = userViewModel.getFriendsByUid(currentUser.uid).collectAsState()

              // val flover = userViewModel.getUserById("Z8qgzNOoQOR09x6QQYahcyzVXfE2")

              // Placeholder MVVM calls :
              val alice =
                  User(
                      "uid_Alice",
                      "Alice",
                      "alice@gmail.com",
                      42,
                      List(1) { "Z8qgzNOoQOR09x6QQYahcyzVXfE2" })
              val bob = User("uid_Bob", "Bob", "bob@gmail.com", 42, emptyList())

              val currentUser =
                  User(
                      "uid_current",
                      "Current User",
                      "user@gmail.com",
                      42,
                      List(2) {
                        alice.uid
                        bob.uid
                      })

              val friends = List(10) { alice }

              // friends = emptyList()

              if (friends.isNotEmpty()) {

                // LazyColumn is scrollable
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 2.dp),
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(padding)
                            .testTag("friendList")) {
                      items(friends) { friend -> DisplayFriend(friend, padding) }
                    }
              } else {
                Text(
                    modifier = Modifier.padding(padding).testTag("emptyFriendList"),
                    fontSize = 20.sp,
                    text = "You have no friends yet :(")
              }
            }
      })
}

@Composable
fun DisplayFriend(friend: User, padding: PaddingValues) {
  return Row(modifier = Modifier.fillMaxWidth().padding(padding)) {

    // profile placeholder
    Image(
        painter = painterResource(id = R.drawable.profile),
        contentDescription = "profile picture",
        modifier = Modifier.size(75.dp))

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.fillMaxWidth().padding(padding)) {

      // Small spacing between elements
      Spacer(modifier = Modifier.height(12.dp))

      // username
      Text(fontSize = 22.sp, text = friend.username)

      // score
      Text(text = "Score: ${friend.score}", fontSize = 22.sp, modifier = Modifier.padding(padding))
    }
  }
}
