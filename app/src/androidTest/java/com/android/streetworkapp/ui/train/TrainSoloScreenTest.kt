package com.android.streetworkapp.ui.train

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigator
import androidx.navigation.compose.ComposeNavigator
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.WorkoutRepository
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class TrainSoloScreenTest {

  private lateinit var workoutRepository: WorkoutRepository
  private lateinit var workoutViewModel: WorkoutViewModel
  private lateinit var userRepository: UserRepository
  private lateinit var userViewModel: UserViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    workoutRepository = mock()
    workoutViewModel = WorkoutViewModel(workoutRepository)
    userRepository = mock()
    userViewModel = UserViewModel(userRepository)
  }

  @Test
  fun trainSoloScreen_componentsAreDisplayed_forTimeDependent() {
    composeTestRule.setContent {
      TrainSoloScreen(
          activity = "Push-ups",
          isTimeDependent = true,
          time = 60,
          sets = 5,
          reps = 10,
          userViewModel = userViewModel,
          workoutViewModel = workoutViewModel)
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
          activity = "Push-ups",
          isTimeDependent = false,
          workoutViewModel = workoutViewModel,
          time = 60,
          sets = 5,
          reps = 10,
          userViewModel = userViewModel)
    }

    // Verify core components
    composeTestRule.onNodeWithTag("TrainSoloScreen").assertExists()
    composeTestRule.onNodeWithTag("TrainSoloTitle").assertExists()
    composeTestRule.onNodeWithTag("ActivityText").assertExists()
    composeTestRule.onNodeWithTag("TimeDependentText").assertExists()

    // Verify Counter components
    composeTestRule.onNodeWithTag("CounterText").assertExists()
  }

  @Test
  fun trainSoloScreen_stopButtonUpdatesState() {
    composeTestRule.setContent {
      TrainSoloScreen(
          activity = "Push-ups",
          isTimeDependent = true,
          userViewModel = userViewModel,
          time = 1,
          sets = 1,
          reps = 1,
          workoutViewModel = workoutViewModel)
    }

    composeTestRule.onNodeWithTag("StopButton").assertExists().performClick()

    composeTestRule.onNodeWithTag("TimeUpText").assertExists().assertIsDisplayed()
  }
}
