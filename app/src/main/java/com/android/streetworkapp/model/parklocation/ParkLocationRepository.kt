package com.android.streetworkapp.model.parklocation

/** A repository interface for searching nearby park locations. */
interface ParkLocationRepository {
  fun search(
      lat: Double,
      lon: Double,
      onSuccess: (List<ParkLocation>) -> Unit,
      onFailure: (Exception) -> Unit
  )
}
