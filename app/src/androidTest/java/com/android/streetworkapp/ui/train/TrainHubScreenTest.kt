package com.android.streetworkapp.ui.train

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.WorkoutRepository
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class TrainHubScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var userRepository: UserRepository
  private lateinit var userViewModel: UserViewModel
  private lateinit var workoutRepository: WorkoutRepository
  private lateinit var workoutViewModel: WorkoutViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock()
    userRepository = mock()
    workoutRepository = mock()

    // Mock ViewModels
    userViewModel =
        UserViewModel(userRepository).apply {
          setCurrentUser(
              User("testUid", "testUsername", "testEmail", 0, emptyList(), "", parks = listOf("")))
        }
    workoutViewModel = WorkoutViewModel(workoutRepository)
  }

  @Test
  fun trainHubScreen_componentsAreDisplayed() {
    composeTestRule.setContent {
      TrainHubScreen(
          navigationActions = navigationActions,
          workoutViewModel = workoutViewModel,
          userViewModel = userViewModel)
    }

    // Verify Section Titles
    composeTestRule.onNodeWithTag("RoleSelectionTitle").assertExists()
    composeTestRule.onNodeWithTag("ActivitySelectionTitle").assertExists()

    // Verify RoleSelectionGrid
    listOf("Solo", "Coach").forEach { role ->
      composeTestRule.onNodeWithTag("Role_$role").assertExists()
    }

    // Verify ActivitySelectionGrid
    listOf(
            "Push-ups",
            "Dips",
            "Burpee",
            "Lunge",
            "Planks",
            "Handstand",
            "Front lever",
            "Flag",
            "Muscle-up")
        .forEach { activity -> composeTestRule.onNodeWithTag("Activity_$activity").assertExists() }

    // Verify ConfirmButton
    // composeTestRule.onNodeWithTag("ConfirmButton").assertExists()
  }
}
