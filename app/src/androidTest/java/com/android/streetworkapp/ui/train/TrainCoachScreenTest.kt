package com.android.streetworkapp.ui.train

import androidx.compose.ui.test.junit4.createComposeRule
import com.android.streetworkapp.model.workout.WorkoutRepository
import com.android.streetworkapp.model.workout.WorkoutViewModel
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class TrainCoachScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val mockWorkoutRepository = mock<WorkoutRepository>()
  private val workoutViewModel = WorkoutViewModel(mockWorkoutRepository)

  @Test fun trainCoachScreen_displaysCorrectInformation() {}
}
