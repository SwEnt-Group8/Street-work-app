package com.android.streetworkapp.model.progression

import junit.framework.TestCase.assertEquals
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
  fun getOrAddProgressionCallsRepository() = runTest {
    progressionViewModel.getOrAddProgression("test")
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getOrAddProgression("test")
  }

  @Test
  fun getProgressionIdCallsRepository() = runTest {
    progressionViewModel.getNewProgressionId()
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).getNewProgressionId()
  }

  @Test
  fun checkAchievementCallsRepository() = runTest {
    progressionViewModel.checkAchievements(0, 1000)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(repository).updateProgressionWithAchievementAndGoal(any(), any(), any())
  }

  @Test
  fun getMedalTest() = runTest {
    assertEquals(MedalsAchievement.NONE, getMedalByScore(0))
    assertEquals(MedalsAchievement.BRONZE, getMedalByScore(100))
    assertEquals(MedalsAchievement.SILVER, getMedalByScore(1000))
    assertEquals(MedalsAchievement.GOLD, getMedalByScore(10000))
    assertEquals(MedalsAchievement.PLATINUM, getMedalByScore(100000))
  }
}
