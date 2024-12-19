package com.android.streetworkapp.end2end

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.rule.GrantPermissionRule
import com.android.streetworkapp.StreetWorkApp
import com.android.streetworkapp.model.event.EventRepository
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.image.ImageRepository
import com.android.streetworkapp.model.image.ImageViewModel
import com.android.streetworkapp.model.moderation.TextModerationRepository
import com.android.streetworkapp.model.moderation.TextModerationViewModel
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.ParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.preferences.PreferencesRepository
import com.android.streetworkapp.model.preferences.PreferencesViewModel
import com.android.streetworkapp.model.progression.Progression
import com.android.streetworkapp.model.progression.ProgressionRepository
import com.android.streetworkapp.model.progression.ProgressionViewModel
import com.android.streetworkapp.model.progression.Ranks
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.WorkoutRepository
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.navigation.Route
import com.android.streetworkapp.utils.GoogleAuthService
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.RETURNS_DEFAULTS
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.wheneverBlocking

class End2EndAchievement {

  @Mock private lateinit var progressionRepository: ProgressionRepository
  @Mock private lateinit var userRepository: UserRepository

  @InjectMocks private lateinit var userViewModel: UserViewModel
  @InjectMocks private lateinit var progressionViewModel: ProgressionViewModel

  @get:Rule val composeTestRule = createComposeRule()

  // grant the permission to access location (remove the window for permission)
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  private var mockedUser =
      User(
          uid = "test",
          username = "John Doe",
          email = "john.doe@example.com",
          score = Ranks.SILVER.score + (Ranks.GOLD.score - Ranks.SILVER.score) / 2,
          friends =
              emptyList(), // The user starts without friends, in order to win the corresponding
          // achievement later
          picture = "",
          parks = listOf(""))

  private val mockedUserProgression =
      Progression(
          progressionId = "prog123456",
          uid = mockedUser.uid,
          currentGoal = Ranks.BRONZE.score,
          eventsCreated = 0,
          eventsJoined = 0,
          achievements = emptyList())

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    userViewModel.setCurrentUser(mockedUser)
    // mock the mockedUser's progression
    wheneverBlocking { progressionRepository.getOrAddProgression(mockedUser.uid) }
        .thenReturn(mockedUserProgression)
  }

  /**
   * The objective of this end2end is to make the mocked user do actions that will give him points
   * and then achievements, that he will be able to see in the progression Screen
   */
  @Ignore("google maps API can be slow sometimes, wouldn't want the CI to fail because of this")
  @Test
  fun e2eUserWinsAchievementsAndPoints() {

    // Setup
    composeTestRule.setContent {
      StreetWorkApp(
          ParkLocationViewModel(mock(ParkLocationRepository::class.java)),
          { navigateTo(Route.MAP) },
          {},
          userViewModel,
          ParkViewModel(mock(ParkRepository::class.java)),
          EventViewModel(mock(EventRepository::class.java)),
          progressionViewModel,
          WorkoutViewModel(mock(WorkoutRepository::class.java)),
          TextModerationViewModel(mock(TextModerationRepository::class.java)),
          ImageViewModel(mock(ImageRepository::class.java)),
          PreferencesViewModel(mock(PreferencesRepository::class.java)),
          GoogleAuthService(
              "abc", mock(FirebaseAuth::class.java, RETURNS_DEFAULTS), LocalContext.current))
    }

    // Assert that the map screen is visible (Start of the app)
    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()

    // The user first goes to the progression, seeing that there is no achievement for the moment

    composeTestRule.onNodeWithTag("bottomNavigationItem${Route.PROGRESSION}").performClick()
    composeTestRule.onNodeWithTag("AchievementTab").performClick()
    composeTestRule.waitForIdle()

    composeTestRule
        .onNodeWithTag("emptyAchievementsText")
        .assertExists() // the user sees that there is no achievement yet

    // The user will now try to add a friend, in order to win points

    // User goes to profile and then to add friend
    composeTestRule.onNodeWithTag("bottomNavigationItem${Route.PROFILE}").performClick()

    composeTestRule.onNodeWithTag("profileAddButton").performClick()

    composeTestRule
        .onNodeWithTag("addFriendScreen")
        .assertIsDisplayed() // check we are at the addFriend screen

    // To simulate adding a friend through bluetooth
    mockedUser = mockedUser.copy(friends = listOf("friendID"))

    // User goes back to progression to see the achievement for adding a friend

    composeTestRule.onNodeWithTag("bottomNavigationItem${Route.PROGRESSION}").performClick()
    composeTestRule.onNodeWithTag("AchievementTab").performClick()
    composeTestRule.waitForIdle()

    // TODO check presence of "first friend" achievement
  }
}
