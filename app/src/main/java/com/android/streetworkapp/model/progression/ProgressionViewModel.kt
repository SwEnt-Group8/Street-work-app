package com.android.streetworkapp.model.progression

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

  private val _currentProgression = MutableStateFlow<Progression>(Progression())
  val currentProgression: StateFlow<Progression> = _currentProgression.asStateFlow()

  /**
   * Used to have a unique progressionId in the database.
   *
   * @return progressionId: The progression id
   */
  fun getNewProgressionId(): String {
    return repository.getNewProgressionId()
  }

  /**
   * Fetches or creates, if it doesn't exist, the user's Progression
   *
   * @param uid: the user's uid
   */
  fun getOrAddProgression(uid: String) {
    viewModelScope.launch { _currentProgression.value = repository.getOrAddProgression(uid) }
  }

  /**
   * Check the score of the user. With enough points, the user wins medals. This function should be
   * called EACH TIME points are added
   *
   * @param score: The current score of the user
   */
  fun checkScore(score: Int) =
      viewModelScope.launch {
        while (_currentProgression.value.currentGoal < score && score <= Ranks.PLATINUM.score) {
          val unlockedAchievement = getMedalByScore(_currentProgression.value.currentGoal)
          val nextMedalName =
              enumValues<MedalsAchievement>().getOrNull(unlockedAchievement.ordinal + 1)?.name
          nextMedalName
              ?.let { // if this executes this means that the user is not at the highest rank
                val newAchievements =
                    _currentProgression.value.achievements + unlockedAchievement.name
                _currentProgression.value.currentGoal = enumValueOf<Ranks>(it).score

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
