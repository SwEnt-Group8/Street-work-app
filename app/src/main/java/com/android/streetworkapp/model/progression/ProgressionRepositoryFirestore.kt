package com.android.streetworkapp.model.progression

import com.android.streetworkapp.model.user.User
import com.google.firebase.firestore.FirebaseFirestore

class ProgressionRepositoryFirestore(private val db: FirebaseFirestore) : ProgressionRepository {
  override fun getNewProgressionId(): String {
    TODO("Not yet implemented")
  }

  override fun getProgression(
      user: User,
      onSuccess: (Progression) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    TODO("Not yet implemented")
  }

  override fun updateProgression(progression: Progression) {
    TODO("Not yet implemented")
  }

  override suspend fun createProgression(uid: String) {
    TODO("Not yet implemented")
  }
}
