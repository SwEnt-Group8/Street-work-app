package com.android.streetworkapp.model.progression

interface ProgressionRepository {

  fun getNewProgressionId(): String

  suspend fun getProgression(uid: String): Progression

  suspend fun updateProgressionWithAchievementAndGoal(
      progressionId: String,
      achievements: List<String>,
      goal: Int
  )

  suspend fun createProgression(uid: String, progressionId: String)
}
