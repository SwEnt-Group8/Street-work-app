package com.android.streetworkapp.model.park

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

open class ParkViewModel(private val repository: ParkRepository) : ViewModel() {

  // LiveData of the current park
  private val _currentPark = MutableLiveData<Park?>()
  val currentPark: LiveData<Park?>
    get() = _currentPark

  private val _park = MutableLiveData<Park?>()
  val park: LiveData<Park?>
    get() = _park

  /**
   * Set the current park.
   *
   * @param park The park to set as the current park.
   */
  fun setCurrentPark(park: Park?) {
    _currentPark.value = park
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
      _currentPark.postValue(fetchedPark)
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
      _currentPark.postValue(fetchedPark)
    }
  }

  /**
   * Create a park.
   *
   * @param park The park to create.
   */
  fun createPark(park: Park) = viewModelScope.launch { repository.createPark(park) }

  /**
   * Update the name of a park.
   *
   * @param pid The park ID.
   * @param name The new name.
   */
  fun updateName(pid: String, name: String) =
      viewModelScope.launch { repository.updateName(pid, name) }

  /**
   * Update the image reference of a park.
   *
   * @param pid The park ID.
   * @param imageReference The new image reference.
   */
  fun updateImageReference(pid: String, imageReference: String) =
      viewModelScope.launch { repository.updateImageReference(pid, imageReference) }

  /**
   * Add a rating to a park.
   *
   * @param pid The park ID.
   * @param rating The rating to add.
   */
  fun addRating(pid: String, rating: Int) =
      viewModelScope.launch { repository.addRating(pid, rating) }

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
   * Delete a park by its ID.
   *
   * @param pid The park ID.
   */
  fun deleteParkByPid(pid: String) = viewModelScope.launch { repository.deleteParkByPid(pid) }
}
