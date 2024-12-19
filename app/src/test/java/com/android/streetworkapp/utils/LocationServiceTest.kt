package com.android.streetworkapp.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class LocationServiceTest {
  private lateinit var context: Context

  private lateinit var userRepository: UserRepository
  private lateinit var userViewModel: UserViewModel
  private lateinit var navigationActions: NavigationActions
  private lateinit var scope: CoroutineScope
  private lateinit var locationService: LocationService

  private val testDispatcher = StandardTestDispatcher()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setup() {
    Dispatchers.setMain(testDispatcher)
    userRepository = mock(UserRepository::class.java)
    // Mock parks as a MutableStateFlow
    val parksFlow = MutableStateFlow(listOf("oldPark"))
    userViewModel =
        mock(UserViewModel::class.java).apply {
          whenever(parks).thenReturn(parksFlow) // Return the mocked flow
        }

    navigationActions = mock(NavigationActions::class.java)
    scope = CoroutineScope(testDispatcher)
    context = ApplicationProvider.getApplicationContext()

    // Instance of the function to test
    locationService = LocationService(context, userViewModel, navigationActions, scope)
  }

  @Test
  fun rewardParkDiscoveryGivePointWhenNewPark() = runTest {
    // Instantiate basic (but correct) user :
    val user = User("uid", "username", "email", 0, emptyList(), picture = "", emptyList())

    // Test data: User location and parks
    val userLocation = LatLng(0.0, 0.0)
    val newPark = ParkLocation(0.0, 0.0, "newPark")
    val farPark = ParkLocation(10.0, 10.0, "farPark")

    val parkLocations = listOf(newPark, farPark)

    // Invoke the function
    locationService.rewardParkDiscovery(user, userLocation, parkLocations)
    testDispatcher.scheduler.advanceUntilIdle()

    // Ensure check of parkID was called
    Mockito.verify(userViewModel, Mockito.times(1)).getParksByUid(user.uid)
    // Ensure addNewPark was called for "newPark"
    Mockito.verify(userViewModel, Mockito.times(1)).addNewPark(user.uid, "newPark")
  }

  @Test
  fun rewardParkDiscoveryDontGivePointWhenAlreadyDiscoveredPark() = runTest {
    // Instantiate basic (but correct) user :
    val user = User("uid", "username", "email", 0, emptyList(), "", listOf("oldPark"))

    // Test data: User location and parks
    val userLocation = LatLng(0.0, 0.0)
    val oldPark = ParkLocation(0.0, 0.0, "oldPark")
    val farPark = ParkLocation(10.0, 10.0, "farPark")

    val parkLocations = listOf(oldPark, farPark)

    // Invoke the function
    locationService.rewardParkDiscovery(user, userLocation, parkLocations)
    testDispatcher.scheduler.advanceUntilIdle()

    // Ensure check of parkID was called for nearby park
    Mockito.verify(userViewModel, Mockito.times(1)).getParksByUid(user.uid)
    // Ensure addNewPark was not called for "oldPark"
    Mockito.verify(userViewModel, Mockito.times(0)).addNewPark(user.uid, "oldPark")
  }

  @Test
  fun rewardParkDiscoveryDontGivePointWhenFarAwayPark() = runTest {
    // Instantiate basic (but correct) user :
    val user = User("uid", "username", "email", 0, emptyList(), picture = "", emptyList())

    // Test data: User location and parks
    val userLocation = LatLng(0.0, 0.0)
    val farPark1 = ParkLocation(10.0, 10.0, "farPark1")
    val farPark2 = ParkLocation(10.0, 10.0, "farPark2")

    val parkLocations = listOf(farPark1, farPark2)

    // Invoke the function
    locationService.rewardParkDiscovery(user, userLocation, parkLocations)
    testDispatcher.scheduler.advanceUntilIdle()

    // Ensure check of parkID was not called
    Mockito.verify(userViewModel, Mockito.times(0)).getParksByUid(user.uid)
    // Ensure addNewPark was not called for "farPark1"
    Mockito.verify(userViewModel, Mockito.times(0)).addNewPark(user.uid, "farPark1")
    // Ensure addNewPark was not called for "farPark2"
    Mockito.verify(userViewModel, Mockito.times(0)).addNewPark(user.uid, "farPark2")
  }
}
