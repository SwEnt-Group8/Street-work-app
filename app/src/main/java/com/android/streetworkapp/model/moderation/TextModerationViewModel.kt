package com.android.streetworkapp.model.moderation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TextModerationViewModel(val repository: TextModerationRepository): ViewModel() {
    fun analyzeText(content: String, onTextUnderThresholds: () -> Unit, onTextOverThresholds: () -> Unit, thresholds: Map<TextModerationTags, Double> = PerspectiveAPIThresholds.DEFAULT_THRESHOLD_VALUES): Boolean {
        viewModelScope.launch {
            if (repository.evaluateText(content, thresholds))
                onTextUnderThresholds()
            else
                onTextOverThresholds()
        }
    }
}


