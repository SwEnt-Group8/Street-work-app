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
   * @param onTextEvaluationResult A callback function to be executed if the text has been
   *   successfully evaluated. The value passed as parameter is true if the text is under all
   *   thresholds, false otherwise
   * @param onTextEvaluationError A callback function to be executed if an error occurs during the
   *   request.
   * @param thresholds A map of threshold values for different text moderation tags. If not
   *   provided, default values are used.
   */
  fun analyzeText(
      content: String,
      onTextEvaluationResult: (Boolean) -> Unit,
      onTextEvaluationError: () -> Unit,
      thresholds: Map<TextModerationTags, Double> =
          PerspectiveAPIThresholds.DEFAULT_THRESHOLD_VALUES
  ) {
    viewModelScope.launch {
      when (val result = repository.evaluateText(content, thresholds)) {
        is TextEvaluation.Error -> onTextEvaluationError()
        is TextEvaluation.Result -> onTextEvaluationResult(result.isTextUnderThresholds)
      }
    }
  }
}

/** Used by the repositories to return the result to the viewmodel */
sealed class TextEvaluation {
  data class Result(val isTextUnderThresholds: Boolean) : TextEvaluation()

  data class Error(val errorMessage: String) : TextEvaluation()
}
