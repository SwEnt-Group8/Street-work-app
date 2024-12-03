package com.android.streetworkapp.model.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * A ViewModel for preferences data.
 *
 * @property preferencesRepository The repository for preferences data
 */
class PreferencesViewModel(private val preferencesRepository: PreferencesRepository) : ViewModel() {

  /** A StateFlow of the login state of the user. */
  private val _loginState = MutableStateFlow<Boolean?>(null)
  val loginState: StateFlow<Boolean?>
    get() = _loginState

  /** A StateFlow of the user's uid. */
  private val _uid = MutableStateFlow<String?>(null)
  val uid: StateFlow<String?>
    get() = _uid

  /** A StateFlow of the user's name. */
  private val _name = MutableStateFlow<String?>(null)
  val name: StateFlow<String?>
    get() = _name

  /** A StateFlow of the user's score. */
  private val _score = MutableStateFlow<Int?>(null)
  val score: StateFlow<Int?>
    get() = _score

  /** Get the login state of the user. */
  fun getLoginState() {
    viewModelScope.launch { preferencesRepository.getLoginState().let { _loginState.value = it } }
  }

  /** Get the user's uid. */
  fun getUid() {
    viewModelScope.launch { preferencesRepository.getUid().let { _uid.value = it } }
  }

  /** Get the user's name. */
  fun getName() {
    viewModelScope.launch { preferencesRepository.getName().let { _name.value = it } }
  }

  /** Get the user's score. */
  fun getScore() {
    viewModelScope.launch { preferencesRepository.getScore().let { _score.value = it } }
  }

  /**
   * Set the login state of the user.
   *
   * @param isLoggedIn The login state of the user
   */
  fun setLoginState(isLoggedIn: Boolean) {
    viewModelScope.launch { preferencesRepository.setLoginState(isLoggedIn) }
  }

  /**
   * Set the user's uid.
   *
   * @param uid The user's uid
   */
  fun setUid(uid: String) {
    viewModelScope.launch { preferencesRepository.setUid(uid) }
  }

  /**
   * Set the user's name.
   *
   * @param name The user's name
   */
  fun setName(name: String) {
    viewModelScope.launch { preferencesRepository.setName(name) }
  }

  /**
   * Set the user's score.
   *
   * @param score The user's score
   */
  fun setScore(score: Int) {
    viewModelScope.launch { preferencesRepository.setScore(score) }
  }
}
