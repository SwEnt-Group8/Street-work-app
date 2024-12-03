package com.android.streetworkapp.model.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class PreferencesRepositoryDataStore(context: Context) : PreferencesRepository {

  private val Context.dataStore: DataStore<Preferences> by
      preferencesDataStore(name = "preferences")
  private val dataStore = context.dataStore

  companion object {
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    val SAVED_UID = stringPreferencesKey("saved_uid")
    val SAVED_NAME = stringPreferencesKey("saved_name")
    val SAVED_SCORE = intPreferencesKey("saved_score")

    private const val IO_ERR = "An IO exception occurred, emitting empty preferences."
    private const val ERR_TAG = "PreferencesRepositoryDataStore"
  }

  /** A flow that emits the login state of the user. */
  val loginStateFlow: Flow<Boolean> =
      dataStore.data
          .catch { exception ->
            // Handle exceptions
            if (exception is IOException) {
              emit(emptyPreferences())
              Log.d(ERR_TAG, IO_ERR)
            } else {
              Log.d(ERR_TAG, exception.message.toString())
            }
          }
          .map { preferences -> preferences[IS_LOGGED_IN] ?: false }

  /** A flow that emits the user's uid. */
  val uidFlow: Flow<String> =
      dataStore.data
          .catch { exception ->
            // Handle exceptions
            if (exception is IOException) {
              emit(emptyPreferences())
              Log.d(ERR_TAG, IO_ERR)
            } else {
              Log.d(ERR_TAG, exception.message.toString())
            }
          }
          .map { preferences -> preferences[SAVED_UID] ?: "" }

  /** A flow that emits the user's name. */
  val nameFlow: Flow<String> =
      dataStore.data
          .catch { exception ->
            // Handle exceptions
            if (exception is IOException) {
              emit(emptyPreferences())
              Log.d(ERR_TAG, IO_ERR)
            } else {
              Log.d(ERR_TAG, exception.message.toString())
            }
          }
          .map { preferences -> preferences[SAVED_NAME] ?: "" }

  /** A flow that emits the user's score. */
  val scoreFlow: Flow<Int> =
      dataStore.data
          .catch { exception ->
            // Handle exceptions
            if (exception is IOException) {
              emit(emptyPreferences())
              Log.d(ERR_TAG, "An IO exception occurred, emitting empty preferences.")
            } else {
              Log.d(ERR_TAG, exception.message.toString())
            }
          }
          .map { preferences -> preferences[SAVED_SCORE] ?: 0 }

  /**
   * Returns the login state of the user from preferences.
   *
   * @return The boolean login state of the user
   */
  override suspend fun getLoginState(): Boolean {
    return loginStateFlow.firstOrNull() ?: false
  }

  /**
   * Saves the login state of the user in preferences.
   *
   * @param loginState: The boolean login state of the user
   */
  override suspend fun setLoginState(loginState: Boolean) {
    dataStore.edit { preferences -> preferences[IS_LOGGED_IN] = loginState }
  }

  /**
   * Returns the user's uid from preferences.
   *
   * @return The user's uid
   */
  override suspend fun getUid(): String {
    return uidFlow.firstOrNull() ?: ""
  }

  /**
   * Saves the user's uid in preferences.
   *
   * @param uid: The user's uid
   */
  override suspend fun setUid(uid: String) {
    dataStore.edit { preferences -> preferences[SAVED_UID] = uid }
  }

  /**
   * Returns the user's name from preferences.
   *
   * @return The user's name
   */
  override suspend fun getName(): String {
    return nameFlow.firstOrNull() ?: ""
  }

  /**
   * Saves the user's name in preferences.
   *
   * @param name: The user's name
   */
  override suspend fun setName(name: String) {
    dataStore.edit { preferences -> preferences[SAVED_NAME] = name }
  }

  /**
   * Returns the user's score from preferences.
   *
   * @return The user's score
   */
  override suspend fun getScore(): Int {
    return scoreFlow.firstOrNull() ?: 0
  }

  /**
   * Saves the user's score in preferences.
   *
   * @param score: The user's score
   */
  override suspend fun setScore(score: Int) {
    dataStore.edit { preferences -> preferences[SAVED_SCORE] = score }
  }
}
