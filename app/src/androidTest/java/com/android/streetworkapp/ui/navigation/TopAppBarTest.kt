package com.android.streetworkapp.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import com.android.streetworkapp.StreetWorkApp
import com.android.streetworkapp.model.event.EventRepositoryFirestore
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.park.ParkRepositoryFirestore
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.OverpassParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.user.UserRepositoryFirestore
import com.android.streetworkapp.model.user.UserViewModel
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TopAppBarTest {

    private lateinit var parkLocationRepository: OverpassParkLocationRepository
    private lateinit var parkLocationViewModel: ParkLocationViewModel

    @get:Rule
    val composeTestRule = createComposeRule()


    @Before
    fun setUp() {
        val mockParkList =
            listOf(
                ParkLocation(
                    lat = 46.518659400000004,
                    lon = 6.566561505148001,
                    id = "1"),
                ParkLocation(lat = 34.052235, lon = -118.243683, id = "2"),
                ParkLocation(lat = 51.507351, lon = -0.127758, id = "3"),
                ParkLocation(lat = 35.676192, lon = 139.650311, id = "4"),
                ParkLocation(lat = -33.868820, lon = 151.209290, id = "5")
            )

        parkLocationRepository =
            mockk<OverpassParkLocationRepository>()
        every {
            parkLocationRepository.search(
                any<Double>(),
                any<Double>(),
                any<(List<ParkLocation>) -> Unit>(),
                any<(Exception) -> Unit>())
        } answers
                {
                    val onSuccess = this.args[2] as (List<ParkLocation>) -> Unit
                    onSuccess(mockParkList) // Invoke onSuccess with the custom list
                }

        parkLocationViewModel = ParkLocationViewModel(parkLocationRepository)
    }

    
    @Test
    fun isDisplayedCorrectlyOnScreens() {
        val currentScreenParam = mutableStateOf(LIST_OF_SCREENS.first()) //can't call setContent twice per test so we use this instead
        composeTestRule.setContent { StreetWorkApp(
            parkLocationViewModel,
            {navigateTo(currentScreenParam.value.screenName)},
            {},
            UserViewModel(mockk<UserRepositoryFirestore>()),
            ParkViewModel(mockk<ParkRepositoryFirestore>()),
            EventViewModel(mockk<EventRepositoryFirestore>())
        ) }

        for (screenParam in LIST_OF_SCREENS) {
            if (screenParam.screenName in TEST_SCREEN_EXCLUSION_LIST)
                continue

            currentScreenParam.value = screenParam // Update the state

            composeTestRule.waitForIdle()
            if (screenParam.isTopBarVisible) {
                composeTestRule.onNodeWithTag("topAppBar").assertIsDisplayed()
                screenParam.topAppBarManager?.let { topAppBarManager ->
                    if (topAppBarManager.hasNavigationIcon())
                        composeTestRule.onNodeWithTag("goBackButton").assertIsDisplayed()
                }
            }
            else
                composeTestRule.onNodeWithTag("topAppBar").assertIsNotDisplayed()

        }
    }
}