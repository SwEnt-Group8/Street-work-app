package com.android.streetworkapp.ui.map

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.utils.LocationService
import com.android.streetworkapp.utils.PermissionManager
import com.google.android.gms.maps.model.LatLng
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class LocationTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testPermissionDenied() {
    // Mock PermissionManager to deny permission
    val mockPermissionManager = mock(PermissionManager::class.java)
    `when`(mockPermissionManager.hasLocationPermission()).thenReturn(false)

    val mockLocationService = mock(LocationService::class.java)
    val userLocation = mutableStateOf(LatLng(0.0, 0.0))

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
    val mockPermissionManager = mock(PermissionManager::class.java)
    `when`(mockPermissionManager.hasLocationPermission()).thenReturn(true)

    val mockLocationService = mock(LocationService::class.java)
    val userLocation = mutableStateOf(LatLng(0.0, 0.0))

    composeTestRule.setContent {
      MapManager(
          userLocation = userLocation,
          onUserLocationChange = {},
          permissionManager = mockPermissionManager,
          locationService = mockLocationService)
    }
  }
}
