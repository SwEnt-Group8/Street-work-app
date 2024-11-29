package com.android.streetworkapp.ui.train

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.android.streetworkapp.model.workout.WorkoutRepository
import com.android.streetworkapp.model.workout.WorkoutViewModel
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class TrainChallengeScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockWorkoutRepository = mock<WorkoutRepository>()
  private val workoutViewModel = WorkoutViewModel(mockWorkoutRepository)

  @Test
  fun trainChallengeScreen_displaysCorrectInformation() {
    val testActivity = "Handstand"
    val testIsTimeDependent = false

    composeTestRule.setContent {
      TrainChallengeScreen(
          activity = testActivity,
          isTimeDependent = testIsTimeDependent,
          workoutViewModel = workoutViewModel)
    }

    // Verify the static title
    composeTestRule.onNodeWithText("Train Challenge").assertExists().assertIsDisplayed()

    // Verify the activity text
    composeTestRule.onNodeWithText("Activity: $testActivity").assertExists().assertIsDisplayed()

    // Verify the time-dependent text
    composeTestRule
        .onNodeWithText("Time Dependent: $testIsTimeDependent")
        .assertExists()
        .assertIsDisplayed()
  }
}
