package com.android.streetworkapp.model.moderation

interface TextModerationRepository {
    fun evaluateText(content: String): TextEvaluationResult
}