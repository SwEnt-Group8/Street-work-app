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
import com.android.streetworkapp.ui.navigation.BottomNavigationMenu
import com.android.streetworkapp.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.streetworkapp.ui.navigation.NavigationActions

@Composable
fun ProfileScreen(navigationActions: NavigationActions) {
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
              Spacer(modifier = Modifier.height(10.dp))

              Row(
                  modifier = Modifier.fillMaxWidth().padding(padding),
                  horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                  verticalAlignment = Alignment.Top) {

                    // profile placeholder
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "profile picture",
                        modifier = Modifier.size(200.dp))

                    Column() {
                      Spacer(modifier = Modifier.height(2.dp))
                      // score placeholder
                      Text(
                          text = "Score: 42’424",
                          fontSize = 20.sp,
                          modifier = Modifier.testTag("profileScore"))

                      Spacer(modifier = Modifier.height(10.dp))

                      // button to add a new friend
                      Button(
                          onClick = {},
                          modifier = Modifier.size(220.dp, 50.dp).testTag("profileAddButton")) {
                            Text(text = "Add a new friend", fontSize = 17.sp)
                          }

                      Spacer(modifier = Modifier.height(10.dp))

                      // button to train with a friend
                      Button(
                          onClick = {},
                          colors = ButtonDefaults.buttonColors(Color(0xFFA53A36)),
                          modifier = Modifier.size(220.dp, 50.dp).testTag("profileTrainButton")) {
                            Text(text = "Train with a friend", fontSize = 17.sp)
                          }
                    }
                  }

              // Friend list here :
              var friends = listOf("Friend 1", "Friend 2", "Friend 3", "Friend 4", "Friend 5")
              // friends = emptyList()

              if (friends.isNotEmpty()) {

                // LazyColumn is scollable
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier =
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(padding)) {
                      items(friends) { friend -> DisplayFriend(friend, padding) }
                    }
              } else {
                Text(
                    modifier = Modifier.padding(padding).testTag("emptyFriendList"),
                    fontSize = 20.sp,
                    text = "You have no friends yet :(")
              }

              /*
              Code extract from bootcamp :
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(pd)) {
                      items(todos.value.size) { index ->
                        ToDoItem(todo = todos.value[index]) {
                          listToDosViewModel.selectToDo(todos.value[index])
                          navigationActions.navigateTo(Screen.EDIT_TODO)
                        }
                      }
                    }
                   */

              /*
                   tabList.forEach { tab ->
                BottomNavigationItem(
                    icon = { Icon(tab.icon, contentDescription = null) },
                    label = { Text(tab.textId) },
                    selected = tab.route == selectedItem,
                    onClick = { onTabSelect(tab) },
                    modifier = Modifier.clip(RoundedCornerShape(50.dp)).testTag(tab.textId))
              }
               */
            }
      })
}

@Composable
fun DisplayFriend(friend: String, padding: PaddingValues) {
  return Row(modifier = Modifier.fillMaxSize().padding(padding)) {

    // profile placeholder
    Image(
        painter = painterResource(id = R.drawable.profile),
        contentDescription = "profile picture",
        modifier = Modifier.size(75.dp))

    Spacer(modifier = Modifier.width(2.dp))

    Text(
        modifier = Modifier.padding(padding).testTag("emptyFriendList"),
        fontSize = 20.sp,
        text = friend)
  }
}
