package com.android.streetworkapp.model.moderation

import android.util.Log
import com.squareup.okhttp.RequestBody
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import okhttp3.OkHttpClient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class PerspectiveAPIRepository(private val client: OkHttpClient): TextModerationRepository {
    override suspend fun evaluateText(content: String): List<TagAnnotation>? {
        if (content.isEmpty())
            return null

        val requestMediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = this.formatPostRequestBody(content).toRequestBody(requestMediaType)

        //TODO: parse api key
        //note: I'm pretty sure someone could get the key by sniffing the packets, but since we can't have a backend here we are :)
        val request = Request.Builder().url("https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=").post(requestBody).build()

        val response = client.newCall(request).execute()
        //TODO: check error codes here

        val jsonResponseBody = Json.parseToJsonElement(response.body.toString())

        //TODO: continue here

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

    override fun extractTagAndProbabilitiesFromResponseBody(responseBody: String): List<TagAnnotation>? {
        val attributeScoresMap = Json.decodeFromString<Map<String, AttributeScore>>(responseBody)

        try {
            val tagsAnnotations = attributeScoresMap.map { (tag, attributeScore) ->
                TagAnnotation(
                    enumValueOf<TEXT_MODERATION_TAGS>(tag),
                    attributeScore.summaryScore.value
                )
            }

            return tagsAnnotations
        } catch (e : Exception) {
            Log.d("PerspectiveApiRepository:", "Failed to map response body into valid List<TagAnnotation>")
            return null
        }
    }
}


//Request data classes
@Serializable
data class Request(
    val comment: Comment,
    val requestedAttributes: Map<String, Unit>,
    val languages: List<String>,
    val doNotStore: Boolean
)

@Serializable
data class Comment(
    val text: String
)

//Responses data classes
@Serializable
data class AttributeScore(
    val spanScores: List<SpanScore>,
    val summaryScore: Score
)

@Serializable
data class SpanScore(
    val begin: Int,
    val end: Int,
    val score: Score
)

@Serializable
data class Score(
    val value: Double,
    val type: String
)
