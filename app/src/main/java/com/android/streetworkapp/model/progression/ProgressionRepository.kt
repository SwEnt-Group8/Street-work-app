package com.android.streetworkapp.model.progression

interface ProgressionRepository {

  fun getNewProgressionId(): String

  fun getProgression(uid: String, onSuccess: (Progression) -> Unit, onFailure: (Exception) -> Unit)

  fun updateProgression(progression: Progression)

  suspend fun createProgression(uid: String)
}
