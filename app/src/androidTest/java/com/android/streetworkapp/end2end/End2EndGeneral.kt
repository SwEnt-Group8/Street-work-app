package com.android.streetworkapp.end2end

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.streetworkapp.StreetWorkApp
import com.android.streetworkapp.model.event.EventRepositoryFirestore
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.park.ParkRepositoryFirestore
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.OverpassParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.progression.MedalsAchievement
import com.android.streetworkapp.model.progression.Progression
import com.android.streetworkapp.model.progression.ProgressionRepositoryFirestore
import com.android.streetworkapp.model.progression.ProgressionViewModel
import com.android.streetworkapp.model.progression.Ranks
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepositoryFirestore
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.Route
import com.android.streetworkapp.utils.GoogleAuthService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import okhttp3.internal.wait
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`

class End2EndGeneral {
    @MockK
    private lateinit var userRepository: UserRepositoryFirestore
    private lateinit var userViewModel: UserViewModel

    @MockK
    private lateinit var progressionRepository: ProgressionRepositoryFirestore
    private lateinit var progressionViewModel: ProgressionViewModel

    @MockK
    private lateinit var parkLocationRepository: OverpassParkLocationRepository

    private lateinit var mockedFirebaseUser : FirebaseUser

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockedUserUid = "123456"


    private val mockedFriendsForMockedUser =
        spyk(listOf<User>( //needed to wrap it into a spyk, otherwise couldn't return it in coEvery...
            User(
                uid = "434356",
                username = "David Miller",
                email = "david.miller@example.com",
                score = Ranks.BRONZE.score + (Ranks.SILVER.score - Ranks.BRONZE.score) / 2,
                friends = listOf(mockedUserUid)
            ),
            User(
                uid = "987356",
                username = "Jane Smith",
                email = "jane.smith@example.com",
                score = Ranks.GOLD.score + (Ranks.PLATINUM.score - Ranks.GOLD.score) / 3,
                friends = listOf(mockedUserUid)
            ),
            User(
                uid = "966245",
                username = "Alice Johnson",
                email = "alice.johnson@example.com",
                score = Ranks.BRONZE.score / 2,
                friends = listOf(mockedUserUid)
            ))
        )

    private val mockedUser =
        User(
            uid = mockedUserUid,
            username = "John Doe",
            email = "john.doe@example.com",
            score = Ranks.SILVER.score + (Ranks.GOLD.score - Ranks.SILVER.score) / 2,
            friends = listOf("friend_1", "friend_2", "friend_3"))

    //for the input text field in add friend testing
    private val dummyFriendId = "friendId123"

    private val mockedUserProgression = Progression(
        progressionId = "prog123456",
        uid = mockedUser.uid,
        currentGoal = Ranks.BRONZE.score,
        eventsCreated = 0,
        eventsJoined = 0,
        achievements = listOf(MedalsAchievement.BRONZE.name)
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        userViewModel = UserViewModel(userRepository)

        userViewModel.setCurrentUser(mockedUser)
        progressionViewModel = ProgressionViewModel(progressionRepository)

        //mock the needed functions for the userRepository
        coEvery { userRepository.getFriendsByUid(mockedUser.uid) } returns mockedFriendsForMockedUser

        //mock the needed functions for the progressionRepository
        coEvery { progressionRepository.getProgression(any(), captureLambda<(Progression) -> Unit>(), any()) } answers {
            val onSuccess = firstArg<(Progression) -> Unit>()
            onSuccess(mockedUserProgression)
        }

        composeTestRule.setContent {
            StreetWorkApp(ParkLocationViewModel(parkLocationRepository),
                { navigateTo(Route.MAP) },
                {},
                userViewModel,
                ParkViewModel(mockk<ParkRepositoryFirestore>()),
                EventViewModel(mockk<EventRepositoryFirestore>())
            )
        }
    }

    /**
     * Tests everything included up to M2 except for everything that involves parks
     */
    @Test
    fun e2eNavigationAndDisplaysCorrectDetailsExceptForParks() {
        composeTestRule.waitForIdle()
        //already on map here
        composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()

        //navigate to Progress screen
        composeTestRule.onNodeWithTag("bottomNavigationItem${Route.PROGRESSION}").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("progressionScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("percentageInsideCircularProgressBar").assertTextEquals("${(mockedUser.score/mockedUserProgression.currentGoal.toFloat()*100).toInt()}")
        composeTestRule.onNodeWithTag("scoreTextUnderCircularProgressBar").assertTextEquals("${mockedUser.score}/${mockedUserProgression.currentGoal}")

        //checking that each achievements exists in the lazy column
        mockedUserProgression.achievements.forEachIndexed{ index, _ ->
            composeTestRule.onNodeWithTag("achievementItem${index}").assertExists()
        }

        //navigate to Profile screen
        composeTestRule.onNodeWithTag("bottomNavigationItem${Route.PROFILE}").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("ProfileScreen").assertIsDisplayed()

        composeTestRule.onNodeWithTag("profileUsername").assertTextEquals(mockedUser.username)
        composeTestRule.onNodeWithTag("profileScore").assertTextEquals("Score: ${mockedUser.score}")
        //verifying the friends list
        val friendItems = composeTestRule.onAllNodesWithTag("friendItem")
        assert(friendItems.fetchSemanticsNodes().size == mockedUser.friends.size)

        //go to add friend
        composeTestRule.onNodeWithTag("profileAddButton").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("addFriendScreen").assertIsDisplayed()
        //perform add friend request
        composeTestRule.onNodeWithTag("inputID").performTextInput(dummyFriendId)
        composeTestRule.onNodeWithTag("RequestButton").performClick()

        //change to user repo
        coVerify { userRepository.addFriend(mockedUser.uid, dummyFriendId) }
        //the behavior of adding friends will very likely change in the future, thus the test doesn't go into more depth
    }
}