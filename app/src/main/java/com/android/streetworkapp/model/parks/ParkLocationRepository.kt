package com.android.streetworkapp.model.parks

interface ParkLocationRepository {
  fun search(
      lat: Double,
      lon: Double,
      onSuccess: (List<ParkLocation>) -> Unit,
      onFailure: (Exception) -> Unit
  )
}
