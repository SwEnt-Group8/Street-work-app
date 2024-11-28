package com.android.streetworkapp.model.textmoderation

import com.android.streetworkapp.model.moderation.PerspectiveAPIRepository
import com.android.streetworkapp.model.moderation.PerspectiveAPIThresholds
import com.android.streetworkapp.model.moderation.PerspectiveApiErrors
import com.android.streetworkapp.model.moderation.TextEvaluation
import com.android.streetworkapp.model.moderation.TextModerationTags
import java.net.HttpURLConnection.HTTP_BAD_REQUEST
import java.net.HttpURLConnection.HTTP_OK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class PerspectiveAPIRepositoryTest {

  @Mock private lateinit var okHttpClient: OkHttpClient
  @Mock private lateinit var response: Response
  @Mock private lateinit var call: Call
  @Mock private lateinit var responseBody: ResponseBody

  @InjectMocks private lateinit var perspectiveAPIRepository: PerspectiveAPIRepository

  // API responses
  private val PERSPECTIVE_API_INVALID_ARGUMENT_RESPONSE =
      """{
        "error": {
        "code": 400,
        "message": "Comment must be non-empty.",
        "status": "INVALID_ARGUMENT",
        "details": [
        {
            "@type": "type.googleapis.com/google.commentanalyzer.v1alpha1.Error",
            "errorType": "COMMENT_EMPTY"
        }
        ]
    }
    }"""

  private val PERSPECTIVE_API_VALID_RESPONSE_UNDER_THRESHOLDS =
      """{
    "attributeScores": {
        "TOXICITY": {
            "spanScores": [
                {
                    "begin": 0,
                    "end": 5,
                    "score": {
                        "value": 0.017341165,
                        "type": "PROBABILITY"
                    }
                }
            ],
            "summaryScore": {
                "value": 0.00,
                "type": "PROBABILITY"
            }
        },
        "THREAT": {
            "spanScores": [
                {
                    "begin": 0,
                    "end": 5,
                    "score": {
                        "value": 0.008427517,
                        "type": "PROBABILITY"
                    }
                }
            ],
            "summaryScore": {
                "value": 0.00,
                "type": "PROBABILITY"
            }
        },
        "INSULT": {
            "spanScores": [
                {
                    "begin": 0,
                    "end": 5,
                    "score": {
                        "value": 0.009051885,
                        "type": "PROBABILITY"
                    }
                }
            ],
            "summaryScore": {
                "value": 0.00,
                "type": "PROBABILITY"
            }
        }
    },
    "languages": [
        "en"
    ],
    "detectedLanguages": [
        "en"
    ]
}"""

  private val PERSPECTIVE_API_VALID_RESPONSE_OVER_THRESHOLDS =
      """{
    "attributeScores": {
        "TOXICITY": {
            "spanScores": [
                {
                    "begin": 0,
                    "end": 5,
                    "score": {
                        "value": ${0.5* (1.0 + PerspectiveAPIThresholds.DEFAULT_THRESHOLD_VALUES[TextModerationTags.TOXICITY]!!)},
                        "type": "PROBABILITY"
                    }
                }
            ],
            "summaryScore": {
                "value": ${0.5* (1.0 + PerspectiveAPIThresholds.DEFAULT_THRESHOLD_VALUES[TextModerationTags.TOXICITY]!!)},
                "type": "PROBABILITY"
            }
        },
        "THREAT": {
            "spanScores": [
                {
                    "begin": 0,
                    "end": 5,
                    "score": {
                        "value": ${0.5* (1.0 + PerspectiveAPIThresholds.DEFAULT_THRESHOLD_VALUES[TextModerationTags.THREAT]!!)},
                        "type": "PROBABILITY"
                    }
                }
            ],
            "summaryScore": {
                "value": ${0.5* (1.0 + PerspectiveAPIThresholds.DEFAULT_THRESHOLD_VALUES[TextModerationTags.THREAT]!!)},
                "type": "PROBABILITY"
            }
        },
        "INSULT": {
            "spanScores": [
                {
                    "begin": 0,
                    "end": 5,
                    "score": {
                        "value": ${0.5* (1.0 + PerspectiveAPIThresholds.DEFAULT_THRESHOLD_VALUES[TextModerationTags.INSULT]!!)},
                        "type": "PROBABILITY"
                    }
                }
            ],
            "summaryScore": {
                "value": ${0.5* (1.0 + PerspectiveAPIThresholds.DEFAULT_THRESHOLD_VALUES[TextModerationTags.INSULT]!!)},
                "type": "PROBABILITY"
            }
        }
    },
    "languages": [
        "en"
    ],
    "detectedLanguages": [
        "en"
    ]
}"""

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    Dispatchers.setMain(Dispatchers.Unconfined)
    MockitoAnnotations.openMocks(this)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun cleanUp() {
    Dispatchers.resetMain()
  }

  @Test
  fun evaluateTextReturnsErrorAndCorrectErrorMessageOnUnhandledHTPPResponseCode() = runTest {
    whenever(okHttpClient.newCall(any())).thenReturn(call)
    whenever(call.execute()).thenReturn(response)
    whenever(response.code).thenReturn(-1)

    when (val result =
        perspectiveAPIRepository.evaluateText(
            "content", PerspectiveAPIThresholds.DEFAULT_THRESHOLD_VALUES)) {
      is TextEvaluation.Error ->
          assert(result.errorMessage == PerspectiveApiErrors.UNSUPPORTED_HTTP_CODE.errorMessage)
      is TextEvaluation.Result -> assert(false)
    }
  }

  @Test
  fun evaluateTextReturnsErrorOnInvalidParameterRequestAndCorrectErrorMessage() = runTest {
    whenever(okHttpClient.newCall(any())).thenReturn(call)
    whenever(call.execute()).thenReturn(response)
    whenever(response.code).thenReturn(HTTP_BAD_REQUEST)
    whenever(response.body).thenReturn(responseBody)
    whenever(responseBody.string()).thenReturn(PERSPECTIVE_API_INVALID_ARGUMENT_RESPONSE)

    when (val result =
        perspectiveAPIRepository.evaluateText(
            "content", PerspectiveAPIThresholds.DEFAULT_THRESHOLD_VALUES)) {
      is TextEvaluation.Error ->
          assert(result.errorMessage == PerspectiveApiErrors.INVALID_ARGUMENT.errorMessage)
      is TextEvaluation.Result -> assert(false)
    }
  }

  @Test
  fun evaluateTextReturnsErrorAndCorrectErrorMessageOnDeserializationError() = runTest {
    whenever(okHttpClient.newCall(any())).thenReturn(call)
    whenever(call.execute()).thenReturn(response)
    whenever(response.code).thenReturn(HTTP_BAD_REQUEST)
    whenever(response.body).thenReturn(responseBody)
    whenever(responseBody.string()).thenReturn("###@@@#§") // making the deserialization fail

    when (val result =
        perspectiveAPIRepository.evaluateText(
            "content", PerspectiveAPIThresholds.DEFAULT_THRESHOLD_VALUES)) {
      is TextEvaluation.Error ->
          assert(
              result.errorMessage == PerspectiveApiErrors.JSON_DESERIALIZATION_ERROR.errorMessage)
      is TextEvaluation.Result -> assert(false)
    }
  }

  @Test
  fun evaluateTextReturnsErrorAndCorrectErrorMessageOnAPIResponseSuccessAndDeserializationError() =
      runTest {
        whenever(okHttpClient.newCall(any())).thenReturn(call)
        whenever(call.execute()).thenReturn(response)
        whenever(response.code).thenReturn(HTTP_OK)
        whenever(response.body).thenReturn(responseBody)
        whenever(responseBody.string()).thenReturn("###@@@#§") // making the deserialization fail

        when (val result =
            perspectiveAPIRepository.evaluateText(
                "content", PerspectiveAPIThresholds.DEFAULT_THRESHOLD_VALUES)) {
          is TextEvaluation.Error ->
              assert(
                  result.errorMessage ==
                      PerspectiveApiErrors.JSON_DESERIALIZATION_ERROR.errorMessage)
          is TextEvaluation.Result -> assert(false)
        }
      }

  @Test
  fun evaluateTextReturnsResultAndTrueOnAPIResponseSuccessAndUnderThresholdValues() = runTest {
    whenever(okHttpClient.newCall(any())).thenReturn(call)
    whenever(call.execute()).thenReturn(response)
    whenever(response.code).thenReturn(HTTP_OK)
    whenever(response.body).thenReturn(responseBody)
    whenever(responseBody.string()).thenReturn(PERSPECTIVE_API_VALID_RESPONSE_UNDER_THRESHOLDS)

    when (val result =
        perspectiveAPIRepository.evaluateText(
            "content", PerspectiveAPIThresholds.DEFAULT_THRESHOLD_VALUES)) {
      is TextEvaluation.Error -> assert(false)
      is TextEvaluation.Result -> assert(result.isTextUnderThresholds)
    }
  }

  @Test
  fun evaluateTextReturnsResultAndFalseOnAPIResponseSuccessAndOverThresholdValues() = runTest {
    whenever(okHttpClient.newCall(any())).thenReturn(call)
    whenever(call.execute()).thenReturn(response)
    whenever(response.code).thenReturn(HTTP_OK)
    whenever(response.body).thenReturn(responseBody)
    whenever(responseBody.string()).thenReturn(PERSPECTIVE_API_VALID_RESPONSE_OVER_THRESHOLDS)

    when (val result =
        perspectiveAPIRepository.evaluateText(
            "content", PerspectiveAPIThresholds.DEFAULT_THRESHOLD_VALUES)) {
      is TextEvaluation.Error -> assert(false)
      is TextEvaluation.Result -> assert(!result.isTextUnderThresholds)
    }
  }
}
