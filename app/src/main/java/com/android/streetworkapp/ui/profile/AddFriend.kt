package com.android.streetworkapp.ui.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.R
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriendScreen(
    navigationActions: NavigationActions,
    userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory),
    innerPaddingValues: PaddingValues = PaddingValues(0.dp)
) {
  // variable for outlined text
  var id by remember { mutableStateOf("") }
  // context for Toast
  val context = LocalContext.current
  // fake user ID (placeholder)
  val uid = "user123"

  Box(modifier = Modifier.testTag("addFriendScreen")) {
    Column(
        modifier = Modifier.fillMaxSize().padding(innerPaddingValues).testTag("AddFriendColumn"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)) {
          Image(
              painter = painterResource(id = R.drawable.place_holder),
              contentDescription = "profile picture",
              modifier = Modifier.size(200.dp))

          // button to activate NFC (don't work)
          NfcButton()

          // write friend ID
          OutlinedTextField(
              value = id,
              onValueChange = { id = it },
              label = { Text("Friend ID") },
              placeholder = { Text("Add a friend with id") },
              modifier =
                  Modifier.width(300.dp) // make the text field smaller
                      .testTag("inputID"))

          // Put the id inside the friend list of USER
          Button(
              onClick = {
                if (id.isEmpty()) {
                  // If id is null or empty, show a toast message to the user
                  Toast.makeText(context, "ID cannot be empty.", Toast.LENGTH_SHORT).show()
                } else {
                  // add the friend to the user firendlist
                  userViewModel.addFriend(uid, id)
                  Toast.makeText(context, "Friend request sent.", Toast.LENGTH_SHORT).show()
                }
              },
              modifier = Modifier.size(220.dp, 50.dp).testTag("RequestButton")) {
                Text(text = "Send request", fontSize = 17.sp)
              }
        }
  }
}
