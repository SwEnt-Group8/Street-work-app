package com.android.streetworkapp.utils

import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.S])
@RunWith(RobolectricTestRunner::class)
class GraphTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        System.setProperty("ro.build.fingerprint", "test_fingerprint")
        println("Mocked ro.build.fingerprint: ${System.getProperty("ro.build.fingerprint")}")
    }

    @Test
    fun testGraphRendersAxesAndLabels() {
        val configuration = GraphConfiguration(
            graphColor = Color.Blue,
            axisColor = Color.Black,
            xUnitLabel = "Time",
            yUnitLabel = "Reps",
            dataPoints = listOf(GraphData(0f, 0f), GraphData(1f, 1f))
        )

        composeTestRule.setContent {
            Graph(modifier = Modifier.fillMaxSize(), graphConfiguration = configuration)
        }

        // Assert X-axis label
        composeTestRule.onNodeWithText("Time").assertExists()

        // Assert Y-axis label
        composeTestRule.onNodeWithText("Reps").assertExists()
    }

}
