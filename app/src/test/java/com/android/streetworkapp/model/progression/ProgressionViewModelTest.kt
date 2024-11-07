package com.android.streetworkapp.model.progression

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any

class ProgressionViewModelTest {

  private lateinit var repository: ProgressionRepository
  private lateinit var progressionViewModel: ProgressionViewModel
  private val testDispatcher = StandardTestDispatcher()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    repository = mock()
    progressionViewModel = ProgressionViewModel(repository)
  }

  @Test
  fun createProgressionCallsRepository() = runTest {
    progressionViewModel.createProgression("test", "test")
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).createProgression("test", "test")
  }

  @Test
  fun getProgressionCallsRepository() = runTest {
    progressionViewModel.getCurrentProgression("test")
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getProgression(any(), any(), any())
  }

  @Test
  fun getProgressionIdCallsRepository() = runTest {
    progressionViewModel.getNewProgressionId()
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getNewProgressionId()
  }

  @Test
  fun checkScoreCallsRepository2() = runTest {
    progressionViewModel.checkScore(1000)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).updateProgressionWithAchievementAndGoal(any(), any(), any())
  }
}
