package com.android.streetworkapp.utils

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.android.streetworkapp.model.park.Park
import com.google.firebase.Timestamp

class ParkFilter(private val filterSettings: FilterSettings) {

  fun filter(park: Park): Boolean {
    return filterRating(park) &&
        filterEventsDensity(park) &&
        filterEventStatus(park) &&
        filterFullEvent(park)
  }

  private fun filterRating(park: Park): Boolean {
    return park.rating >= filterSettings.minRating.value
  }

  private fun filterEventsDensity(park: Park): Boolean {
    return filterSettings.minEvents.value.isIncluded(park.events.size)
  }

  private fun filterEventStatus(park: Park): Boolean {
    return filterSettings.eventStatus.any { /* EventViewMocel => it.isStatus(park.events[0].date) */
      it == it
    }
  }

  private fun filterFullEvent(park: Park): Boolean {
    return !filterSettings.shouldNotBeFull.value ||
        park.events.any { /* EventViewModel => it.participants < it.maxParticipants */
          it == it
        }
  }
}

class FilterSettings {
  val minRating: MutableState<Int> = mutableIntStateOf(1)
  val minEvents: MutableState<EventDensity> = mutableStateOf(EventDensity.LOW)
  val eventStatus = mutableStateListOf(EventStatus.CREATED, EventStatus.ONGOING)
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
    eventStatus.addAll(listOf(EventStatus.CREATED, EventStatus.ONGOING))
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

  fun atLeast(eventCount: Int): Boolean {
    return isIncluded(eventCount) || eventCount > getThreshold()
  }
}

// TODO : This is a placeholder waiting for EVENT-side implementation
enum class EventStatus {
  CREATED,
  ONGOING,
  FINISHED;

  fun isStatus(eventDate: Timestamp): Boolean {
    return when (this) {
      CREATED -> eventDate.seconds < Timestamp.now().seconds
      ONGOING -> eventDate.seconds == Timestamp.now().seconds
      FINISHED -> eventDate.seconds < Timestamp.now().seconds
    }
  }

  companion object {
    fun makeSettingsArg(statusList: List<Boolean>): List<EventStatus> {
      val list = mutableListOf<EventStatus>()

      if (statusList[0]) list.add(CREATED)
      if (statusList[1]) list.add(ONGOING)
      if (statusList[2]) list.add(FINISHED)

      return list
    }

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
