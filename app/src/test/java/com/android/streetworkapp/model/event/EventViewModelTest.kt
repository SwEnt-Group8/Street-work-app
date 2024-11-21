package com.android.streetworkapp.model.event

import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any

class EventViewModelTest {

  private lateinit var repository: EventRepository
  private lateinit var eventViewModel: EventViewModel
  private val testDispatcher = StandardTestDispatcher()

  private val event =
      Event(
          eid = "1",
          title = "Group workout",
          description = "A fun group workout session to train new skills",
          participants = 3,
          maxParticipants = 5,
          date = Timestamp(0, 0), // 01/01/1970 00:00
          owner = "user123")

  // Park with events
  private val park =
      Park(
          pid = "123",
          name = "EPFL Esplanade",
          location = ParkLocation(0.0, 0.0, "321"),
          imageReference = "parks/sample.png",
          rating = 4.0f,
          nbrRating = 102,
          capacity = 10,
          occupancy = 8,
          events = listOf("event1", "event2"))

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    repository = mock()
    eventViewModel = EventViewModel(repository)
  }

  @Test
  fun addEventCallsRepository() = runTest {
    eventViewModel.addEvent(event)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).addEvent(event)
  }

  @Test
  fun getEventsCallsRepository() = runTest {
    eventViewModel.getEvents(park)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getEvents(any(), any(), any())
  }

  @Test
  fun getEventByEidCallsRepository() = runTest {
    eventViewModel.getEventByEid(event.eid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getEventByEid(event.eid)
  }

  @Test
  fun addParticipantToEventCallsRepository() = runTest {
    eventViewModel.addParticipantToEvent(event.eid, "123")
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).addParticipantToEvent(any(), any())
  }

  @Test
  fun removeParticipantFromEventCallsRepository() = runTest {
    eventViewModel.removeParticipantFromEvent(event.eid, "123")
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).removeParticipantFromEvent(any(), any())
  }
}
