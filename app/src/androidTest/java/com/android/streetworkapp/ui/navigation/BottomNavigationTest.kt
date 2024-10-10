package com.android.streetworkapp.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BottomNavigationTest {

  @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
  @Composable
  fun BottomNavigationTest() {
    Scaffold(
        bottomBar = {
          BottomNavigationMenu(onTabSelect = {}, tabList = LIST_TOP_LEVEL_DESTINATION)
        }) {
          Text("test")
        }
  }

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent { BottomNavigationTest() }
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertIsDisplayed()
    composeTestRule
        .onAllNodesWithTag("bottomNavigationItem")
        .assertCountEquals(LIST_TOP_LEVEL_DESTINATION.size)

    for (i in LIST_TOP_LEVEL_DESTINATION.indices) {
      composeTestRule.onAllNodesWithTag("bottomNavigationItem")[i].assertIsDisplayed()
    }
  }
}
