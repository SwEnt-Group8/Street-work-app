package com.android.streetworkapp.end2end

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.streetworkapp.StreetWorkApp
import com.android.streetworkapp.model.event.EventRepository
import com.android.streetworkapp.model.event.EventRepositoryFirestore
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.park.ParkRepositoryFirestore
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.OverpassParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.progression.MedalsAchievement
import com.android.streetworkapp.model.progression.Progression
import com.android.streetworkapp.model.progression.ProgressionRepository
import com.android.streetworkapp.model.progression.ProgressionRepositoryFirestore
import com.android.streetworkapp.model.progression.ProgressionViewModel
import com.android.streetworkapp.model.progression.Ranks
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.internal.wait
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.spy
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class End2EndGeneral {
    @Mock
    private lateinit var userRepository: UserRepository
    @Mock
    private lateinit var progressionRepository: ProgressionRepository

    @InjectMocks
    private lateinit var userViewModel: UserViewModel
    @InjectMocks
    private lateinit var progressionViewModel: ProgressionViewModel


    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockedUserUid = "123456"

    private val mockedFriendsForMockedUser =
        (listOf( //needed to wrap it into a spyk, otherwise couldn't return it in coEvery...
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
            )))


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
        currentGoal = Ranks.GOLD.score,
        eventsCreated = 0,
        eventsJoined = 0,
        achievements = listOf(MedalsAchievement.BRONZE.name)
    )

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userViewModel.setCurrentUser(mockedUser)

        composeTestRule.setContent {
            StreetWorkApp(ParkLocationViewModel(mock(ParkLocationRepository::class.java)),
                { navigateTo(Route.MAP) },
                {},
                userViewModel,
                ParkViewModel(mock(ParkRepository::class.java)),
                EventViewModel(mock(EventRepository::class.java)),
                progressionViewModel
            )
        }
    }

    /**
     * Tests everything included up to M2 except for everything that involves parks
     */
    @Test
    fun e2eNavigationAndDisplaysCorrectDetailsExceptForParks() = runTest {

        //mock the mockedUser's progression
        doAnswer { invocation ->
            val onSuccessCallback = invocation.getArgument<(Progression) -> Unit>(1)
            onSuccessCallback.invoke(mockedUserProgression)
        }.`when`(progressionRepository).getProgression(eq(mockedUser.uid), any<(Progression) -> Unit>(), any())

        //mock the mockedUser's friends
        whenever(userRepository.getFriendsByUid(mockedUser.uid)).thenReturn(mockedFriendsForMockedUser)


        composeTestRule.waitForIdle()
        //already on map here
        composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()

        //navigate to Progress screen
        composeTestRule.onNodeWithTag("bottomNavigationItem${Route.PROGRESSION}").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("progressionScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("percentageInsideCircularProgressBar").assertTextEquals("${(mockedUser.score/mockedUserProgression.currentGoal.toFloat()*100).toInt()}%")
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

        verify(userRepository).addFriend(mockedUser.uid, dummyFriendId) //the behavior of adding friends will very likely change in the future, thus the test doesn't go into more depth
    }
}