package com.android.streetworkapp.ui.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.StreetWorkAppMain
import com.android.streetworkapp.model.parks.ParkLocationRepository
import com.android.streetworkapp.model.parks.ParkLocationViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Screen
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
    parkLocationRepository = mock(ParkLocationRepository::class.java)
    parkLocationViewModel = ParkLocationViewModel(parkLocationRepository)
    navigationActions = mock(NavigationActions::class.java)
  }

  @Test
  fun displayAllComponents() {
    `when`(navigationActions.currentRoute()).thenReturn(Screen.MAP)

    composeTestRule.setContent { MapScreen(parkLocationViewModel, navigationActions) }

    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("googleMap").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
  }

  @Test
  fun componentsAreNotDisplayedOnOtherRoutes() {
    `when`(navigationActions.currentRoute()).thenReturn(Screen.AUTH)

    composeTestRule.setContent { StreetWorkAppMain() }

    composeTestRule.onNodeWithTag("mapScreen").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("googleMap").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsNotDisplayed()
  }
}
