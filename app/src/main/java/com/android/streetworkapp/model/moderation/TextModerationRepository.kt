package com.android.streetworkapp.model.moderation

interface TextModerationRepository {
    suspend fun evaluateText(content: String): TextEvaluationResult
    fun formatPostRequestBody(content: String): String
    fun extractTagsAndProbabilitiesFromResponseBody(responseBody: String): List<TagAnnotation>?
}