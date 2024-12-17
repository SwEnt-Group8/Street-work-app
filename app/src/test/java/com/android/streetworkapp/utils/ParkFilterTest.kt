package com.android.streetworkapp.utils

import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.android.streetworkapp.utils.FilterSettings.Companion.DEFAULT_EVENT_DENSITY
import org.junit.Test

class ParkFilterTest {

  private fun verifyListEquals(list1: List<EventDensity>, list2: List<EventDensity>) {
    assert(list1.size == list2.size)
    for (elem in list1) assert(list2.contains(elem))
  }

  @Test
  fun isSettingsAPICorrect() {
    val filterSettings = FilterSettings()

    // Verify the default values :
    assert(filterSettings.minRating.value == FilterSettings.DEFAULT_RATING)
    verifyListEquals(filterSettings.eventDensity, DEFAULT_EVENT_DENSITY)

    // Change the values :
    filterSettings.set(minRating = 3, eventDensity = listOf(EventDensity.LOW))
    assert(filterSettings.minRating.value == 3)
    verifyListEquals(filterSettings.eventDensity, listOf(EventDensity.LOW))

    // Change using new filter :
    val newFilterSettings = FilterSettings()
    newFilterSettings.set(minRating = 4, eventDensity = listOf(EventDensity.HIGH))

    filterSettings.set(newFilterSettings)
    assert(filterSettings.minRating.value == 4)
    verifyListEquals(filterSettings.eventDensity, listOf(EventDensity.HIGH))

    // Reset the values :
    filterSettings.reset()
    assert(filterSettings.minRating.value == FilterSettings.DEFAULT_RATING)
    verifyListEquals(filterSettings.eventDensity, DEFAULT_EVENT_DENSITY)

    // Testing the event density :

    val LOW_LIST = listOf(0, 1, 2)
    val MEDIUM_LIST = listOf(3, 4, 5, 6)
    val HIGH_LIST = listOf(7, 8, 9, 10)

    for (i in 0..10) {
      assert(EventDensity.LOW.isIncluded(i) == LOW_LIST.contains(i))
      assert(EventDensity.MEDIUM.isIncluded(i) == MEDIUM_LIST.contains(i))
      assert(EventDensity.HIGH.isIncluded(i) == HIGH_LIST.contains(i))
    }
  }

  private fun createSimplerParks(rating: Float, events: List<String>): Park {
    return Park(
        "pid", "name", ParkLocation(0.0, 0.0, ""), "", rating, 0, 0, 0, events, emptyList(), "")
  }

  @Test
  fun isParkFilterAPICorrect() {
    val filterSettings = FilterSettings()
    val parkFilter = ParkFilter(filterSettings)

    val badly_rated_park = createSimplerParks(1.0f, emptyList())
    val simple_park = createSimplerParks(3.0f, listOf("event_1", "event_2", "event_3"))
    val good_park = createSimplerParks(5.0f, listOf("event_1", "event_2", "event_3", "event_4"))

    assert(parkFilter.filter(badly_rated_park))
    assert(parkFilter.filter(simple_park))
    assert(parkFilter.filter(good_park))

    // Medium-filters :

    filterSettings.set(minRating = 3)
    assert(!parkFilter.filter(badly_rated_park))
    assert(parkFilter.filter(simple_park))
    assert(parkFilter.filter(good_park))


    filterSettings.reset()
    filterSettings.set(eventDensity = listOf(EventDensity.MEDIUM))
    assert(!parkFilter.filter(badly_rated_park))
    assert(parkFilter.filter(simple_park))
    assert(parkFilter.filter(good_park))

    // High-filters :

    filterSettings.set(minRating = 4)
    assert(!parkFilter.filter(badly_rated_park))
    assert(!parkFilter.filter(simple_park))
    assert(parkFilter.filter(good_park))
  }
}
