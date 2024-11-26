package com.android.streetworkapp.device.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

class DataStoreManager(context: Context) {

  private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
  private val dataStore = context.dataStore

  companion object {
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    val SAVED_UID = stringPreferencesKey("saved_uid")
  }
}
