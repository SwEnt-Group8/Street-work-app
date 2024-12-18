package com.android.streetworkapp.ui.train

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class TrainHelperTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun trainCoachDialog_rendersAndInteracts() {
    composeTestRule.setContent { TrainCoachDialog(onRoleSelected = {}, onDismiss = {}) }

    composeTestRule.onNodeWithTag("DialogTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RoleText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("RoleSwitch").performClick()
    composeTestRule.onNodeWithTag("ConfirmButton").performClick()
  }
}
