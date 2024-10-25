package com.android.streetworkapp.model.event

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

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    repository = mock()
    eventViewModel = EventViewModel(repository)
  }

  @Test
  fun addEventCallsRepository() = runTest {
    val event =
        Event(
            eid = "1",
            title = "Group workout",
            description = "A fun group workout session to train new skills",
            participants = 3,
            maxParticipants = 5,
            date = Timestamp(0, 0), // 01/01/1970 00:00
            owner = "user123")
    eventViewModel.addEvent(event)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).addEvent(event)
  }

  @Test
  fun getEventsCallsRepository() = runTest {
    eventViewModel.getEvents()
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getEvents(any(), any())
  }
}
