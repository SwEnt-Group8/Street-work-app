package com.android.streetworkapp.ui.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.model.parks.OverpassParkLocationRepository
import com.android.streetworkapp.model.parks.ParkLocationViewModel
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapTest {
    private lateinit var overpassParkLocationRepository: OverpassParkLocationRepository
    private lateinit var parkLocationViewModel: ParkLocationViewModel

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        overpassParkLocationRepository = OverpassParkLocationRepository(OkHttpClient())
        parkLocationViewModel = ParkLocationViewModel(overpassParkLocationRepository)
    }

    @Test
    fun displayAllComponents() {

        composeTestRule.setContent { MapScreen(parkLocationViewModel) }

        composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("googleMap").assertIsDisplayed()
        composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
        composeTestRule.onNodeWithTag("parkIcon").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profileIcon").assertIsDisplayed()
    }
}
