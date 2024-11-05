package com.android.streetworkapp.ui.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.model.parklocation.OverpassParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Screen
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class MapUiTest {
  private lateinit var parkLocationRepository: ParkLocationRepository
  private lateinit var parkLocationViewModel: ParkLocationViewModel
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    parkLocationRepository = OverpassParkLocationRepository(OkHttpClient())
    parkLocationViewModel = ParkLocationViewModel(parkLocationRepository)
    navigationActions = mock(NavigationActions::class.java)
  }

  @Test
  fun printComposeHierarchy() {
    composeTestRule.setContent { MapScreen(parkLocationViewModel, navigationActions) }
    composeTestRule.onRoot().printToLog("MapScreen")
  }

  @Test
  fun displayAllComponents() {
    `when`(navigationActions.currentRoute()).thenReturn(Screen.MAP)

    composeTestRule.setContent { MapScreen(parkLocationViewModel, navigationActions) }

    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("googleMap").assertIsDisplayed()
  }
}
