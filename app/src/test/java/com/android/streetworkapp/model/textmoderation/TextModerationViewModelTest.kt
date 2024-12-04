package com.android.streetworkapp.model.textmoderation

import com.android.streetworkapp.model.moderation.TextEvaluation
import com.android.streetworkapp.model.moderation.TextModerationRepository
import com.android.streetworkapp.model.moderation.TextModerationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TextModerationViewModelTest {

  @Mock private lateinit var textModerationRepository: TextModerationRepository
  @InjectMocks private lateinit var textModerationViewModel: TextModerationViewModel

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
  fun underThresholdsIsCalledIfTextIsUnderThresholds() = runTest {
    whenever(textModerationRepository.evaluateText(any(), any()))
        .thenReturn(TextEvaluation.Result(true))

    val onEvaluationResultCallback = mock<(Boolean) -> Unit>()
    textModerationViewModel.analyzeText("content", { onEvaluationResultCallback(it) }, {})
    verify(onEvaluationResultCallback).invoke(true)
  }

  @Test
  fun overThresholdsIsCalledIfTextIsOverThresholds() = runTest {
    whenever(textModerationRepository.evaluateText(any(), any()))
        .thenReturn(TextEvaluation.Result(false))

    val onEvaluationResultCallback = mock<(Boolean) -> Unit>()
    textModerationViewModel.analyzeText("content", { onEvaluationResultCallback(it) }, {})
    verify(onEvaluationResultCallback).invoke(false)
  }
}
