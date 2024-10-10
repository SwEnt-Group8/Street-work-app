package com.android.streetworkapp.ui.profile

import android.annotation.SuppressLint
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen() {
  Scaffold(
      modifier = Modifier.testTag("ProfileScreen"),
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).testTag("ProfileColumn"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)) {
              // profile placeholder
              Row() {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "App Logo")
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
                    contentDescription = "App Logo",
                    modifier = Modifier.size(260.dp))
              }
              Row() {
                Button(
                    onClick = {},
                    modifier = Modifier.size(220.dp, 50.dp).testTag("profileAddButton")) {
                      Text(text = "Add a new friend", fontSize = 17.sp)
                    }
              }
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
