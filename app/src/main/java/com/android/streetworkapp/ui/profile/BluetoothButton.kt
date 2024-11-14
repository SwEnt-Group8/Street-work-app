package com.android.streetworkapp.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.streetworkapp.device.bluetooth.BluetoothServer
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK
import com.android.streetworkapp.ui.theme.ColorPalette.PRIMARY_TEXT_COLOR

@Composable
fun BluetoothButton(
    bluetoothController: BluetoothServer,
    uid: String // UID to send
) {
    val context = LocalContext.current

    Button(
        onClick = {
            bluetoothController.startGattServer(uid) // Start GATT server to send UID
            Toast.makeText(context, "Send friend request", Toast.LENGTH_SHORT).show()
        },
        colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
        modifier = Modifier
            .width(220.dp)
            .height(50.dp)
            .testTag("BluetoothButton")
    ) {
        Text(
            text = "Send request",
            fontSize = 17.sp,
            color = PRIMARY_TEXT_COLOR
        )
    }
}

