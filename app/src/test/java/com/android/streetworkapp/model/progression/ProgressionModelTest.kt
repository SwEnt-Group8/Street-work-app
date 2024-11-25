package com.android.streetworkapp.model.progression

import kotlinx.coroutines.test.runTest
import org.junit.Test

class ProgressionModelTest {

  @Test
  fun assertHierarchyOfPointsInProgression() = runTest {
    assert(ScoreIncrease.ADD_EVENT.points < ScoreIncrease.JOIN_EVENT.points)
    assert(ScoreIncrease.JOIN_EVENT.points < ScoreIncrease.ADD_FRIEND.points)
  }
}
