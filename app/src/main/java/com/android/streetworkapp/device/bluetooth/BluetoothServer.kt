package com.android.streetworkapp.device.bluetooth

import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresApi

/**
 * BluetoothServer class that starts a GATT server with a UID characteristic and advertises the
 * service to clients.
 */
class BluetoothServer(private val context: Context) {

  private var gattServer: BluetoothGattServer? = null
  private val uidSentCount = mutableMapOf<String, Int>()
  private val maxUidSendCount = 20
  private val bluetoothAdapter: BluetoothAdapter by lazy {
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    bluetoothManager.adapter
  }

  private val advertiseCallback =
      object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
          Log.d(BluetoothConstants.TAG, "Advertising started successfully.")
        }

        override fun onStartFailure(errorCode: Int) {
          Log.e(BluetoothConstants.TAG, "${BluetoothConstants.ERROR_SCAN_FAILED} $errorCode")
        }
      }

  /**
   * Starts the GATT server with a UID characteristic and starts advertising the service.
   *
   * @param uid The UID to broadcast.
   */
  @RequiresApi(Build.VERSION_CODES.S)
  fun startGattServer(uid: String) {
    Log.d("DEBUGSWENT", "uid: $uid")
    if (!bluetoothAdapter.isEnabled) {
      Log.e(BluetoothConstants.TAG, "Bluetooth is not enabled.")
      return
    }

    try {
      val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
      gattServer =
          bluetoothManager.openGattServer(context, gattServerCallback)?.apply {
            val service =
                BluetoothGattService(
                    BluetoothConstants.SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
            val characteristic =
                BluetoothGattCharacteristic(
                    BluetoothConstants.CHARACTERISTIC_UUID,
                    BluetoothGattCharacteristic.PROPERTY_READ,
                    BluetoothGattCharacteristic.PERMISSION_READ)
            characteristic.value = uid.toByteArray(Charsets.UTF_8)
            service.addCharacteristic(characteristic)
            addService(service)
            Log.d(BluetoothConstants.TAG, "GATT server started with UID characteristic.")
          }

      val advertiser = bluetoothAdapter.bluetoothLeAdvertiser
      val settings =
          AdvertiseSettings.Builder()
              .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
              .setConnectable(true)
              .build()
      val data =
          AdvertiseData.Builder()
              .setIncludeDeviceName(true)
              .addServiceUuid(ParcelUuid(BluetoothConstants.SERVICE_UUID))
              .build()

      advertiser.startAdvertising(settings, data, advertiseCallback)
    } catch (e: SecurityException) {
      Log.e(BluetoothConstants.TAG, "${BluetoothConstants.ERROR_SECURITY_EXCEPTION} ${e.message}")
    }
  }

  /** Stops the GATT server. */
  @RequiresApi(Build.VERSION_CODES.S)
  fun stopGattServer() {
    try {
      gattServer?.close()
      Log.d(BluetoothConstants.TAG, "GATT server stopped.")
    } catch (e: SecurityException) {
      Log.e(BluetoothConstants.TAG, "${BluetoothConstants.ERROR_SECURITY_EXCEPTION} ${e.message}")
    }
  }

  private val gattServerCallback =
      object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
          if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.d(BluetoothConstants.TAG, "Device connected: ${device.address}")
          } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.d(BluetoothConstants.TAG, "Device disconnected: ${device.address}")
          }
        }

        /**
         * Sends the UID to the connected device when a read request is received.
         *
         * @param device The connected device.
         * @param requestId The request ID.
         * @param offset The offset.
         * @param characteristic The characteristic to read.
         */
        @RequiresApi(Build.VERSION_CODES.S)
        override fun onCharacteristicReadRequest(
            device: BluetoothDevice,
            requestId: Int,
            offset: Int,
            characteristic: BluetoothGattCharacteristic
        ) {
          if (characteristic.uuid == BluetoothConstants.CHARACTERISTIC_UUID) {
            val currentCount = uidSentCount[device.address] ?: 0
            if (currentCount < maxUidSendCount) {
              try {

                gattServer?.sendResponse(
                    device, requestId, BluetoothGatt.GATT_SUCCESS, 0, characteristic.value)
                Log.d(BluetoothConstants.TAG, "UID sent to device: ${device.address}")
                uidSentCount[device.address] = currentCount + 1
              } catch (e: SecurityException) {
                Log.e(
                    BluetoothConstants.TAG,
                    "${BluetoothConstants.ERROR_SECURITY_EXCEPTION} ${e.message}")
              }
            } else {
              Log.d(
                  BluetoothConstants.TAG,
                  "Max UID send count reached for device: ${device.address}")
            }
          }
        }
      }
}
