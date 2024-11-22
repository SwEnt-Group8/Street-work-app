package com.android.streetworkapp.model.moderation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// All the definitions below are used to interact with Perspective API

/** Default values we can use as thresholds for our TextModerationTags */
object PerspectiveAPIThresholds {
  val DEFAULT_THRESHOLD_VALUES = TextModerationTags.entries.associateWith { 0.2 }
}

/** All the tags we want to fetch from the API */
enum class TextModerationTags {
  TOXICITY,
  INSULT,
  THREAT,
  PROFANITY
}

/** Used to store the results from the API */
data class TagAnnotation(val tag: TextModerationTags, val probability: Double)

/** Used to return the result of a query to PerspectiveAPI */
sealed class PerspectiveAPIEvaluationResult {
  data class Success(val annotations: List<TagAnnotation>) : PerspectiveAPIEvaluationResult()

  data class Error(val errorType: PerspectiveApiErrors) : PerspectiveAPIEvaluationResult()
}

// Request data classes
@Serializable
data class Request(
    val comment: Comment,
    val requestedAttributes: Map<String, Unit>,
    val languages: List<String>,
    val doNotStore: Boolean
)

@Serializable data class Comment(val text: String)

// Responses data classes
@Serializable
data class SuccessResponse(
    val attributeScores: Map<String, AttributeScore>,
    val languages: List<String>,
    val detectedLanguages: List<String>
)

@Serializable data class AttributeScore(val spanScores: List<SpanScore>, val summaryScore: Score)

@Serializable data class SpanScore(val begin: Int, val end: Int, val score: Score)

@Serializable data class Score(val value: Double, val type: String)

// errors def
@Serializable data class ErrorResponse(val error: ErrorDetail)

@Serializable
data class ErrorDetail(
    val code: Int,
    val message: String,
    val status: String,
    val details: List<ErrorExtraDetails>
)

@Serializable
data class ErrorExtraDetails(@SerialName("@type") val type: String, val errorType: String)

enum class PerspectiveApiErrors(val errorMessage: String) {
  // Official errors from the Perspective API's website
  /** Errors related to quota limits */
  QUOTA_EXCEEDED("The request exceeds your quota."),

  /** Errors related to invalid input */
  INVALID_ARGUMENT("The request contains invalid arguments."),

  /** Errors related to missing authorization */
  UNAUTHENTICATED("The request is missing valid authentication credentials."),

  /** Errors related to forbidden actions */
  PERMISSION_DENIED("You do not have permission to access this resource."),

  /** Errors related to non-existent resources */
  NOT_FOUND("The specified resource was not found."),

  /** Errors related to exceeding allowed limits */
  RESOURCE_EXHAUSTED("You have exceeded your API limits."),

  /** Errors due to server issues */
  INTERNAL_ERROR("An internal error occurred in the API."),

  /** Errors related to service being unavailable */
  UNAVAILABLE("The service is currently unavailable. Please try again later."),

  /** Errors due to unimplemented functionality */
  UNIMPLEMENTED("The requested functionality is not implemented."),

  /** Unknown or unexpected errors */
  UNKNOWN_ERROR("An unknown error occurred."),

  // Custom errors
  /** Failed to deserialize the response gotten from the API */
  JSON_DESERIALIZATION_ERROR("Failed to deserialize input from Perspective API response"),

  /** Received a HTTP code not included in {200, 400} */
  UNSUPPORTED_HTTP_CODE("Received an unsupported HTTP code"),

  /** Received an empty body as API response */
  EMPTY_BODY_RESPONSE("Received an empty body in Perspective API response"),
    /** Got an exception after executing network call **/
    NETWORK_ERROR("Something went wrong with the network request")
}
