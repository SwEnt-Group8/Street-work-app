package com.android.streetworkapp.model.progression

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.streetworkapp.model.progression.MedalsAchievement.BRONZE
import com.android.streetworkapp.model.progression.MedalsAchievement.GOLD
import com.android.streetworkapp.model.progression.MedalsAchievement.NONE
import com.android.streetworkapp.model.progression.MedalsAchievement.SILVER
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class ProgressionViewModel(private val repository: ProgressionRepository) : ViewModel() {

  private val _currentProgression = MutableStateFlow(Progression())
  val currentProgression: StateFlow<Progression> = _currentProgression.asStateFlow()

  fun getNewProgressionId(): String {
    return repository.getNewProgressionId()
  }

  suspend fun getCurrentProgression(uid: String) {
    _currentProgression.value = repository.getProgression(uid)
  }

  fun createProgression(uid: String,progressionId: String) = viewModelScope.launch { repository.createProgression(uid,progressionId) }

  suspend fun checkScore(score: Int) {
    if (_currentProgression.value.currentGoal < score) {

      _currentProgression.value.currentGoal *= 10
      val newAchievements =
          _currentProgression.value.achievements +
              getMedalByScore(_currentProgression.value.currentGoal).name

      _currentProgression.value.achievements = newAchievements

      repository.updateProgressionWithAchievementAndGoal(_currentProgression.value.progressionId,newAchievements,_currentProgression.value.currentGoal)
    }
  }
}

fun getMedalByScore(score: Int): MedalsAchievement {
  return when (score) {
    Ranks.BRONZE.score -> BRONZE
    Ranks.SILVER.score -> SILVER
    Ranks.GOLD.score -> GOLD
    else -> NONE
  }
}
