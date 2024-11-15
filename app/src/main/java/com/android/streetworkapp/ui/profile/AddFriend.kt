package com.android.streetworkapp.ui.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.streetworkapp.device.bluetooth.BluetoothClient
import com.android.streetworkapp.device.bluetooth.BluetoothConstants
import com.android.streetworkapp.device.bluetooth.BluetoothServer
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK
import com.android.streetworkapp.ui.theme.ColorPalette.PRIMARY_TEXT_COLOR
import com.android.streetworkapp.ui.theme.ColorPalette.PRINCIPLE_BACKGROUND_COLOR
import com.android.streetworkapp.ui.theme.ColorPalette.SECONDARY_TEXT_COLOR
import com.android.streetworkapp.ui.theme.LightGray

@Composable
fun AddFriendScreen(
    userViewModel: UserViewModel,
    innerPaddingValues: PaddingValues = PaddingValues(0.dp)
) {
  // context for Toast
  val context = LocalContext.current
  val currentUser = userViewModel.currentUser.collectAsState().value
  val user = userViewModel.user.collectAsState().value
  val uid = currentUser?.uid ?: ""

  // Instantiate BluetoothClient
  val bluetoothClient = remember { BluetoothClient(context) }
  val bluetoothServer = remember { BluetoothServer(context) }

  var showRequestDialog by remember { mutableStateOf(false) }
  var receivedRequestUid by remember { mutableStateOf("") }

  // Start scanning for GATT servers on screen load
  LaunchedEffect(Unit) {
    bluetoothClient.startGattClient { receivedUid ->
      userViewModel.getUserByUid(receivedUid)
      receivedRequestUid = receivedUid
      Log.d(BluetoothConstants.TAG, "UID received on client: $receivedUid")
      showRequestDialog = true
    }
  }

  // Ensure GATT client is stopped when leaving the screen
  DisposableEffect(Unit) {
    onDispose {
      bluetoothClient.stopGattClient()
      bluetoothServer.stopGattServer()
      Log.d(BluetoothConstants.TAG, "Bluetooth client and server stopped.")
    }
  }

  if (showRequestDialog && user != null) {
    FriendRequestDialog(
        username = user.username,
        onAccept = {
          userViewModel.addFriend(uid, receivedRequestUid)
          Toast.makeText(context, "Friend added.", Toast.LENGTH_SHORT).show()
          showRequestDialog = false
        },
        onRefuse = { showRequestDialog = false })
  }

  Box(modifier = Modifier.testTag("addFriendScreen")) {
    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(innerPaddingValues)
                .padding(vertical = 24.dp)
                .testTag("AddFriendColumn"),
        horizontalAlignment = Alignment.CenterHorizontally) {
          Column(
              modifier =
                  Modifier.align(Alignment.Start)
                      .padding(horizontal = 16.dp, vertical = 8.dp)
                      .testTag("InstructionsContainer")) {
                // Title
                Text(
                    text = "Instructions",
                    modifier =
                        Modifier.align(Alignment.Start)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("InstructionsTitle"),
                    style =
                        TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight(500),
                            color = PRIMARY_TEXT_COLOR))
              }
          Box(
              modifier =
                  Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
                      .border(
                          width = 1.dp,
                          color = Color.Gray,
                          shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp))
                      .width(336.dp)
                      .background(
                          PRINCIPLE_BACKGROUND_COLOR,
                          shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                      .padding(12.dp)
                      .testTag("InstructionsBox")) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Image(
                      painter = painterResource(id = R.drawable.phone),
                      contentDescription = "Phone Icon",
                      modifier =
                          Modifier.size(32.dp)
                              .background(LightGray, shape = CircleShape)
                              .padding(6.dp)
                              .testTag("PhoneIcon"))
                  Text(
                      text =
                          "Bring your phones together while activating bluetooth to add a friend",
                      style =
                          TextStyle(
                              fontSize = 14.sp,
                              lineHeight = 16.sp,
                              fontWeight = FontWeight(500),
                              color = PRIMARY_TEXT_COLOR),
                      modifier = Modifier.padding(start = 8.dp).testTag("InstructionsText"))
                }
              }
          Image(
              painter = painterResource(id = R.drawable.bluetooth),
              contentDescription = "Bluetooth Icon",
              modifier = Modifier.size(150.dp).testTag("BluetoothIcon").padding(vertical = 16.dp))

          BluetoothButton(bluetoothServer, uid)
        }
  }
}

@Composable
fun FriendRequestDialog(username: String, onAccept: () -> Unit, onRefuse: () -> Unit) {
  androidx.compose.material3.AlertDialog(
      onDismissRequest = { onRefuse() },
      title = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("dialogTitle")) {
              Text("Friend request from $username", color = PRIMARY_TEXT_COLOR)
            }
      },
      text = { Text("Do you want to add $username as a friend?", color = SECONDARY_TEXT_COLOR) },
      confirmButton = {
        Button(
            onClick = onAccept,
            colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
            modifier = Modifier.testTag("acceptButton")) {
              Text("Accept", color = PRIMARY_TEXT_COLOR)
            }
      },
      dismissButton = {
        Button(
            onClick = onRefuse,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier.testTag("refuseButton")) {
              Text("Refuse", color = PRIMARY_TEXT_COLOR)
            }
      },
      containerColor = PRINCIPLE_BACKGROUND_COLOR,
      modifier = Modifier.testTag("FriendRequestDialog"))
}
