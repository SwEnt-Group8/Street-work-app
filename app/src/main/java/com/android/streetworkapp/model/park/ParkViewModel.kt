package com.android.streetworkapp.model.park

import androidx.lifecycle.ViewModel

open class ParkViewModel(private val repository: ParkRepository) : ViewModel() {

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
  suspend fun getParkByPid(pid: String): Park? {
    return repository.getParkByPid(pid)
  }

  /**
   * Get a park by its location ID.
   *
   * @param locationId The location ID.
   * @return The park with the given location ID, or null if the park does not exist.
   */
  suspend fun getParkByLocationId(locationId: String): Park? {
    return repository.getParkByLocationId(locationId)
  }

  /**
   * Create a park.
   *
   * @param park The park to create.
   */
  suspend fun createPark(park: Park) {
    repository.createPark(park)
  }

  /**
   * Update the name of a park.
   *
   * @param pid The park ID.
   * @param name The new name.
   */
  suspend fun updateName(pid: String, name: String) {
    repository.updateName(pid, name)
  }

  /**
   * Update the image reference of a park.
   *
   * @param pid The park ID.
   * @param imageReference The new image reference.
   */
  suspend fun updateImageReference(pid: String, imageReference: String) {
    repository.updateImageReference(pid, imageReference)
  }

  /**
   * Add a rating to a park.
   *
   * @param pid The park ID.
   * @param rating The rating to add.
   */
  suspend fun addRating(pid: String, rating: Int) {
    repository.addRating(pid, rating)
  }

  /**
   * Delete a rating from a park.
   *
   * @param pid The park ID.
   * @param rating The rating to delete.
   */
  suspend fun deleteRating(pid: String, rating: Int) {
    repository.deleteRating(pid, rating)
  }

  /**
   * Update the capacity of a park.
   *
   * @param pid The park ID.
   * @param capacity The new capacity.
   */
  suspend fun updateCapacity(pid: String, capacity: Int) {
    repository.updateCapacity(pid, capacity)
  }

  /**
   * Increment the occupancy of a park.
   *
   * @param pid The park ID.
   */
  suspend fun incrementOccupancy(pid: String) {
    repository.incrementOccupancy(pid)
  }

  /**
   * Decrement the occupancy of a park.
   *
   * @param pid The park ID.
   */
  suspend fun decrementOccupancy(pid: String) {
    repository.decrementOccupancy(pid)
  }

  /**
   * Add an event to a park.
   *
   * @param pid The park ID.
   * @param eid The event ID.
   */
  suspend fun addEventToPark(pid: String, eid: String) {
    repository.addEventToPark(pid, eid)
  }

  /**
   * Delete an event from a park.
   *
   * @param pid The park ID.
   * @param eid The event ID.
   */
  suspend fun deleteEventFromPark(pid: String, eid: String) {
    repository.deleteEventFromPark(pid, eid)
  }

  /**
   * Delete a park by its ID.
   *
   * @param pid The park ID.
   */
  suspend fun deleteParkByPid(pid: String) {
    repository.deleteParkByPid(pid)
  }
}
