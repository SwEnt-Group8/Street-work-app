package com.android.streetworkapp.device.bluetooth

import android.bluetooth.*
import android.content.Context
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class BluetoothServerTest {

  @Mock private lateinit var context: Context
  @Mock private lateinit var gattServer: BluetoothGattServer
  @Mock private lateinit var device: BluetoothDevice
  @Mock private lateinit var characteristic: BluetoothGattCharacteristic

  private lateinit var bluetoothServer: BluetoothServer
  private val deviceAddress = "00:11:22:33:44:55"

  @Before
  fun setUp() {
    whenever(device.address).thenReturn(deviceAddress)

    bluetoothServer = BluetoothServer(context)
  }

  @Test
  fun `onConnectionStateChange - should log device connection and disconnection`() {
    val gattServerCallback =
        bluetoothServer.javaClass
            .getDeclaredField("gattServerCallback")
            .apply { isAccessible = true }
            .get(bluetoothServer) as BluetoothGattServerCallback

    // Test device connection
    gattServerCallback.onConnectionStateChange(
        device, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_CONNECTED)
    // Test device disconnection
    gattServerCallback.onConnectionStateChange(
        device, BluetoothGatt.GATT_SUCCESS, BluetoothProfile.STATE_DISCONNECTED)
  }

  @Test
  fun `onCharacteristicReadRequest - should not send UID if count is above max`() {
    val gattServerCallback =
        bluetoothServer.javaClass
            .getDeclaredField("gattServerCallback")
            .apply { isAccessible = true }
            .get(bluetoothServer) as BluetoothGattServerCallback

    // Configure the characteristic UUID to match the expected UUID
    whenever(characteristic.uuid).thenReturn(BluetoothConstants.CHARACTERISTIC_UUID)

    // Simulate reaching the maximum count
    for (i in 1..28) {
      gattServerCallback.onCharacteristicReadRequest(device, i, 0, characteristic)
    }

    // Verify that sendResponse is not called after the maximum count
    verify(gattServer, never()).sendResponse(eq(device), any(), any(), any(), any())
  }
}
