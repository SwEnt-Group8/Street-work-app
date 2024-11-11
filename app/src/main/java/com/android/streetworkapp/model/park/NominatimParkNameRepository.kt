package com.android.streetworkapp.model.park

import okhttp3.OkHttpClient

class NominatimParkNameRepository(val client: OkHttpClient) : ParkNameRepository {

  /**
   * Used to convert a LocationId into a human readable park name.
   *
   * @param onSuccess The callback to execute on success.
   * @param onFailure The callback to execute on failure.
   */
  override fun convertLocationIdToParkName(
      locationId: String,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    TODO("Not yet implemented")
  }
}
