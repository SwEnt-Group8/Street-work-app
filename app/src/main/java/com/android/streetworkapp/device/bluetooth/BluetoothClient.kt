package com.android.streetworkapp.device.bluetooth

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat

class BluetoothClient(private val context: Context) {

  private var bluetoothGatt: BluetoothGatt? = null
  private val bluetoothAdapter: BluetoothAdapter by lazy {
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    bluetoothManager.adapter
  }
  private var scanCallback: ScanCallback? = null
  private val connectedDevices = mutableSetOf<String>()
  private val gattHandler = android.os.Handler(android.os.Looper.getMainLooper())

  /**
   * Starts the GATT client and scans for devices advertising the specified service UUID.
   *
   * @param onUidReceived Callback triggered when a UID is received from a device.
   */
  fun startGattClient(onUidReceived: (String) -> Unit) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) !=
        PackageManager.PERMISSION_GRANTED) {
      Log.e(BluetoothConstants.TAG, "Missing Bluetooth permissions")
      return
    }
    Log.d(BluetoothConstants.TAG, "Starting Bluetooth LE scan for all devices")

    // Create a filter with the specified UUID
    val scanFilter =
        ScanFilter.Builder()
            .setServiceUuid(android.os.ParcelUuid(BluetoothConstants.SERVICE_UUID))
            .build()

    // Create scan settings
    val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build()

    scanCallback =
        object : ScanCallback() {
          /**
           * Callback triggered when a BLE advertisement is found.
           *
           * @param callbackType Determines how this callback was triggered.
           * @param result Contains the Bluetooth LE scan result.
           */
          override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            connectToGattServer(device, onUidReceived)
          }

          /**
           * Callback triggered when the scan fails.
           *
           * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
           */
          override fun onScanFailed(errorCode: Int) {
            Log.e(BluetoothConstants.TAG, "${BluetoothConstants.ERROR_SCAN_FAILED} $errorCode")
          }
        }

    bluetoothAdapter.bluetoothLeScanner?.startScan(listOf(scanFilter), scanSettings, scanCallback)
  }

  /**
   * Connects to the GATT server on the specified device and reads the UID characteristic.
   *
   * @param device The Bluetooth device to connect to.
   * @param onUidReceived Callback triggered when a UID is received from a device.
   */
  private fun connectToGattServer(device: BluetoothDevice, onUidReceived: (String) -> Unit) {
    if (connectedDevices.contains(device.address)) {
      return
    }
    connectedDevices.add(device.address)
    Log.d(BluetoothConstants.TAG, "Attempting to connect to GATT server on ${device.address}")
    try {
      bluetoothGatt =
          device.connectGatt(
              context,
              false,
              object : BluetoothGattCallback() {
                /**
                 * Callback triggered when the connection state changes.
                 *
                 * @param gatt The GATT client.
                 * @param status Status of the connect or disconnect operation.
                 * @param newState New state (one of STATE_CONNECTED or STATE_DISCONNECTED).
                 */
                override fun onConnectionStateChange(
                    gatt: BluetoothGatt,
                    status: Int,
                    newState: Int
                ) {
                  gatt.requestMtu(BluetoothConstants.UID_SIZE)
                  try {
                    if (status == BluetoothGatt.GATT_SUCCESS &&
                        newState == BluetoothProfile.STATE_CONNECTED) {
                      Log.d(BluetoothConstants.TAG, "Connected to GATT server on ${device.address}")

                      gatt.discoverServices()
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                      Log.d(
                          BluetoothConstants.TAG,
                          "Disconnected from GATT server on ${device.address}")
                      gatt.close()
                    }
                  } catch (e: SecurityException) {
                    Log.e(
                        BluetoothConstants.TAG,
                        "${BluetoothConstants.ERROR_SECURITY_EXCEPTION} ${e.message}")
                  }
                }

                /**
                 * Callback triggered when the MTU size changes.
                 *
                 * @param gatt The GATT client.
                 * @param mtu The new MTU size.
                 * @param status Status of the MTU request.
                 */
                override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
                  if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(BluetoothConstants.TAG, "MTU changed to $mtu bytes")
                  } else {
                    Log.e(BluetoothConstants.TAG, "Failed to change MTU")
                  }
                }
                /**
                 * Callback triggered when services are discovered.
                 *
                 * @param gatt The GATT client.
                 * @param status Status of the service discovery operation.
                 */
                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                  try {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                      val service = gatt.getService(BluetoothConstants.SERVICE_UUID)
                      val characteristic =
                          service?.getCharacteristic(BluetoothConstants.CHARACTERISTIC_UUID)
                      characteristic?.let {
                        Log.d(
                            BluetoothConstants.TAG, "Characteristic found. Attempting to read UID.")
                        gatt.readCharacteristic(it)
                      } ?: Log.e(BluetoothConstants.TAG, "Characteristic not found.")
                    } else {
                      Log.e(BluetoothConstants.TAG, "Service discovery failed with status: $status")
                    }
                  } catch (e: SecurityException) {
                    Log.e(
                        BluetoothConstants.TAG,
                        "${BluetoothConstants.ERROR_SECURITY_EXCEPTION} ${e.message}")
                  }
                }

                /**
                 * Callback triggered when a characteristic is read.
                 *
                 * @param gatt The GATT client.
                 * @param characteristic The characteristic that was read.
                 * @param status Status of the read operation.
                 */
                override fun onCharacteristicRead(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic,
                    status: Int
                ) {
                  try {
                    Log.d(BluetoothConstants.TAG, "onCharacteristicRead - Status: $status")
                    if (status == BluetoothGatt.GATT_SUCCESS &&
                        characteristic.uuid == BluetoothConstants.CHARACTERISTIC_UUID) {
                      val value = characteristic.value
                      if (value != null) {
                        Log.d(
                            BluetoothConstants.TAG,
                            "Raw UID bytes received: ${value.joinToString { it.toString(16) }}")
                        val receivedUid = characteristic.getStringValue(0) ?: "No String Value"
                        Log.d(BluetoothConstants.TAG, "UID received: $receivedUid")
                        onUidReceived(receivedUid)
                      } else {
                        Log.e(BluetoothConstants.TAG, "Received characteristic value is null.")
                      }
                    } else if (status == 133) {
                      Log.e(BluetoothConstants.TAG, "GATT error 133 - retrying read operation")
                      gattHandler.postDelayed({ gatt.readCharacteristic(characteristic) }, 500)
                    } else {
                      Log.e(
                          BluetoothConstants.TAG,
                          "Failed to read characteristic with status: $status")
                    }
                  } catch (e: SecurityException) {
                    Log.e(
                        BluetoothConstants.TAG,
                        "${BluetoothConstants.ERROR_SECURITY_EXCEPTION} ${e.message}")
                  } finally {
                    gatt.close()
                  }
                }
              })
    } catch (e: SecurityException) {
      Log.e(BluetoothConstants.TAG, "${BluetoothConstants.ERROR_SECURITY_EXCEPTION} ${e.message}")
    }
  }

  /** Stops the GATT client. */
  fun stopGattClient() {
    try {
      bluetoothAdapter.bluetoothLeScanner?.stopScan(scanCallback)
      bluetoothGatt?.close()
      bluetoothGatt = null
      Log.d(BluetoothConstants.TAG, "GATT client stopped.")
    } catch (e: SecurityException) {
      Log.e(BluetoothConstants.TAG, "${BluetoothConstants.ERROR_SECURITY_EXCEPTION} ${e.message}")
    }
  }
}
