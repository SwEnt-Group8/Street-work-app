package com.android.streetworkapp.device.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Check if the device is connected to the internet.
 *
 * @param context: The context of the application
 * @return true if the device is connected to the internet, false otherwise
 */
fun isInternetAvailable(context: Context): Boolean {
  val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
  val network = connectivityManager.activeNetwork ?: return false
  val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
  return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
