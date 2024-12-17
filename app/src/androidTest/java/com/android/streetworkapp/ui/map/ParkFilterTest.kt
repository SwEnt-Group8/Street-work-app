package com.android.streetworkapp.ui.map

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.sample.R
import com.android.streetworkapp.utils.EventDensity
import com.android.streetworkapp.utils.FilterSettings
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ParkFilterTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var filterSettings: FilterSettings
  private lateinit var context: Context

  @Before
  fun setUp() {
    filterSettings = FilterSettings()
    composeTestRule.setContent {
      ParkFilterSettings(filterSettings)
      context = LocalContext.current
    }
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.onNodeWithTag("ratingFilterTitle").assertExists()
    composeTestRule.onNodeWithTag("ratingComponent").assertExists()
    composeTestRule.onNodeWithTag("eventDensityFilterTitle").assertExists()
    composeTestRule.onNodeWithTag("lowDensityFilterChip").assertExists()
    composeTestRule.onNodeWithTag("mediumDensityFilterChip").assertExists()
    composeTestRule.onNodeWithTag("highDensityFilterChip").assertExists()
    composeTestRule.onNodeWithTag("resetButton").assertExists()

    // Check the default values being correctly displayed
    composeTestRule
        .onNodeWithTag("ratingFilterTitle")
        .assertTextEquals(
            context.getString(R.string.rating_filter_title, FilterSettings.DEFAULT_RATING))
    composeTestRule
        .onNodeWithTag("eventDensityFilterTitle")
        .assertTextEquals(context.getString(R.string.eventDensity_filter_title))

    // Chips all enabled by default
    composeTestRule.onNodeWithTag("lowDensityFilterChip").assertIsSelected()
    composeTestRule.onNodeWithTag("mediumDensityFilterChip").assertIsSelected()
    composeTestRule.onNodeWithTag("highDensityFilterChip").assertIsSelected()
  }

  @Test
  fun isEventFilterInteractionCorrect() {

    assert(filterSettings.eventDensity.size == 3)

    // Click on the chips to change the filter settings :
    composeTestRule.onNodeWithTag("lowDensityFilterChip").performClick()
    composeTestRule.onNodeWithTag("mediumDensityFilterChip").performClick()
    composeTestRule.onNodeWithTag("highDensityFilterChip").performClick()

    composeTestRule.waitForIdle()

    // Check if the chips are selected :
    composeTestRule.onNodeWithTag("lowDensityFilterChip").assertIsNotSelected()
    composeTestRule.onNodeWithTag("mediumDensityFilterChip").assertIsNotSelected()
    composeTestRule.onNodeWithTag("highDensityFilterChip").assertIsNotSelected()

    assert(filterSettings.eventDensity.size == 0)

    composeTestRule.onNodeWithTag("lowDensityFilterChip").performClick()

    composeTestRule.waitForIdle()

    assert(filterSettings.eventDensity.contains(EventDensity.LOW))
    assert(!filterSettings.eventDensity.contains(EventDensity.MEDIUM))
    assert(!filterSettings.eventDensity.contains(EventDensity.HIGH))

    composeTestRule.onNodeWithTag("mediumDensityFilterChip").performClick()

    composeTestRule.waitForIdle()

    assert(filterSettings.eventDensity.contains(EventDensity.LOW))
    assert(filterSettings.eventDensity.contains(EventDensity.MEDIUM))
    assert(!filterSettings.eventDensity.contains(EventDensity.HIGH))

    composeTestRule.onNodeWithTag("highDensityFilterChip").performClick()

    composeTestRule.waitForIdle()

    assert(filterSettings.eventDensity.contains(EventDensity.LOW))
    assert(filterSettings.eventDensity.contains(EventDensity.MEDIUM))
    assert(filterSettings.eventDensity.contains(EventDensity.HIGH))

    // Click on the reset button :
    composeTestRule.onNodeWithTag("resetButton").performClick()

    composeTestRule.waitForIdle()

    // Check if the chips are selected :
    composeTestRule.onNodeWithTag("lowDensityFilterChip").assertIsSelected()
    composeTestRule.onNodeWithTag("mediumDensityFilterChip").assertIsSelected()
    composeTestRule.onNodeWithTag("highDensityFilterChip").assertIsSelected()
  }
}
