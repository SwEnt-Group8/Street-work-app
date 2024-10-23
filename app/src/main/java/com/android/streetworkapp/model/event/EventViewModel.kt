package com.android.streetworkapp.model.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

open class EventViewModel(private val repository: EventRepository) : ViewModel() {

  /**
   * Get a new event ID.
   *
   * @return A new event ID.
   */
  fun getNewEid(): String {
    return repository.getNewEid()
  }

  /**
   * Add a new event to the database.
   *
   * @param event The event to add.
   */
  fun addEvent(event: Event) = viewModelScope.launch { repository.addEvent(event) }
}
