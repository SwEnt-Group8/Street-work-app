package com.android.streetworkapp.model.progression

import com.android.streetworkapp.model.user.User

interface ProgressionRepository {

  fun getNewProgressionId(): String

  fun getProgression(user: User, onSuccess: (Progression) -> Unit, onFailure: (Exception) -> Unit)

  fun updateProgression(progression: Progression)

  suspend fun createProgression(uid: String)
}
