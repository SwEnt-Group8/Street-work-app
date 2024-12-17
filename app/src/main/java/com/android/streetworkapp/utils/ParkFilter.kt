package com.android.streetworkapp.utils

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.android.streetworkapp.model.park.Park

/**
 * This class add filtering capabilities to a list of parks.
 *
 * @param filterSettings The filter settings.
 */
class ParkFilter(private val filterSettings: FilterSettings) {

  /**
   * Filters a park based on the set filter settings.
   *
   * @param park The park to filter.
   * @return True if the park passes the filter, false otherwise.
   */
  fun filter(park: Park): Boolean {
    // Log.d("ParkFilter", "Filtering park ${park.name}")

    return filterRating(park) && filterEventsDensity(park)
  }

  /**
   * Filters a park according to the required minimal rating (filter settings).
   *
   * @param park The park to filter.
   * @return True if the park passes the filter, false otherwise.
   */
  private fun filterRating(park: Park): Boolean {
    return park.rating >= filterSettings.minRating.value
  }

  /**
   * Filters a park according to its event density (filter settings).
   *
   * @param park The park to filter.
   * @return The filtered list of parks.
   */
  private fun filterEventsDensity(park: Park): Boolean {
    return filterSettings.eventDensity.any { it.isIncluded(park.events.size) }
  }
}

/**
 * Represents the filter settings.
 *
 * @property minRating The minimum rating required.
 * @property eventDensity The required event density.
 */
class FilterSettings {
  val minRating: MutableState<Int> = mutableIntStateOf(DEFAULT_RATING)
  val eventDensity: SnapshotStateList<EventDensity> = DEFAULT_EVENT_DENSITY.toMutableStateList()

  /** The default filter settings. */
  companion object {
    val DEFAULT_RATING = 1
    val DEFAULT_EVENT_DENSITY = listOf(EventDensity.LOW, EventDensity.MEDIUM, EventDensity.HIGH)
  }

  /**
   * Sets the filter settings.
   *
   * @param minRating The minimum rating required.
   * @param eventDensity The required event density.
   */
  fun set(
      minRating: Int? = null,
      eventDensity: List<EventDensity>? = null,
  ) {
    minRating?.let { if (minRating in 1..5) this.minRating.value = it }
    eventDensity.let {
      if (it != null) {
        this.eventDensity.clear()
        this.eventDensity.addAll(it)
      }
    }
  }

  /**
   * Sets the filter settings.
   *
   * @param filterSettings The filter settings.
   */
  fun set(filterSettings: FilterSettings) {
    minRating.value = filterSettings.minRating.value
    this.eventDensity.clear()
    this.eventDensity.addAll(filterSettings.eventDensity)
  }

  /**
   * Updates event density from the filter settings (FilterChips UI).
   *
   * @param density The event density to add or remove.
   */
  fun updateDensity(density: EventDensity) {
    when (density in eventDensity) {
      true -> {
        Log.d("FilterSettings", "Removing $density from list $eventDensity")
        eventDensity.remove(density)
      }
      false -> {
        Log.d("FilterSettings", "Adding $density from list $eventDensity")
        eventDensity.add(density)
      }
    }
  }

  /** Resets the filter settings to their default values (fewest restrictions). */
  fun reset() {
    minRating.value = DEFAULT_RATING
    this.eventDensity.clear()
    this.eventDensity.addAll(DEFAULT_EVENT_DENSITY)
  }
}

/**
 * Represents the density of events in a park.
 *
 * Low: 0-2 events. Medium: 3-6 events. High: 7+ events.
 */
enum class EventDensity {
  LOW,
  MEDIUM,
  HIGH;

  /**
   * Checks if the event count is included in the density.
   *
   * @param eventCount The number of events.
   */
  fun isIncluded(eventCount: Int): Boolean {
    return when (this) {
      LOW -> eventCount in 0..2
      MEDIUM -> eventCount in 3..6
      HIGH -> eventCount >= 7
    }
  }
}
