package com.android.streetworkapp.model.moderation

import android.util.Log
import com.android.sample.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import java.net.HttpURLConnection.HTTP_OK
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class PerspectiveAPIRepository(private val client: OkHttpClient) : TextModerationRepository {
  private val DEBUG_PREFIX = "PerspectiveAPIRepository:"

  /**
   * Evaluates the text
   *
   * @param content Text to be analyzed
   * @return True if the text is under all thresholds, false if the text is over at least one
   *   threshold
   */
  override suspend fun evaluateText(
      content: String,
      thresholds: Map<TextModerationTags, Double>
  ): TextEvaluation {
    if (content.isEmpty()) return TextEvaluation.Result(true)

    when (val result = this.getTextAnnotations(content)) {
      is PerspectiveAPIEvaluationResult.Error -> {
        Log.d(this.DEBUG_PREFIX, result.errorType.errorMessage)
        return TextEvaluation.Error(result.errorType.errorMessage)
      }
      is PerspectiveAPIEvaluationResult.Success -> {
        for (resultAnnotation in result.annotations) {
          thresholds[resultAnnotation.tag]?.let { thresholdTagProbability ->
            if (resultAnnotation.probability > thresholdTagProbability)
                return TextEvaluation.Result(false) // Text over one of the thresholds
          }
              ?: run {
                return TextEvaluation.Result(false) // invalid tag
              }
        }

        return TextEvaluation.Result(true) // all the tags under the thresholds :)
      }
    }
  }

  /**
   * Gets the tags and their probabilities for param content
   *
   * @param content Text to be analyzed
   * @return TextEvaluationResult.Success if the API could process the content,
   *   TextEvaluationResult.Error if an error was encountered
   */
  private suspend fun getTextAnnotations(content: String): PerspectiveAPIEvaluationResult {
      return withContext(Dispatchers.IO) {
          try {
              // Prepare the request
              val requestMediaType = "application/json; charset=utf-8".toMediaType()
              val requestBody = formatPostRequestBody(content).toRequestBody(requestMediaType)

              val url = HttpUrl.Builder()
                  .scheme("https")
                  .host("commentanalyzer.googleapis.com")
                  .addPathSegment("v1alpha1")
                  .addPathSegment("comments:analyze")
                  .addQueryParameter("key", BuildConfig.PERSPECTIVE_API_KEY)
                  .build()

              val request = Request.Builder().url(url).post(requestBody).build()

              // Execute the request
              val response = client.newCall(request).execute()

              // Handle response
              when (response.code) {
                  HTTP_OK -> {
                      val responseBody = response.body?.string()
                          ?: return@withContext PerspectiveAPIEvaluationResult.Error(
                              PerspectiveApiErrors.EMPTY_BODY_RESPONSE
                          )
                      val annotations = extractTagsAndProbabilitiesFromResponseBody(responseBody)
                      annotations?.let {
                          PerspectiveAPIEvaluationResult.Success(annotations)
                      } ?: PerspectiveAPIEvaluationResult.Error(
                          PerspectiveApiErrors.JSON_DESERIALIZATION_ERROR
                      )
                  }
                  HTTP_BAD_REQUEST -> {
                      val responseBody = response.body?.string()
                          ?: return@withContext PerspectiveAPIEvaluationResult.Error(
                              PerspectiveApiErrors.EMPTY_BODY_RESPONSE
                          )
                      val error = extractErrorFromResponseBody(responseBody)
                      PerspectiveAPIEvaluationResult.Error(error)
                  }
                  else -> {
                      Log.d(DEBUG_PREFIX, "Received unsupported HTTP code (${response.code}) from API")
                      PerspectiveAPIEvaluationResult.Error(PerspectiveApiErrors.UNSUPPORTED_HTTP_CODE)
                  }
              }
          } catch (e: Exception) {
              Log.e(DEBUG_PREFIX, "Network error: ${e.message}", e)
              PerspectiveAPIEvaluationResult.Error(PerspectiveApiErrors.NETWORK_ERROR)
          }
      }
  }

  private fun formatPostRequestBody(content: String): String {
    val request =
        Request(
            comment = Comment(content),
            requestedAttributes = TextModerationTags.entries.associate { it.name to Unit },
            languages = emptyList(), // we don't know the input language, we'll let the api decide
            doNotStore = true // we don't want the api to store our inputs
            )

    return Json.encodeToJsonElement(request).toString()
  }

  private fun extractTagsAndProbabilitiesFromResponseBody(
      responseBody: String
  ): List<TagAnnotation>? {
    try {
      val attributeScoresMap = Json.decodeFromString<SuccessResponse>(responseBody).attributeScores
      val tagsAnnotations =
          attributeScoresMap.map { (tag, attributeScore) ->
            TagAnnotation(enumValueOf<TextModerationTags>(tag), attributeScore.summaryScore.value)
          }

      return tagsAnnotations
    } catch (e: Exception) {
      Log.d(this.DEBUG_PREFIX, "Failed to map response body into valid List<TagAnnotation>")
      return null
    }
  }

  private fun extractErrorFromResponseBody(responseBody: String): PerspectiveApiErrors {
    try {
      val error = Json.decodeFromString<ErrorResponse>(responseBody)
      return enumValues<PerspectiveApiErrors>().find { it.name == error.error.status }
          ?: PerspectiveApiErrors.UNKNOWN_ERROR
    } catch (e: Exception) {
      return PerspectiveApiErrors.JSON_DESERIALIZATION_ERROR
    }
  }
}
