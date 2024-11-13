package com.android.streetworkapp.end2end

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class End2EndGeneral {



    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Tests everything included up to M2 except for everything that involves clicking on a park
     */
    @Test
    fun e2eNavigationAndDisplaysCorrectDetailsExceptForParks() {

    }
}