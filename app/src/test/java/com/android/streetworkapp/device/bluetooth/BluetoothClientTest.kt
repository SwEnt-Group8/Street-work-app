package com.android.streetworkapp.device.bluetooth

import android.Manifest
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class BluetoothClientTest {

  @Mock private lateinit var context: Context
  @Mock private lateinit var bluetoothLeScanner: BluetoothLeScanner

  private lateinit var bluetoothClient: BluetoothClient

  @Before
  fun setUp() {
    // Mock Bluetooth services and adapter setup
    bluetoothClient = BluetoothClient(context)
  }

  @Test
  fun `startGattClient - should not start scan if permissions are missing`() {
    whenever(ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN))
        .thenReturn(PackageManager.PERMISSION_DENIED)

    bluetoothClient.startGattClient { /* onUidReceived callback */}

    verify(bluetoothLeScanner, never())
        .startScan(any<List<ScanFilter>>(), any(), any<ScanCallback>())
  }
}
