package com.android.streetworkapp.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.streetworkapp.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriendScreen(navigationActions: NavigationActions) {
  var id by remember { mutableStateOf("") }

  Scaffold(
      modifier = Modifier.testTag("addFriendScreen"),
      topBar = {
        TopAppBar(
            title = { Text("Add a new friend", Modifier.testTag("addFriendTitle")) },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() }, Modifier.testTag("goBackButton")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back")
                  }
            })
      },
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).testTag("AddFriendColumn"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)) {
              Image(
                  painter = painterResource(id = R.drawable.place_holder),
                  contentDescription = "profile picture",
                  modifier = Modifier.size(200.dp))

              // button activate NFC (don't work)

              Button(onClick = {}, modifier = Modifier.size(220.dp, 50.dp).testTag("NFCButton")) {
                Text(text = "Activate NFC", fontSize = 17.sp)
              }

              // add friend ID
              OutlinedTextField(
                  value = id,
                  onValueChange = { id = it },
                  label = { Text("Friend ID") },
                  placeholder = { Text("Add a friend with id") },
                  modifier =
                      Modifier.width(300.dp) // make the test field smaller
                          .testTag("inputTodoTitle"))

              Button(
                  onClick = {}, modifier = Modifier.size(220.dp, 50.dp).testTag("RequestButton")) {
                    Text(text = "Send request", fontSize = 17.sp)
                  }

              Spacer(modifier = Modifier.height(10.dp))
            }
      })
}
