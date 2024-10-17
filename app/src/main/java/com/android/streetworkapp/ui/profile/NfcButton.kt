package com.android.streetworkapp.ui.profile

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NfcButton() {
  Button(onClick = {}, modifier = Modifier.size(220.dp, 50.dp).testTag("NFCButton")) {
    Text(text = "Activate NFC", fontSize = 17.sp)
  }
}
