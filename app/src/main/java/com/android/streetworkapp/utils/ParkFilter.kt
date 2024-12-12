package com.android.streetworkapp.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.android.streetworkapp.model.park.Park
import com.google.firebase.Timestamp

class ParkFilter(
    private val minRating: MutableState<Int> = mutableIntStateOf(1),
    private val minEvents: MutableState<EventDensity> = mutableStateOf(EventDensity.LOW),
    private val eventStatus: List<EventStatus> = listOf(EventStatus.CREATED, EventStatus.ONGOING),
    private val shouldNotBeFull: MutableState<Boolean> = mutableStateOf(false)
) {
  fun isParkIncluded(park: Park): Boolean {
    return filterRating(park) &&
        filterEventsDensity(park) &&
        filterEventStatus(park) &&
        filterFullEvent(park)
  }

  private fun filterRating(park: Park): Boolean {
    return park.rating >= minRating.value
  }

  private fun filterEventsDensity(park: Park): Boolean {
    return minEvents.value.isIncluded(park.events.size)
  }

  private fun filterEventStatus(park: Park): Boolean {
    return eventStatus.any { /* EventViewMocel => it.isStatus(park.events[0].date) */
      it == it
    }
  }

  private fun filterFullEvent(park: Park): Boolean {
    return !shouldNotBeFull.value ||
        park.events.any { /* EventViewModel => it.participants < it.maxParticipants */
          it == it
        }
  }
}

// TODO : Need to define these thresholds
enum class EventDensity {
  LOW,
  MEDIUM,
  HIGH;

  fun isIncluded(eventCount: Int): Boolean {
    return when (this) {
      LOW -> eventCount in 0 .. 2
      MEDIUM -> eventCount in 3..7
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
      CREATED -> eventDate.seconds < Timestamp.now().seconds
      ONGOING -> eventDate.seconds == Timestamp.now().seconds
      FINISHED -> eventDate.seconds < Timestamp.now().seconds
    }
  }
}
