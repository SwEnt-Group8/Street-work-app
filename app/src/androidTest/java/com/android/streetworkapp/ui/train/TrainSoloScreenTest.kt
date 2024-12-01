package com.android.streetworkapp.ui.train

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.streetworkapp.model.workout.WorkoutRepository
import com.android.streetworkapp.model.workout.WorkoutViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class TrainSoloScreenTest {

  private lateinit var workoutRepository: WorkoutRepository
  private lateinit var workoutViewModel: WorkoutViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    workoutRepository = mock()
    workoutViewModel = WorkoutViewModel(workoutRepository)
  }

  @Test
  fun trainSoloScreen_componentsAreDisplayed_forTimeDependent() {
    composeTestRule.setContent {
      TrainSoloScreen(
          activity = "Push-ups", isTimeDependent = true, workoutViewModel = workoutViewModel)
    }

    // Verify core components
    composeTestRule.onNodeWithTag("TrainSoloScreen").assertExists()
    composeTestRule.onNodeWithTag("TrainSoloTitle").assertExists()
    composeTestRule.onNodeWithTag("ActivityText").assertExists()
    composeTestRule.onNodeWithTag("TimeDependentText").assertExists()

    // Verify Timer components
    composeTestRule.onNodeWithTag("StopButton").assertExists()
  }

  @Test
  fun trainSoloScreen_componentsAreDisplayed_forNonTimeDependent() {
    composeTestRule.setContent {
      TrainSoloScreen(
          activity = "Push-ups", isTimeDependent = false, workoutViewModel = workoutViewModel)
    }

    // Verify core components
    composeTestRule.onNodeWithTag("TrainSoloScreen").assertExists()
    composeTestRule.onNodeWithTag("TrainSoloTitle").assertExists()
    composeTestRule.onNodeWithTag("ActivityText").assertExists()
    composeTestRule.onNodeWithTag("TimeDependentText").assertExists()

    // Verify Counter components
    composeTestRule.onNodeWithTag("CounterText").assertExists()
    composeTestRule.onNodeWithTag("DecrementButton").assertExists()
    composeTestRule.onNodeWithTag("IncrementButton").assertExists()
  }

  @Test
  fun trainSoloScreen_stopButtonUpdatesState() {
    composeTestRule.setContent {
      TrainSoloScreen(
          activity = "Push-ups", isTimeDependent = true, workoutViewModel = workoutViewModel)
    }

    // Click "I stopped" button
    composeTestRule.onNodeWithTag("StopButton").performClick()

    // Verify updated state
    composeTestRule.onNodeWithTag("TimeUpText").assertExists()
  }

  @Test
  fun trainSoloScreen_counterUpdatesState() {
    composeTestRule.setContent {
      TrainSoloScreen(
          activity = "Push-ups", isTimeDependent = false, workoutViewModel = workoutViewModel)
    }

    // Verify initial state
    composeTestRule.onNodeWithTag("CounterText").assertExists()

    // Increment counter
    composeTestRule.onNodeWithTag("IncrementButton").performClick()

    // Verify counter updated
    composeTestRule.onNodeWithTag("CounterText").assertExists()

    // Decrement counter
    composeTestRule.onNodeWithTag("DecrementButton").performClick()

    // Verify counter updated
    composeTestRule.onNodeWithTag("CounterText").assertExists()
  }
}
