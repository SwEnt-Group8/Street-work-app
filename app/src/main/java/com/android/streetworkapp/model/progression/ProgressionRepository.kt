package com.android.streetworkapp.model.progression

/** A repository interface for Progression data. */
interface ProgressionRepository {

  fun getNewProgressionId(): String

  fun getProgression(uid: String, onSuccess: (Progression) -> Unit, onFailure: (Exception) -> Unit)

  suspend fun updateProgressionWithAchievementAndGoal(
      progressionId: String,
      achievements: List<String>,
      goal: Int
  )

  suspend fun createProgression(uid: String, progressionId: String)
}
