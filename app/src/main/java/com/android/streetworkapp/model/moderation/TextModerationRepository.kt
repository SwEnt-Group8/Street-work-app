package com.android.streetworkapp.model.moderation

interface TextModerationRepository {
    suspend fun evaluateText(content: String): List<TagAnnotation>?
    fun formatPostRequestBody(content: String) : String
}