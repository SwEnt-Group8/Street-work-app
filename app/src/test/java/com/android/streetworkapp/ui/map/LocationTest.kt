package com.android.streetworkapp.ui.map

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.utils.LocationService
import com.android.streetworkapp.utils.PermissionManager
import com.google.android.gms.maps.model.LatLng
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class LocationTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Mock lateinit var mockPermissionManager: PermissionManager
  @Mock lateinit var mockLocationService: LocationService

  private lateinit var userLocation: MutableState<LatLng>

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)

    // init variable
    userLocation = mutableStateOf(LatLng(10.0, 0.0))
    mockLocationService = mock(LocationService::class.java)
    mockPermissionManager = mock(PermissionManager::class.java)
  }

  @Test
  fun testPermissionDenied() {
    // Mock PermissionManager to deny permission
    `when`(mockPermissionManager.hasLocationPermission()).thenReturn(false)

    composeTestRule.setContent {
      MapManager(
          userLocation = userLocation,
          onUserLocationChange = {},
          permissionManager = mockPermissionManager,
          locationService = mockLocationService)
    }
  }

  @Test
  fun testPermissionGranted() {
    // Mock PermissionManager to grant permission
    `when`(mockPermissionManager.hasLocationPermission()).thenReturn(true)

    composeTestRule.setContent {
      MapManager(
          userLocation = userLocation,
          onUserLocationChange = { userLocation = it },
          permissionManager = mockPermissionManager,
          locationService = mockLocationService)
    }
  }

  // if necessary test that location is correctly updated or toast
}
