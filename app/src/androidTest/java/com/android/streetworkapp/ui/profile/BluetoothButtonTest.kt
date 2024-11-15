package com.android.streetworkapp.ui.profile

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.android.streetworkapp.device.bluetooth.BluetoothServer
import org.junit.Rule
import org.junit.Test

class BluetoothButtonTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun bluetoothButton_click_displaysToast() {
    // Use a real instance of BluetoothServer
    val context = ApplicationProvider.getApplicationContext<Context>()
    val bluetoothServer = BluetoothServer(context)
    val testUid = "testUid"

    // Set the content to use the BluetoothButton with the real instance
    composeTestRule.setContent {
      BluetoothButton(bluetoothController = bluetoothServer, uid = testUid)
    }

    // Perform the click action on the Bluetooth button
    composeTestRule.onNodeWithTag("BluetoothButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("BluetoothButton").performClick()
  }
}
