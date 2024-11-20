package com.android.streetworkapp.ui.Progression

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.streetworkapp.model.progression.ExerciseAchievement
import com.android.streetworkapp.model.progression.MedalsAchievement
import com.android.streetworkapp.model.progression.Progression
import com.android.streetworkapp.model.progression.ProgressionRepositoryFirestore
import com.android.streetworkapp.model.progression.ProgressionViewModel
import com.android.streetworkapp.model.progression.Ranks
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepositoryFirestore
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.progress.ProgressScreen
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProgressionTest {

  @MockK private lateinit var navigationActions: NavigationActions

  @MockK private lateinit var userRepository: UserRepositoryFirestore
  private lateinit var userViewModel: UserViewModel

  @MockK private lateinit var progressionRepository: ProgressionRepositoryFirestore
  private lateinit var progressionViewModel: ProgressionViewModel

  @get:Rule val composeTestRule = createComposeRule()

  private val mockedUser =
      User(
          uid = "123456",
          username = "john_doe",
          email = "john.doe@example.com",
          score = Ranks.BRONZE.score + (Ranks.SILVER.score - Ranks.BRONZE.score) / 2,
          friends = listOf("friend_1", "friend_2", "friend_3"),
          picture = "")

  @Before
  fun setUp() {
    MockKAnnotations.init(this, relaxed = true)
    userViewModel = UserViewModel(userRepository)

    userViewModel.setCurrentUser(mockedUser)

    progressionViewModel = ProgressionViewModel(progressionRepository)
  }

  @Test
  fun isScreenDisplayed() {

    coEvery { progressionRepository.getOrAddProgression(eq(mockedUser.uid)) } answers
        {
          Progression()
        }
    composeTestRule.setContent {
      ProgressScreen(navigationActions, userViewModel, progressionViewModel)
    }
    composeTestRule.onNodeWithTag("progressionScreen").assertIsDisplayed()
  }

  @Test
  fun areAllComponentsDisplayed() {

    coEvery { progressionRepository.getOrAddProgression(eq(mockedUser.uid)) } answers
        {
          Progression()
        }

    composeTestRule.setContent {
      ProgressScreen(navigationActions, userViewModel, progressionViewModel)
    }
    composeTestRule.onNodeWithTag("progressionScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("circularProgressBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("percentageInsideCircularProgressBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("scoreTextUnderCircularProgressBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("metricCardScoreValue").assertIsDisplayed()
    composeTestRule.onNodeWithTag("metricCardParksVisitedValue").assertIsDisplayed()
    composeTestRule.onNodeWithTag("metricCardFriendsAddedValue").assertIsDisplayed()
  }

  @Test
  fun screenDisplaysCorrectElementsOnTypicalInputWithNonEmptyAchievements() {

    val mockedProgression =
        Progression(
            progressionId = "prog123456",
            uid = mockedUser.uid,
            currentGoal = Ranks.SILVER.score,
            eventsCreated = 9,
            eventsJoined = 14,
            achievements = listOf(MedalsAchievement.BRONZE.toString()))

    coEvery { progressionRepository.getOrAddProgression(eq(mockedUser.uid)) } answers
        {
          mockedProgression
        }

    composeTestRule.setContent {
      ProgressScreen(navigationActions, userViewModel, progressionViewModel)
    }

    composeTestRule.onNodeWithTag("AchievementTab").performClick()

    composeTestRule
        .onNodeWithTag("percentageInsideCircularProgressBar")
        .assertTextEquals(
            "${(mockedUser.score / mockedProgression.currentGoal.toFloat() * 100).toInt()}%")
    composeTestRule
        .onNodeWithTag("scoreTextUnderCircularProgressBar")
        .assertTextEquals("${mockedUser.score}/${mockedProgression.currentGoal}")

    composeTestRule.onNodeWithTag("metricCardScoreValue").assertTextEquals("${mockedUser.score}")
    // composeTestRule.onNodeWithTag("metricCardParksVisitedValue").assertTextEquals(INPUT_VALUE)
    // TODO: commented out as it is not yet implemented
    composeTestRule
        .onNodeWithTag("metricCardFriendsAddedValue")
        .assertTextEquals("${mockedUser.friends.size}")

    composeTestRule.onNodeWithTag("emptyAchievementsText").assertIsNotDisplayed()

    mockedProgression.achievements.forEachIndexed { index, achievementName
      -> // only one type of achievement for now, but since we'll scale things later I make the test
      // easily
      // scalable
      val achievement = enumValueOf<MedalsAchievement>(achievementName).achievement
      val achievementItem = composeTestRule.onNodeWithTag("achievementItem${index}")
    }
  }

  @Test
  fun screenDisplaysEmptyAchievementsTextIfNoAchievements() {
    val mockedProgression =
        Progression(
            progressionId = "prog123456",
            uid = mockedUser.uid,
            currentGoal = Ranks.BRONZE.score,
            eventsCreated = 0,
            eventsJoined = 0,
            achievements = emptyList())

    coEvery { progressionRepository.getOrAddProgression(eq(mockedUser.uid)) } answers
        {
          mockedProgression
        }

    composeTestRule.setContent {
      ProgressScreen(navigationActions, userViewModel, progressionViewModel)
    }
    composeTestRule.onNodeWithTag("AchievementTab").performClick()

    composeTestRule.onNodeWithTag("emptyAchievementsText").assertIsDisplayed()
  }

  @Test
  fun screenDisplaysExercisesByDefault() {
    val mockedProgression =
        Progression(
            progressionId = "prog123456",
            uid = mockedUser.uid,
            currentGoal = Ranks.BRONZE.score,
            eventsCreated = 0,
            eventsJoined = 0,
            achievements = emptyList())

    coEvery { progressionRepository.getOrAddProgression(eq(mockedUser.uid)) } answers
        {
          mockedProgression
        }

    composeTestRule.setContent {
      ProgressScreen(navigationActions, userViewModel, progressionViewModel)
    }

    enumValues<ExerciseAchievement>().forEach { exercise ->
      composeTestRule.onNodeWithTag("exerciseItem" + exercise.name).assertExists()
    }
  }
}
