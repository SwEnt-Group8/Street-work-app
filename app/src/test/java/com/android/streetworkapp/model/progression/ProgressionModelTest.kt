package com.android.streetworkapp.model.progression

import kotlinx.coroutines.test.runTest
import org.junit.Test

class ProgressionModelTest {

  @Test
  fun getOrAddProgressionCallsRepository() = runTest {
    assert(ScoreIncrease.ADD_EVENT.points == 30)
    assert(ScoreIncrease.JOIN_EVENT.points == 60)
    assert(ScoreIncrease.ADD_FRIEND.points == 90)
  }
}
