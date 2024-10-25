package com.android.streetworkapp.model.event

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class EventViewModel(private val repository: EventRepository) : ViewModel() {

  private val _uiState: MutableStateFlow<EventOverviewUiState> =
      MutableStateFlow(EventOverviewUiState.Empty)

  val uiState: StateFlow<EventOverviewUiState> = _uiState

  /**
   * Get a new event ID.
   *
   * @return A new event ID.
   */
  fun getNewEid(): String {
    return repository.getNewEid()
  }

  /** Fetch all events from the database. */
  fun getEvents() {
    repository.getEvents(
        onSuccess = {
          if (it.isEmpty()) {
            _uiState.value = EventOverviewUiState.Empty
          } else {
            _uiState.value = EventOverviewUiState.NotEmpty(it)
          }
        },
        onFailure = { Log.e("FirestoreError", "Error getting events: ${it.message}") })
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
