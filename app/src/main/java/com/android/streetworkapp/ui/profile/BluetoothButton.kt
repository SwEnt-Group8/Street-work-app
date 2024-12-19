package com.android.streetworkapp.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.streetworkapp.device.bluetooth.BluetoothServer
import com.android.streetworkapp.ui.theme.ColorPalette

@Composable
fun BluetoothButton(
    bluetoothController: BluetoothServer,
    uid: String // UID to send
) {
  val context = LocalContext.current

  Button(
      onClick = {
        bluetoothController.startGattServer(uid) // Start GATT server to send UID
        Toast.makeText(
                context,
                context.getString(R.string.BluetoothButtonToastRequestSent),
                Toast.LENGTH_SHORT)
            .show()
      },
      colors = ColorPalette.BUTTON_COLOR,
      modifier = Modifier.width(220.dp).height(50.dp).testTag("BluetoothButton")) {
        Text(text = context.getString(R.string.BluetoothButtonText), fontSize = 17.sp)
      }
}
