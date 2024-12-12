package com.android.streetworkapp.model.park

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.streetworkapp.model.parklocation.ParkLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

open class ParkViewModel(
    private val repository: ParkRepository,
    private val nameRepository: ParkNameRepository = NominatimParkNameRepository(OkHttpClient())
) : ViewModel() {

  // StateFlow of the current park
  private val _currentPark = MutableStateFlow<Park?>(null)
  val currentPark: StateFlow<Park?>
    get() = _currentPark

  private val _park = MutableStateFlow<Park?>(null)
  val park: StateFlow<Park?>
    get() = _park

  private val _parkLocation = MutableStateFlow<ParkLocation>(ParkLocation())
  val parkLocation: StateFlow<ParkLocation>
    get() = _parkLocation

  private val _parkList = MutableStateFlow<List<Park?>>(emptyList())
  val parkList: StateFlow<List<Park?>>
    get() = _parkList

  /**
   * Set the current park.
   *
   * @param park The park to set as the current park.
   */
  fun setCurrentPark(park: Park?) {
    _currentPark.value = park
  }

  /**
   * Load the current park from the database using its ID.
   *
   * @param pid The park ID.
   */
  fun loadCurrentPark(pid: String) {
    viewModelScope.launch {
      val fetchedPark = repository.getParkByPid(pid)
      _currentPark.value = fetchedPark
    }
  }

  /**
   * Get a new park ID.
   *
   * @return A new park ID.
   */
  fun getNewPid(): String {
    return repository.getNewPid()
  }

  /**
   * Get a park by its ID.
   *
   * @param pid The park ID.
   * @return The park with the given ID, or null if the park does not exist.
   */
  fun getParkByPid(pid: String) {
    viewModelScope.launch {
      val fetchedPark = repository.getParkByPid(pid)
      _park.value = fetchedPark
    }
  }

  /**
   * Get a park by its location ID.
   *
   * @param locationId The location ID.
   * @return The park with the given location ID, or null if the park does not exist.
   */
  fun getParkByLocationId(locationId: String) {
    viewModelScope.launch {
      val fetchedPark = repository.getParkByLocationId(locationId)
      _park.value = fetchedPark
    }
  }

  /**
   * Create a park.
   *
   * @param park The park to create.
   */
  fun createPark(park: Park) = viewModelScope.launch { repository.createPark(park) }

  /**
   * Get or create a park by its location.
   *
   * @param location The park location.
   */
  fun getOrCreateParkByLocation(location: ParkLocation) {
    viewModelScope.launch {
      val park = repository.getOrCreateParkByLocation(location)
      _park.value = park
    }
  }

  fun getOrCreateAllParksByLocation(locations: List<ParkLocation>) {
    viewModelScope.launch {
      val parks = locations.map { repository.getOrCreateParkByLocation(it) }
      _parkList.value = parks
    }
  }
  /**
   * Update the name of a park.
   *
   * @param pid The park ID.
   * @param name The new name.
   */
  fun updateName(pid: String, name: String) =
      viewModelScope.launch { repository.updateName(pid, name) }

  /** Update the name of the current park using the nominatim API. */
  fun updateCurrentParkNameNominatim() =
      viewModelScope.launch {

        // Only true when we have never updated the park name with nominatim to reduce calls to the
        // API
        if (_currentPark.value?.name?.contains("Default Park") == true) {
          nameRepository.convertLocationIdToParkName(
              _parkLocation.value.id,
              { name -> _currentPark.value?.let { repository.updateName(it.pid, name) } },
              { Log.e("Error", "The update of the park name has failed.") })
        }
      }

  /**
   * Set the new parkLocation
   *
   * @param parkLocation: a ParkLocation object
   */
  fun setParkLocation(parkLocation: ParkLocation?) {
    require(parkLocation != null) { "ParkLocation can not be null" }
    _parkLocation.value = parkLocation
  }

  /**
   * Update the image reference of a park.
   *
   * @param pid The park ID.
   * @param imageReference The new image reference.
   */
  fun updateImageReference(pid: String, imageReference: String) =
      viewModelScope.launch { repository.updateImageReference(pid, imageReference) }

  /**
   * Delete a rating from a park.
   *
   * @param pid The park ID.
   * @param rating The rating to delete.
   */
  fun deleteRating(pid: String, rating: Int) =
      viewModelScope.launch { repository.deleteRating(pid, rating) }

  /**
   * Update the capacity of a park.
   *
   * @param pid The park ID.
   * @param capacity The new capacity.
   */
  fun updateCapacity(pid: String, capacity: Int) =
      viewModelScope.launch { repository.updateCapacity(pid, capacity) }

  /**
   * Increment the occupancy of a park.
   *
   * @param pid The park ID.
   */
  fun incrementOccupancy(pid: String) = viewModelScope.launch { repository.incrementOccupancy(pid) }

  /**
   * Decrement the occupancy of a park.
   *
   * @param pid The park ID.
   */
  fun decrementOccupancy(pid: String) = viewModelScope.launch { repository.decrementOccupancy(pid) }

  /**
   * Add an event to a park.
   *
   * @param pid The park ID.
   * @param eid The event ID.
   */
  fun addEventToPark(pid: String, eid: String) =
      viewModelScope.launch { repository.addEventToPark(pid, eid) }

  /**
   * Delete an event from a park.
   *
   * @param pid The park ID.
   * @param eid The event ID.
   */
  fun deleteEventFromPark(pid: String, eid: String) =
      viewModelScope.launch { repository.deleteEventFromPark(pid, eid) }

  /**
   * Add a rating to a park, ensuring that the user has not already rated it.
   *
   * @param pid The park ID.
   * @param uid The user ID of the person rating.
   * @param rating The rating to add.
   */
  fun addRating(pid: String, uid: String, rating: Float) =
      viewModelScope.launch {
        repository.addRating(pid, uid, rating)
        val updatedPark = repository.getParkByPid(pid)
        _currentPark.value = updatedPark
      }

  /**
   * Delete a park by its ID.
   *
   * @param pid The park ID.
   */
  fun deleteParkByPid(pid: String) = viewModelScope.launch { repository.deleteParkByPid(pid) }

  /**
   * Registers a callback that gets called each time the document gets updated
   *
   * @param parkId The id of the document to listen to.
   * @param onCollectionUpdate The callback
   */
  open fun registerCollectionListener(parkId: String, onCollectionUpdate: () -> Unit) {
    viewModelScope.launch {
      require(parkId.isNotEmpty()) { "Empty parkId" }
      repository.registerCollectionListener(parkId, onCollectionUpdate)
    }
  }
}
