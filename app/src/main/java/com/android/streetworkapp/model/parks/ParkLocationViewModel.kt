package com.android.streetworkapp.model.parks

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ParkLocationViewModel(private val repository: ParkLocationRepository) : ViewModel() {

  private val parksPrivate = MutableStateFlow<List<ParkLocation>>(emptyList())
  val parks: StateFlow<List<ParkLocation>> = parksPrivate.asStateFlow()

  /**
   * Find Street Workout parks that are close to a given latitude and longitude
   *
   * @param lat : Latitude (Double)
   * @param lon : Longitude (Double)
   */
  fun findNearbyParks(lat: Double, lon: Double) {
    repository.search(lat, lon, { parkLocations -> parksPrivate.value = parkLocations }, {})
  }
}
