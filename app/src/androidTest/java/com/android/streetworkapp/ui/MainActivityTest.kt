package com.android.streetworkapp.ui

import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.streetworkapp.MainActivity
import com.android.streetworkapp.resources.C
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun mainScreenContainerIsNotNull() {
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag(C.Tag.main_screen_container).assertIsNotDisplayed()
  }
}
