package com.android.streetworkapp.model.moderation

import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

class PerspectiveAPIRepository(private val client: OkHttpClient): TextModerationRepository {
    override suspend fun evaluateText(content: String): List<TagAnnotation>? {
        if (content.isEmpty())
            return null


    }

    override fun formatPostRequestBody(content: String): String {
        val request = Request(
            comment = Comment(content),
            requestedAttributes = TEXT_MODERATION_TAGS.entries.associate { it.name to Unit },
            languages = emptyList(), //we don't know the input language, we'll let the api decide
            doNotStore = true //we don't want the api to store our inputs
            )

        return Json.encodeToJsonElement(request).toString()
    }

}

@Serializable
data class Request(
    val comment: Comment,
    val requestedAttributes: Map<String, Unit>, // Representing each attribute as an empty object
    val languages: List<String>,
    val doNotStore: Boolean
)

@Serializable
data class Comment(
    val text: String
)