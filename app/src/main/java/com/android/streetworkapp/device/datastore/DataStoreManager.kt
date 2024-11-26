package com.android.streetworkapp.device.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

class DataStoreManager(context: Context) {

  private val Context.dataStore: DataStore<Preferences> by
      preferencesDataStore(name = "preferences")
  private val dataStore = context.dataStore

  companion object {
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    val SAVED_UID = stringPreferencesKey("saved_uid")
  }

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
}
