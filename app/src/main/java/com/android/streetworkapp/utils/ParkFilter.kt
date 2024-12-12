package com.android.streetworkapp.utils

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.park.Park
import com.google.firebase.Timestamp

class ParkFilter(private val filterSettings: FilterSettings) {

  fun filter(eventList: List<Event>, park: Park): Boolean {
    // Log.d("ParkFilter", "Filtering park ${park.name}")

    return filterRating(park) &&
        filterEventsDensity(park) &&
        filterEventStatus(eventList) &&
        filterFullEvent(eventList)
  }

  private fun filterRating(park: Park): Boolean {
    return park.rating >= filterSettings.minRating.value
  }

  private fun filterEventsDensity(park: Park): Boolean {
    return filterSettings.minEvents.value.isIncluded(park.events.size)
  }

  /**
   * Filter the events based on the status. There should be at least one event that matches the
   * desired status.
   *
   * @param eventList The list of events of the park.
   */
  private fun filterEventStatus(eventList: List<Event>): Boolean {

    when (eventList.isEmpty()) {
      true -> return true
      false ->
          return eventList.any { event ->
            Log.d(
                "ParkFilter",
                "Filtering events with status ${filterSettings.eventStatus} for event $event")
            filterSettings.eventStatus.any { it.isStatus(event.date) }
          }
    }
  }

  /**
   * Filter the events based on the full status. If a spot is desired, there should be at least one
   * event that is not full.
   *
   * @param eventList The list of events of the park.
   */
  private fun filterFullEvent(eventList: List<Event>): Boolean {
    // Log.d(
    //    "ParkFilter",
    //   "Filtering fullness of events with rule shouldNotBeFull =
    // ${filterSettings.shouldNotBeFull.value}")
    if (!filterSettings.shouldNotBeFull.value) return true
    else {
      Log.d("ParkFilter", "Searching non-full events {$eventList}")
      return when (eventList.isEmpty()) {
        true -> false // No events => no event with remaining spots
        false -> eventList.any { it.participants < it.maxParticipants }
      }
    }
  }
}

class FilterSettings {
  val minRating: MutableState<Int> = mutableIntStateOf(1)
  val minEvents: MutableState<EventDensity> = mutableStateOf(EventDensity.LOW)
  val eventStatus: SnapshotStateList<EventStatus> =
      mutableStateListOf(EventStatus.CREATED, EventStatus.ONGOING, EventStatus.FINISHED)
  val shouldNotBeFull: MutableState<Boolean> = mutableStateOf(false)

  fun set(
      minRating: Int? = null,
      minEvents: EventDensity? = null,
      eventStatus: List<EventStatus>? = null,
      shouldNotBeFull: Boolean? = null
  ) {
    minRating?.let { if (minRating in 1..5) this.minRating.value = it }
    minEvents?.let { this.minEvents.value = it }
    eventStatus?.let {
      this.eventStatus.clear()
      this.eventStatus.addAll(it)
    }
    shouldNotBeFull?.let { this.shouldNotBeFull.value = it }
  }

  fun set(filterSettings: FilterSettings) {
    minRating.value = filterSettings.minRating.value
    minEvents.value = filterSettings.minEvents.value
    eventStatus.clear()
    eventStatus.addAll(filterSettings.eventStatus)
    shouldNotBeFull.value = filterSettings.shouldNotBeFull.value
  }

  fun reset() {
    minRating.value = 1
    minEvents.value = EventDensity.LOW
    eventStatus.clear()
    eventStatus.addAll(listOf(EventStatus.CREATED, EventStatus.ONGOING, EventStatus.FINISHED))
    shouldNotBeFull.value = false
  }
}

// TODO : Need to define these thresholds
enum class EventDensity {
  LOW,
  MEDIUM,
  HIGH;

  fun getThreshold(): Int {
    return when (this) {
      LOW -> 3
      MEDIUM -> 7
      HIGH -> 999
    }
  }

  fun isIncluded(eventCount: Int): Boolean {
    return when (this) {
      LOW -> eventCount in 0..2
      MEDIUM -> eventCount in 3..6
      HIGH -> eventCount > 7
    }
  }
}

// TODO : This is a placeholder waiting for EVENT-side implementation
enum class EventStatus {
  CREATED,
  ONGOING,
  FINISHED;

  fun isStatus(eventDate: Timestamp): Boolean {
    return when (this) {
      CREATED -> eventDate.seconds > Timestamp.now().seconds
      ONGOING -> eventDate.seconds == Timestamp.now().seconds
      FINISHED -> eventDate.seconds < Timestamp.now().seconds
    }
  }

  companion object {
    fun addOrRemove(statusList: SnapshotStateList<EventStatus>, status: EventStatus) {

      when (statusList.contains(status)) {
        true -> {
          Log.d("ParkFilter", "Removing $status from list $statusList when called with $status")
          statusList.remove(status)
        }
        false -> {
          Log.d("ParkFilter", "Adding $status from list $statusList when called with $status")
          statusList.add(status)
        }
      }
    }
  }
}
