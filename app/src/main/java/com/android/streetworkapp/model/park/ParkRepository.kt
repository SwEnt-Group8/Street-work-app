package com.android.streetworkapp.model.park

import android.media.Image

/** A repository interface for park data. */
interface ParkRepository {

  fun getNewPid(): String

  suspend fun getParkByPid(pid: String): Park?

  suspend fun getParkByLocationId(locationId: String): Park?

  suspend fun createPark(park: Park)

  suspend fun updateName(pid: String, name: String)

  suspend fun updateImage(pid: String, image: Image)

  suspend fun addRating(pid: String, rating: Int)

  suspend fun deleteRating(pid: String, rating: Int)

  suspend fun updateCapacity(pid: String, capacity: Int)

  suspend fun incrementOccupancy(pid: String)

  suspend fun decrementOccupancy(pid: String)

  suspend fun addEventToPark(pid: String, eid: String)

  suspend fun deleteEventFromPark(pid: String, eid: String)

  suspend fun deleteParkByPid(pid: String)
}
