package com.android.streetworkapp.model.progression

import kotlinx.coroutines.test.runTest

import org.junit.Test


class ProgressionModelTest {

    @Test
    fun getOrAddProgressionCallsRepository() = runTest {
        assert(ScoreIncrease.CREATE_EVENT.scoreAdded == 30)
        assert(ScoreIncrease.JOIN_EVENT.scoreAdded == 60)
        assert(ScoreIncrease.ADD_FRIEND.scoreAdded == 90)
    }
}