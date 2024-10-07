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
   * @param lat : Latitude (Double) must be between [-90,90]
   * @param lon : Longitude (Double) must be between [-180,180]
   */
  fun findNearbyParks(lat: Double, lon: Double) {
    require(lat < 90 && lat > -90)
    require(lon < 180 && lon > -180)

    repository.search(lat, lon, { parkLocations -> parksPrivate.value = parkLocations }, {})
  }
}
