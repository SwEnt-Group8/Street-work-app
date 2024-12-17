package com.android.streetworkapp.ui.train

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.WorkoutRepository
import com.android.streetworkapp.model.workout.WorkoutViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class TrainCoachScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockWorkoutRepository = mock<WorkoutRepository>()
    private lateinit var workoutViewModel: WorkoutViewModel

    private val mockUserRepository = mock<UserRepository>()
    private lateinit var userViewModel: UserViewModel

    @Before
    fun setUp() = runBlocking {
        val testUser = User("testUser", "Test User", "email@mail.com", 2, emptyList(), "picture")
        whenever(mockUserRepository.getUserByUid("testUser")).thenReturn(testUser)
        // Mock the flow for testUser
        whenever(mockWorkoutRepository.observePairingRequests("testUser"))
            .thenReturn(MutableStateFlow(emptyList()))

        // Initialize the VMs
        workoutViewModel = WorkoutViewModel(mockWorkoutRepository)
        userViewModel = UserViewModel(mockUserRepository)

        // Set the current user in userViewModel if needed
        userViewModel.setCurrentUser(testUser)
    }

    @Test
    fun trainCoachScreen_displaysRoleDialog() {
        composeTestRule.setContent {
            TrainCoachScreen(
                activity = "Push-ups",
                isTimeDependent = true,
                reps = 10,
                time = 60,
                workoutViewModel = workoutViewModel,
                userViewModel = userViewModel
            )
        }

        // Verify Role Dialog elements
        composeTestRule.onNodeWithTag("DialogTitle").assertIsDisplayed()
        composeTestRule.onNodeWithTag("RoleSwitch").performClick()
        composeTestRule.onNodeWithTag("ConfirmButton").performClick()
    }

    @Test
    fun trainCoachScreen_displaysCoachView() {
        composeTestRule.setContent {
            CoachView(
                isTimeDependent = false,
                reps = 20,
                workoutViewModel = workoutViewModel,
                userViewModel = userViewModel
            )
        }

        // Verify Coach View elements
        composeTestRule.onNodeWithTag("IncrementButton").performClick()
        composeTestRule.onNodeWithTag("DecrementButton").performClick()
        composeTestRule.onNodeWithTag("EndSessionButton").performClick()
    }
}
