package com.android.streetworkapp.model.park

interface ParkNameRepository {

  fun convertLocationIdToParkName(
      locationId: String,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit
  )
}
