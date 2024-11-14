package com.android.streetworkapp.ui.profile

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.streetworkapp.ui.theme.ColorPalette

@Composable
fun NfcButton() {
  Button(
      onClick = {},
      modifier = Modifier.size(220.dp, 40.dp).testTag("NFCButton"),
      colors = ColorPalette.BUTTON_COLOR) {
        Text(text = "Activate NFC", fontSize = 17.sp)
      }
}
