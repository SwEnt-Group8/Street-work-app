package com.android.streetworkapp.model.moderation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

open class TextModerationViewModel(val repository: TextModerationRepository) : ViewModel() {
  /**
   * Analyzes the given text (`content`) against specified thresholds to determine if it meets
   * moderation criteria.
   *
   * @param content The text to be analyzed.
   * @param onTextUnderThresholds A callback function to be executed if the text is considered clean
   *   (under the thresholds).
   * @param onTextOverThresholds A callback function to be executed if the text exceeds the
   *   thresholds.
   * @param thresholds A map of threshold values for different text moderation tags. If not
   *   provided, default values are used.
   */
  fun analyzeText(
      content: String,
      onTextUnderThresholds: () -> Unit,
      onTextOverThresholds: () -> Unit,
      thresholds: Map<TextModerationTags, Double> =
          PerspectiveAPIThresholds.DEFAULT_THRESHOLD_VALUES
  ) {
    viewModelScope.launch {
      if (repository.evaluateText(content, thresholds)) onTextUnderThresholds()
      else onTextOverThresholds()
    }
  }
}
