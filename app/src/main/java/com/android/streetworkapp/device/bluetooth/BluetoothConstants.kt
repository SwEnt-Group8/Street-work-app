package com.android.streetworkapp.device.bluetooth

import java.util.UUID

object BluetoothConstants {
  // UUIDs for GATT Service and Characteristic
  val SERVICE_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
  val CHARACTERISTIC_UUID: UUID = UUID.fromString("00002201-0000-1000-8000-00805F9B34FB")

  // Logging Tag
  const val TAG = "Bluetooth"

  // Error Codes
  const val ERROR_SCAN_FAILED = "Scan failed with error: "
  const val ERROR_SECURITY_EXCEPTION = "SecurityException: Missing Bluetooth permissions - "

  // Minimal UID size
  const val UID_SIZE = 28
}
