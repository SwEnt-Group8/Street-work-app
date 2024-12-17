package com.android.streetworkapp.utils

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
}
