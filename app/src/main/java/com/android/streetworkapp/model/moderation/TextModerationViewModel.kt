package com.android.streetworkapp.model.moderation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

open class TextModerationViewModel(val repository: TextModerationRepository) : ViewModel() {
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
