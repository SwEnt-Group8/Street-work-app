package com.android.streetworkapp.model.park

/** A repository interface for park names. */
interface ParkNameRepository {

  fun convertLocationIdToParkName(
      locationId: String,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit
  )
}
