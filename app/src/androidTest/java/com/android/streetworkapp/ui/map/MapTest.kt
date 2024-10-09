package com.android.streetworkapp.ui.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun displayAllComponents() {

    composeTestRule.setContent { MapScreen() }

    composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("googleMap").assertIsDisplayed()
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule.onNodeWithTag("parkIcon").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileIcon").assertIsDisplayed()
  }
}
