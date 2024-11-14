package com.android.streetworkapp.model.progression

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.streetworkapp.model.progression.MedalsAchievement.BRONZE
import com.android.streetworkapp.model.progression.MedalsAchievement.GOLD
import com.android.streetworkapp.model.progression.MedalsAchievement.NONE
import com.android.streetworkapp.model.progression.MedalsAchievement.PLATINUM
import com.android.streetworkapp.model.progression.MedalsAchievement.SILVER
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class ProgressionViewModel(private val repository: ProgressionRepository) : ViewModel() {

  private val _currentProgression = MutableStateFlow(Progression())
  val currentProgression: StateFlow<Progression> = _currentProgression.asStateFlow()

  companion object {
    private const val ERROR_UID_EMPTY = "The uid must not be empty."
  }

  /**
   * Used to have a unique progressionId in the database.
   *
   * @return progressionId: The progression id
   */
  fun getNewProgressionId(): String {
    return repository.getNewProgressionId()
  }

  /**
   * Fetch the progression linked to the given uid
   *
   * @param uid: The uid (User Id)
   */
  fun getCurrentProgression(uid: String) {
    require(uid.isNotEmpty()) { ERROR_UID_EMPTY }

    repository.getProgression(
        uid = uid,
        onSuccess = { progression -> _currentProgression.value = progression },
        onFailure = { Log.e("FirestoreError", "Error getting events: ${it.message}") })
  }

  /**
   * Create the progression linked to the given uid
   *
   * @param uid: The uid (User Id)
   * @param progressionId: The id of the "progression" object
   */
  fun createProgression(uid: String, progressionId: String) =
      viewModelScope.launch { repository.createProgression(uid, progressionId) }

  /**
   * Check the score of the user. With enough points, the user wins medals.
   * This function should be called EACH TIME points are added
   *
   * @param score: The current score of the user
   */
  fun checkScore(score: Int) =
      viewModelScope.launch {
        if (_currentProgression.value.currentGoal < score) {
          val unlockedAchievement = getMedalByScore(_currentProgression.value.currentGoal)
          val nextMedalName =
              enumValues<MedalsAchievement>().getOrNull(unlockedAchievement.ordinal + 1)?.name
          nextMedalName
              ?.let { // if this executes this means that the user is not at the highest rank
                val newAchievements =
                    _currentProgression.value.achievements + unlockedAchievement.name
                currentProgression.value.currentGoal = enumValueOf<Ranks>(it).score

                repository.updateProgressionWithAchievementAndGoal(
                    _currentProgression.value.progressionId,
                    newAchievements,
                    _currentProgression.value.currentGoal)
              }
        }
      }
}

/**
 * Used to find the medal linked to a given score.
 *
 * @param score: The next goal score of the user
 */
fun getMedalByScore(score: Int): MedalsAchievement {
  return when (score) {
    Ranks.BRONZE.score -> BRONZE
    Ranks.SILVER.score -> SILVER
    Ranks.GOLD.score -> GOLD
    Ranks.PLATINUM.score -> PLATINUM
    else -> NONE
  }
}
