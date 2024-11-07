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

  private val _parkEventList = MutableStateFlow<List<Event>>(emptyList())
  val parkeventList: StateFlow<List<Event>>
    get() = _parkEventList

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
   * Get a new event ID.
   *
   * @return A new event ID.
   */
  fun getNewEid(): String {
    return repository.getNewEid()
  }

  /** Fetch all events from the database. */
  fun getEvents(park: Park) {
    viewModelScope.launch {
      repository.getEvents(
          park,
          onSuccess = {
            if (it.isEmpty()) {
              _uiState.value = EventOverviewUiState.Empty
            } else {
              _uiState.value = EventOverviewUiState.NotEmpty(it)
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
  fun addEvent(event: Event) = viewModelScope.launch { repository.addEvent(event) }
}

sealed class EventOverviewUiState {
  data class NotEmpty(val eventList: List<Event>) : EventOverviewUiState()

  data object Empty : EventOverviewUiState()
}
