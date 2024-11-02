package com.android.streetworkapp.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.sample.R
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Screen

@Composable
fun ProfileScreen(
    navigationActions: NavigationActions,
    userViewModel: UserViewModel = viewModel(factory = UserViewModel.Factory),
    innerPaddingValues: PaddingValues = PaddingValues(0.dp)
) {
  Box(modifier = Modifier.testTag("ProfileScreen")) {
    Column(
        modifier = Modifier.fillMaxSize().padding(innerPaddingValues).testTag("ProfileColumn"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)) {
          // profile placeholder

          Image(
              painter = painterResource(id = R.drawable.profile),
              contentDescription = "profile picture",
              modifier = Modifier.size(200.dp))

          // score placeholder

          Text(
              text = "Score: 42’424", fontSize = 20.sp, modifier = Modifier.testTag("profileScore"))

          // QR code placeholder

          Image(
              painter = painterResource(id = R.drawable.qrcode),
              contentDescription = "qr code",
              modifier = Modifier.size(260.dp))

          Spacer(modifier = Modifier.height(10.dp))

          // button to go to add friend screen
          Button(
              onClick = { navigationActions.navigateTo(Screen.ADD_FRIEND) },
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
}
