package com.android.streetworkapp.model.park

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.streetworkapp.model.parklocation.ParkLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

class ParkViewModelTest {

  private lateinit var repository: ParkRepository
  private lateinit var parkViewModel: ParkViewModel
  private val testDispatcher = StandardTestDispatcher()

  private fun createPark(pid: String = "123", locationId: String = "321") =
      Park(
          pid = pid,
          name = "Sample Park",
          location = ParkLocation(0.0, 0.0, locationId),
          imageReference = "parks/sample.png",
          rating = 4.0f,
          nbrRating = 2,
          capacity = 10,
          occupancy = 5,
          events = listOf("event1", "event2"))

  @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    repository = mock()
    parkViewModel = ParkViewModel(repository)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun getNewPidCallsRepositoryAndReturnsPid() {
    whenever(repository.getNewPid()).thenReturn("uniqueId")
    val pid = parkViewModel.getNewPid()
    assertEquals("uniqueId", pid)
    verify(repository).getNewPid()
  }

  @Test
  fun getParkByPidCallsRepositoryWithCorrectPid() = runTest {
    val pid = "user123"
    val park = createPark(pid = pid)
    whenever(repository.getParkByPid(pid)).thenReturn(park)
    parkViewModel.getParkByPid(pid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getParkByPid(pid)
  }

  @Test
  fun getParkByLocationIdCallsRepositoryWithCorrectLocationId() = runTest {
    val locationId = "location123"
    val park = createPark(locationId = locationId)
    whenever(repository.getParkByLocationId(locationId)).thenReturn(park)
    parkViewModel.getParkByLocationId(locationId)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getParkByLocationId(locationId)
  }

  @Test
  fun createParkCallsRepositoryWithCorrectPark() = runTest {
    val park = createPark()
    parkViewModel.createPark(park)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).createPark(park)
  }

  @Test
  fun updateNameCallsRepositoryWithCorrectPidAndName() = runTest {
    val pid = "123"
    val name = "Sample Park"
    parkViewModel.updateName(pid, name)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).updateName(pid, name)
  }

  @Test
  fun updateImageReferenceCallsRepositoryWithCorrectPidAndImageReference() = runTest {
    val pid = "123"
    val imageReference = "parks/sample.png"
    parkViewModel.updateImageReference(pid, imageReference)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).updateImageReference(pid, imageReference)
  }

  @Test
  fun addRatingCallsRepositoryWithCorrectPidAndRating() = runTest {
    val pid = "123"
    val rating = 4
    parkViewModel.addRating(pid, rating)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).addRating(pid, rating)
  }

  @Test
  fun deleteRatingCallsRepositoryWithCorrectPidAndRating() = runTest {
    val pid = "123"
    val rating = 4
    parkViewModel.deleteRating(pid, rating)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).deleteRating(pid, rating)
  }

  @Test
  fun updateCapacityCallsRepositoryWithCorrectPidAndCapacity() = runTest {
    val pid = "123"
    val capacity = 10
    parkViewModel.updateCapacity(pid, capacity)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).updateCapacity(pid, capacity)
  }

  @Test
  fun incrementOccupancyCallsRepositoryWithCorrectPid() = runTest {
    val pid = "123"
    parkViewModel.incrementOccupancy(pid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).incrementOccupancy(pid)
  }

  @Test
  fun decrementOccupancyCallsRepositoryWithCorrectPid() = runTest {
    val pid = "123"
    parkViewModel.decrementOccupancy(pid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).decrementOccupancy(pid)
  }

  @Test
  fun addEventToParkCallsRepositoryWithCorrectPidAndEid() = runTest {
    val pid = "123"
    val eid = "event123"
    parkViewModel.addEventToPark(pid, eid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).addEventToPark(pid, eid)
  }

  @Test
  fun deleteEventFromParkCallsRepositoryWithCorrectPidAndEid() = runTest {
    val pid = "123"
    val eid = "event123"
    parkViewModel.deleteEventFromPark(pid, eid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).deleteEventFromPark(pid, eid)
  }

  @Test
  fun deleteParkByPidCallsRepositoryWithCorrectPid() = runTest {
    val pid = "123"
    parkViewModel.deleteParkByPid(pid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).deleteParkByPid(pid)
  }

  @Test
  fun setCurrentParkUpdatesCurrentPark() {
    val park = createPark()
    parkViewModel.setCurrentPark(park)

    var observedPark: Park? = null
    parkViewModel.currentPark.observeForever { observedPark = it }

    assertEquals(park, observedPark)
  }

  @Test
  fun getParkByPidUpdatesPark() = runTest {
    val park = createPark()
    whenever(repository.getParkByPid("123")).thenReturn(park)

    parkViewModel.getParkByPid("123")
    testDispatcher.scheduler.advanceUntilIdle()

    var observedPark: Park? = null
    parkViewModel.park.observeForever { observedPark = it }

    verify(repository).getParkByPid("123")
    assertEquals(park, observedPark)
  }

  @Test
  fun loadCurrentParkCallsRepositoryWithCorrectPidAndUpdatesCurrentPark() = runTest {
    val pid = "park123"
    val park = createPark(pid = pid)
    whenever(repository.getParkByPid(pid)).thenReturn(park)
    parkViewModel.loadCurrentPark(pid)
    testDispatcher.scheduler.advanceUntilIdle()
    var observedPark: Park? = null
    parkViewModel.currentPark.observeForever { observedPark = it }
    verify(repository).getParkByPid(pid)
    assertEquals(park, observedPark)
  }
}
