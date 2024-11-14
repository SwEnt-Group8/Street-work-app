package com.android.streetworkapp.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.streetworkapp.device.bluetooth.BluetoothServer

@Composable
fun BluetoothButton(
    bluetoothController: BluetoothServer,
    uid: String // UID to send
) {
  val context = LocalContext.current

  Button(
      onClick = {
        bluetoothController.startGattServer(uid) // Start GATT server to send UID
        Toast.makeText(context, "Broadcasting UID via BLE GATT server", Toast.LENGTH_SHORT).show()
      },
      modifier = Modifier.size(220.dp, 50.dp).testTag("BluetoothButton")) {
        Text(text = "Broadcast UID", fontSize = 17.sp)
      }
}
