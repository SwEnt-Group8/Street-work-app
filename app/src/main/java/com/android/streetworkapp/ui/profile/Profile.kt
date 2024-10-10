package com.android.streetworkapp.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).testTag("ProfileColumn"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)) {
              // profile placeholder
              Row() {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "profile picture",
                    modifier = Modifier.size(200.dp))
              }
              // score placeholder
              Row() {
                Text(
                    text = "Score: 42’424",
                    fontSize = 20.sp,
                    modifier = Modifier.testTag("profileScore"))
              }
              // QR code placeholder
              Row() {
                Image(
                    painter = painterResource(id = R.drawable.qrcode),
                    contentDescription = "qr code",
                    modifier = Modifier.size(260.dp))
              }
              // button to add a new friend
              Row() {
                Button(
                    onClick = {},
                    modifier = Modifier.size(220.dp, 50.dp).testTag("profileAddButton")) {
                      Text(text = "Add a new friend", fontSize = 17.sp)
                    }
              }
              // button to train with a friend
              Row() {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(Color(0xFFA53A36)),
                    modifier = Modifier.size(220.dp, 50.dp).testTag("profileTrainButton")) {
                      Text(text = "Train with a friend", fontSize = 17.sp)
                    }
              }
              Spacer(modifier = Modifier.height(100.dp))
            }
      })
}
