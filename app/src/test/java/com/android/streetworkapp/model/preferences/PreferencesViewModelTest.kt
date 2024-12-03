package com.android.streetworkapp.model.preferences

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class PreferencesViewModelTest {

  private lateinit var preferencesRepository: PreferencesRepository
  private lateinit var preferencesViewModel: PreferencesViewModel
  private val testDispatcher = StandardTestDispatcher()

  @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    preferencesRepository = mock()
    preferencesViewModel = PreferencesViewModel(preferencesRepository)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun getLoginStateCallsRepositoryAndUpdatesState() = runTest {
    whenever(preferencesRepository.getLoginState()).thenReturn(true)
    preferencesViewModel.getLoginState()
    testDispatcher.scheduler.advanceUntilIdle()
    assertEquals(true, preferencesViewModel.loginState.first())
    verify(preferencesRepository).getLoginState()
  }

  @Test
  fun getUidCallsRepositoryAndUpdatesState() = runTest {
    val uid = "user123"
    whenever(preferencesRepository.getUid()).thenReturn(uid)
    preferencesViewModel.getUid()
    testDispatcher.scheduler.advanceUntilIdle()
    assertEquals(uid, preferencesViewModel.uid.first())
    verify(preferencesRepository).getUid()
  }

  @Test
  fun getNameCallsRepositoryAndUpdatesState() = runTest {
    val name = "John Doe"
    whenever(preferencesRepository.getName()).thenReturn(name)
    preferencesViewModel.getName()
    testDispatcher.scheduler.advanceUntilIdle()
    assertEquals(name, preferencesViewModel.name.first())
    verify(preferencesRepository).getName()
  }

  @Test
  fun getScoreCallsRepositoryAndUpdatesState() = runTest {
    val score = 100
    whenever(preferencesRepository.getScore()).thenReturn(score)
    preferencesViewModel.getScore()
    testDispatcher.scheduler.advanceUntilIdle()
    assertEquals(score, preferencesViewModel.score.first())
    verify(preferencesRepository).getScore()
  }

  @Test
  fun setLoginStateCallsRepository() = runTest {
    val isLoggedIn = true
    preferencesViewModel.setLoginState(isLoggedIn)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(preferencesRepository).setLoginState(isLoggedIn)
  }

  @Test
  fun setUidCallsRepository() = runTest {
    val uid = "user123"
    preferencesViewModel.setUid(uid)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(preferencesRepository).setUid(uid)
  }

  @Test
  fun setNameCallsRepository() = runTest {
    val name = "John Doe"
    preferencesViewModel.setName(name)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(preferencesRepository).setName(name)
  }

  @Test
  fun setScoreCallsRepository() = runTest {
    val score = 100
    preferencesViewModel.setScore(score)
    testDispatcher.scheduler.advanceUntilIdle()
    verify(preferencesRepository).setScore(score)
  }
}
