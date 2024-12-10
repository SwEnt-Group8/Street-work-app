package com.android.streetworkapp.model.progression

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.streetworkapp.model.progression.MedalsAchievement.BRONZE
import com.android.streetworkapp.model.progression.MedalsAchievement.GOLD
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
   * Checks if the user have won achievements
   *
   * @param numberOfFriends: The current numberOfFriends of the user
   * @param score: The current score of the user
   */
  fun checkAchievements(numberOfFriends: Int, score: Int) =
      viewModelScope.launch {
        val newAchievements = _currentProgression.value.achievements.toMutableList()

        enumValues<SocialAchievement>().forEach {
          if (it.numberOfFriends <= numberOfFriends && !newAchievements.contains(it.name)) {
            newAchievements += it.name
          }
        }

        enumValues<MedalsAchievement>().forEach {
          if (it.rank.score <= score && !newAchievements.contains(it.name)) {
            newAchievements += it.name
            val nextMedal = enumValues<MedalsAchievement>().getOrNull(it.ordinal + 1)

            if (nextMedal != null) {
              _currentProgression.value.currentGoal = nextMedal.rank.score
            }
          }
        }

        repository.updateProgressionWithAchievementAndGoal(
            _currentProgression.value.progressionId,
            newAchievements,
            _currentProgression.value.currentGoal)
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
    else -> PLATINUM
  }
}
