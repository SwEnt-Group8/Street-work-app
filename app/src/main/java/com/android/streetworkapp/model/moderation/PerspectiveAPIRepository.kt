package com.android.streetworkapp.model.moderation

import android.util.Log
import androidx.compose.material3.Text
import com.android.sample.BuildConfig
import com.squareup.okhttp.RequestBody
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import okhttp3.OkHttpClient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import java.net.HttpURLConnection.HTTP_OK

class PerspectiveAPIRepository(private val client: OkHttpClient): TextModerationRepository {
    private val DEBUG_PREFIX = "PerspectiveAPIRepository:"


    override fun evaluateText(
        content: String,
        thresholds: Map<TextModerationTags, Double>
    ): Boolean {
        if (content.isEmpty())
            return false

        when (val result = this.getTextAnnotations(content)) {
            is TextEvaluationResult.Error -> {
                Log.d(DEBUG_PREFIX, result.errorType.errorMessage)
                return false
            }
            is TextEvaluationResult.Success -> {
                for (resultAnnotation in result.annotations) {
                    thresholds[resultAnnotation.tag]?.let { thresholdTagProbability ->
                        if (resultAnnotation.probability > thresholdTagProbability)
                            return false //Text over one of the thresholds

                    } ?: run {
                        return false //invalid tag
                    }
                }

                return true //all the tags under the thresholds :)
            }
        }

    }

    //TODO: comment what this code does in the interface
    override fun getTextAnnotations(content: String): TextEvaluationResult {
        //I don't perform any checks on the content, I'll let the api handle the error checking

        val requestMediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = this.formatPostRequestBody(content).toRequestBody(requestMediaType)

        //note: someone could get the key by sniffing the packets, but since we can't have a backend here we are :)
        val request = Request.Builder().url("https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=${BuildConfig.PERSPECTIVE_API_KEY}").post(requestBody).build()

        val response = client.newCall(request).execute()
        when(response.code) {
            HTTP_OK -> {
                val responseBody = response.body?.string() ?: run { return TextEvaluationResult.Error(PerspectiveApiErrors.EMPTY_BODY_RESPONSE) }
                val annotations = this.extractTagsAndProbabilitiesFromResponseBody(responseBody)
                annotations?.let {
                    return TextEvaluationResult.Success(annotations)
                } ?: run {
                    return TextEvaluationResult.Error(PerspectiveApiErrors.JSON_DESERIALIZATION_ERROR)
                }
            }
            HTTP_BAD_REQUEST -> {
                val error = Json.decodeFromString<ErrorResponse>(response.body.toString())
                return TextEvaluationResult.Error(enumValues<PerspectiveApiErrors>().find { it.name == error.status  } ?: PerspectiveApiErrors.UNKNOWN_ERROR)
            }
            else -> {
                Log.d(this.DEBUG_PREFIX, "Received unsupported http code (${response.code}) from api")
                return TextEvaluationResult.Error(PerspectiveApiErrors.UNSUPPORTED_HTTP_CODE)
            }
        }
    }

    private fun formatPostRequestBody(content: String): String {
        val request = Request(
            comment = Comment(content),
            requestedAttributes = TextModerationTags.entries.associate { it.name to Unit },
            languages = emptyList(), //we don't know the input language, we'll let the api decide
            doNotStore = true //we don't want the api to store our inputs
            )

        return Json.encodeToJsonElement(request).toString()
    }

    private fun extractTagsAndProbabilitiesFromResponseBody(responseBody: String): List<TagAnnotation>? {
        try {
            val attributeScoresMap = Json.decodeFromString<SuccessResponse>(responseBody).attributeScores
            val tagsAnnotations = attributeScoresMap.map { (tag, attributeScore) ->
                TagAnnotation(
                    enumValueOf<TextModerationTags>(tag),
                    attributeScore.summaryScore.value
                )
            }

            return tagsAnnotations
        } catch (e : Exception) {
            Log.d(this.DEBUG_PREFIX, "Failed to map response body into valid List<TagAnnotation>")
            return null
        }
    }
}




