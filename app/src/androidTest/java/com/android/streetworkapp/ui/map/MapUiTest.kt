package com.android.streetworkapp.ui.map

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.park.ParkRepository
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.OverpassParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.android.streetworkapp.model.parklocation.ParkLocationRepository
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
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
  private lateinit var parkRepository: ParkRepository
  private lateinit var parkViewModel: ParkViewModel
  private lateinit var userRepository: UserRepository
  private lateinit var userViewModel: UserViewModel
  private lateinit var navigationActions: NavigationActions

  private var testPark =
      Park(
          name = "Test Park",
          location = ParkLocation(),
          rating = 4F,
          occupancy = 3,
          events = emptyList())

  @get:Rule val composeTestRule = createComposeRule()

  // grant the permission to access location (remove the window for permission)
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

  @Before
  fun setUp() {
    parkLocationRepository = OverpassParkLocationRepository(OkHttpClient())
    parkLocationViewModel = ParkLocationViewModel(parkLocationRepository)
    parkRepository = mock(ParkRepository::class.java)
    parkViewModel = ParkViewModel(parkRepository)
    userRepository = mock(UserRepository::class.java)
    userViewModel = UserViewModel(userRepository)
    navigationActions = mock(NavigationActions::class.java)
  }

  @Test
  fun displayAllComponents() {
    `when`(navigationActions.currentRoute()).thenReturn(Screen.MAP)

    composeTestRule.setContent {
      MapScreen(
          parkLocationViewModel,
          parkViewModel,
          userViewModel,
          navigationActions,
          mutableStateOf(""))
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("googleMap").assertIsDisplayed()
  }

  @Test
  fun infoWindowContentsAreDisplayed() {
    `when`(navigationActions.currentRoute()).thenReturn(Screen.MAP)

    composeTestRule.setContent { MarkerInfoWindowContent(testPark) }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("markerInfoWindow").assertIsDisplayed()

    composeTestRule.onNodeWithTag("parkName").assertIsDisplayed()

    composeTestRule.onNodeWithTag("ratingComponent").assertIsDisplayed()

    composeTestRule.onNodeWithTag("eventIcon").assertIsDisplayed()

    composeTestRule.onNodeWithTag("eventsPlanned").assertIsDisplayed()

    composeTestRule.onNodeWithTag("enterParkIcon").assertIsDisplayed()
  }

  @Test
  fun mapSearchBarComponentIsDisplayed() {

    composeTestRule.setContent { MapSearchBar(mutableStateOf("")) }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("searchBar").assertIsDisplayed()
  }
}
