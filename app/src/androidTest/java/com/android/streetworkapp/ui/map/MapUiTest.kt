package com.android.streetworkapp.ui.map

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.StreetWorkAppMain
import com.android.streetworkapp.model.parklocation.OverpassParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Screen
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify

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
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule
        .onAllNodesWithTag("bottomNavigationItem")
        .assertCountEquals(LIST_TOP_LEVEL_DESTINATION.size)

    for (i in LIST_TOP_LEVEL_DESTINATION.indices) {
      composeTestRule.onAllNodesWithTag("bottomNavigationItem")[i].assertIsDisplayed()
    }
  }

  @Test
  fun mapIsInteractive() {
    `when`(navigationActions.currentRoute()).thenReturn(Screen.MAP)

    composeTestRule.setContent { MapScreen(parkLocationViewModel, navigationActions) }

    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("googleMap").assertIsDisplayed().performClick()

    for (i in LIST_TOP_LEVEL_DESTINATION.indices) {
      composeTestRule
          .onAllNodesWithTag("bottomNavigationItem")[i]
          .assertIsDisplayed()
          .performClick()
    }
  }

  @Test
  fun routeChangesWhenBottomNavigationItemIsClicked() {
    `when`(navigationActions.currentRoute()).thenReturn(Screen.MAP)

    composeTestRule.setContent { MapScreen(parkLocationViewModel, navigationActions) }

    for (i in LIST_TOP_LEVEL_DESTINATION.indices) {
      composeTestRule
          .onAllNodesWithTag("bottomNavigationItem")[i]
          .assertIsDisplayed()
          .performClick()
      verify(navigationActions).navigateTo(LIST_TOP_LEVEL_DESTINATION[i])
    }
  }

  @Test
  fun componentsAreNotDisplayedOnOtherRoutes() {
    `when`(navigationActions.currentRoute()).thenReturn(Screen.AUTH)

    composeTestRule.setContent { StreetWorkAppMain() }

    composeTestRule.onNodeWithTag("mapScreen").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("googleMap").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsNotDisplayed()
    composeTestRule.onAllNodesWithTag("bottomNavigationItem").assertCountEquals(0)
  }
}
