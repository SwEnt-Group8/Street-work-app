package com.android.streetworkapp.device.datastore

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
import kotlinx.coroutines.flow.map

class DataStoreManager(context: Context) {

  private val Context.dataStore: DataStore<Preferences> by
      preferencesDataStore(name = "preferences")
  private val dataStore = context.dataStore

  companion object {
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    val SAVED_UID = stringPreferencesKey("saved_uid")
    val SAVED_NAME = stringPreferencesKey("saved_name")
    val SAVED_SCORE = intPreferencesKey("saved_score")
  }

  /** A flow that emits the login state of the user. */
  val isLoggedInFlow: Flow<Boolean> =
      dataStore.data
          .catch { exception ->
            // Handle exceptions
            if (exception is IOException) {
              emit(emptyPreferences())
              Log.d("DataStoreManager", "An IO exception occurred, emitting empty preferences.")
            } else {
              Log.d("DataStoreManager", exception.message.toString())
            }
          }
          .map { preferences -> preferences[IS_LOGGED_IN] ?: false }

  /** A flow that emits the user's uid. */
  val savedUidFlow: Flow<String> =
      dataStore.data
          .catch { exception ->
            // Handle exceptions
            if (exception is IOException) {
              emit(emptyPreferences())
              Log.d("DataStoreManager", "An IO exception occurred, emitting empty preferences.")
            } else {
              Log.d("DataStoreManager", exception.message.toString())
            }
          }
          .map { preferences -> preferences[SAVED_UID] ?: "" }

  /** A flow that emits the user's name. */
  val savedNameFlow: Flow<String> =
      dataStore.data
          .catch { exception ->
            // Handle exceptions
            if (exception is IOException) {
              emit(emptyPreferences())
              Log.d("DataStoreManager", "An IO exception occurred, emitting empty preferences.")
            } else {
              Log.d("DataStoreManager", exception.message.toString())
            }
          }
          .map { preferences -> preferences[SAVED_NAME] ?: "" }

  /** A flow that emits the user's score. */
  val savedScoreFlow: Flow<Int> =
      dataStore.data
          .catch { exception ->
            // Handle exceptions
            if (exception is IOException) {
              emit(emptyPreferences())
              Log.d("DataStoreManager", "An IO exception occurred, emitting empty preferences.")
            } else {
              Log.d("DataStoreManager", exception.message.toString())
            }
          }
          .map { preferences -> preferences[SAVED_SCORE] ?: 0 }

  /**
   * Saves the login state of the user in preferences.
   *
   * @param isLoggedIn: The boolean login state of the user
   */
  suspend fun saveLoginState(isLoggedIn: Boolean) {
    dataStore.edit { preferences -> preferences[IS_LOGGED_IN] = isLoggedIn }
  }

  /**
   * Saves the user's uid in preferences.
   *
   * @param uid: The user's uid
   */
  suspend fun saveUid(uid: String) {
    dataStore.edit { preferences -> preferences[SAVED_UID] = uid }
  }

  /**
   * Saves the user's name in preferences.
   *
   * @param name: The user's name
   */
  suspend fun saveName(name: String) {
    dataStore.edit { preferences -> preferences[SAVED_NAME] = name }
  }

  /**
   * Saves the user's score in preferences.
   *
   * @param score: The user's score
   */
  suspend fun saveScore(score: Int) {
    dataStore.edit { preferences -> preferences[SAVED_SCORE] = score }
  }
}
