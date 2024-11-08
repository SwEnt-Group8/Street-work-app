package com.android.streetworkapp.model.park

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.streetworkapp.model.parklocation.ParkLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
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
  fun setCurrentParkUpdatesCurrentPark() = runTest {
    val park = createPark()
    parkViewModel.setCurrentPark(park)

    val observedPark = parkViewModel.currentPark.first()
    assertEquals(park, observedPark)
  }

  @Test
  fun getParkByPidUpdatesPark() = runTest {
    val park = createPark()
    whenever(repository.getParkByPid("123")).thenReturn(park)

    parkViewModel.getParkByPid("123")
    testDispatcher.scheduler.advanceUntilIdle()

    val observedPark = parkViewModel.park.first()
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

    val observedPark = parkViewModel.currentPark.first()
    verify(repository).getParkByPid(pid)
    assertEquals(park, observedPark)
  }

  @Test
  fun getOrCreateParkByLocationCallsRepositoryWithCorrectLocationAndReturnsPark() = runTest {
    val parkLocation = ParkLocation(0.0, 0.0, "location123")
    val park = createPark(pid = "123", locationId = "location123")
    whenever(repository.getOrCreateParkByLocation(parkLocation)).thenReturn(park)
    parkViewModel.getOrCreateParkByLocation(parkLocation)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getOrCreateParkByLocation(parkLocation)
  }

  @Test
  fun addRatingCallsRepositoryWithCorrectParameters() = runTest {
    // Arrange
    val pid = "123"
    val uid = "user_001"
    val rating = 4

    // Act
    parkViewModel.addRating(pid, uid, rating)
    testDispatcher.scheduler.advanceUntilIdle()

    // Assert
    verify(repository).addRating(pid, uid, rating)
  }

  @Test
  fun addRatingUpdatesRatingCorrectly() = runTest {
    // Arrange
    val pid = "123"
    val uid = "user_001"
    val newRating = 4
    val existingRating = 3.5f
    val existingNbrRating = 2

    // Set up initial park data with an average rating and number of raters
    val park =
        createPark(pid = pid).apply {
          this.rating = existingRating
          this.nbrRating = existingNbrRating
          this.votersUIDs = mutableListOf("user_002") // Assume "user_002" already rated
        }

    // Mock the repository methods
    whenever(repository.getParkByPid(pid)).thenReturn(park)
    whenever(repository.addRating(pid, uid, newRating)).thenAnswer {
      // Update the park object as if the rating was added
      val updatedNbrRating = park.nbrRating + 1
      val updatedRating = (newRating + park.nbrRating * park.rating) / updatedNbrRating
      park.rating = updatedRating
      park.nbrRating = updatedNbrRating
      park.votersUIDs += uid
      null
    }

    // Act
    parkViewModel.addRating(pid, uid, newRating)
    testDispatcher.scheduler.advanceUntilIdle()

    // Calculate the expected new rating using the formula
    val expectedNbrRating = existingNbrRating + 1
    val expectedRating = (newRating + (existingNbrRating * existingRating)) / expectedNbrRating

    // Assert that the rating and number of ratings were updated correctly in the ViewModel's
    // currentPark
    assertEquals(expectedRating, parkViewModel.currentPark.first()?.rating)
    assertEquals(expectedNbrRating, parkViewModel.currentPark.first()?.nbrRating)
    assertTrue(parkViewModel.currentPark.first()?.votersUIDs?.contains(uid) ?: false)
  }
}
