package com.android.streetworkapp.model.progression

/** A repository interface for Progression data. */
interface ProgressionRepository {

  fun getNewProgressionId(): String

  suspend fun getOrAddProgression(uid: String): Progression

  suspend fun updateProgressionWithAchievementAndGoal(
      progressionId: String,
      achievements: List<String>,
      goal: Int
  )
}
