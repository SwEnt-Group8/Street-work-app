package com.android.streetworkapp.model.event

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.streetworkapp.model.park.Park
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class EventViewModel(private val repository: EventRepository) : ViewModel() {

  private val _uiState: MutableStateFlow<EventOverviewUiState> =
      MutableStateFlow(EventOverviewUiState.Empty)

  val uiState: StateFlow<EventOverviewUiState> = _uiState

  // current selected event
  private val _currentEvent = MutableStateFlow<Event?>(null)
  val currentEvent: StateFlow<Event?>
    get() = _currentEvent

  private val _eventList: MutableStateFlow<Map<String, Event>> = MutableStateFlow(emptyMap())
  val eventList: StateFlow<Map<String, Event>>
    get() = _eventList

  fun setCurrentEvent(event: Event?) {
    _currentEvent.value = event
  }

  fun getEventByEid(eid: String) {
    viewModelScope.launch {
      val fetchedEvent = repository.getEventByEid(eid)
      _currentEvent.value = fetchedEvent
    }
  }

  /**
   * Get a list of events by their IDs.
   *
   * @param eids The list of event IDs.
   */
  fun getEventsByEid(eids: List<String>) {
    val fetchedEvents = mutableMapOf<String, Event>()
    viewModelScope.launch {
      eids.forEach {
        val event = repository.getEventByEid(it)
        if (event != null) fetchedEvents[it] = event
      }
      _eventList.value = fetchedEvents
    }
  }

  /**
   * Get a list of events from the park they are in.
   *
   * @param parks The list of parks.
   */
  fun getEventsByParkList(parks: List<Park>) {
    val eids = parks.flatMap { it.events }

    getEventsByEid(eids)
  }

  fun setUiState(state: EventOverviewUiState) {
    _uiState.value = state
  }

  /**
   * Get a new event ID.
   *
   * @return A new event ID.
   */
  open fun getNewEid(): String {
    return repository.getNewEid()
  }

  /** Fetch all events from the database. */
  fun getEvents(park: Park) {
    viewModelScope.launch {
      repository.getEvents(
          park,
          onSuccess = {
            if (it.isEmpty()) {
              setUiState(EventOverviewUiState.Empty)
            } else {
              setUiState(EventOverviewUiState.NotEmpty(it))
            }
          },
          onFailure = { Log.e("FirestoreError", "Error getting events: ${it.message}") })
    }
  }

  /**
   * Add a new event to the database.
   *
   * @param event The event to add.
   */
  open fun addEvent(event: Event) = viewModelScope.launch { repository.addEvent(event) }

  /**
   * Delete an event from the database.
   *
   * @param event The event to delete.
   */
  fun deleteEvent(event: Event) = viewModelScope.launch { repository.deleteEvent(event) }

  /**
   * Add a participant to an event.
   *
   * @param eid The event ID.
   * @param uid The user ID.
   */
  fun addParticipantToEvent(eid: String, uid: String) {
    viewModelScope.launch { repository.addParticipantToEvent(eid, uid) }
  }

  /**
   * Remove a participant from an event.
   *
   * @param eid The event ID.
   * @param uid The user ID.
   */
  fun removeParticipantFromEvent(eid: String, uid: String) {
    viewModelScope.launch { repository.removeParticipantFromEvent(eid, uid) }
  }

  /**
   * Update the status of an event.
   *
   * @param eid The event ID.
   * @param status The new status.
   */
  fun updateStatus(eid: String, status: EventStatus) {
    viewModelScope.launch { repository.updateStatus(eid, status) }
  }

  /**
   * Remove a participant from all events and delete events where the user is the owner. Update the
   * deleted events ID list value.
   *
   * @param uid The user ID.
   * @return The list of deleted event IDs.
   */
  suspend fun removeParticipantFromAllEvents(uid: String): List<String>? {
    return repository.removeParticipantFromAllEvents(uid)
  }
}

sealed class EventOverviewUiState {
  data class NotEmpty(val eventList: List<Event>) : EventOverviewUiState()

  data object Empty : EventOverviewUiState()
}
