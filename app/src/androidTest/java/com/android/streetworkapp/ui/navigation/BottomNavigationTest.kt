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
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
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

  @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
  @Composable
  fun EmptyBottomNavigationTest() {
    Scaffold(bottomBar = { BottomNavigationMenu(onTabSelect = {}, tabList = listOf()) }) {
      Text("test")
    }
  }

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun printComposeHierarchy() {
    composeTestRule.setContent { BottomNavigationTest() }
    composeTestRule.onRoot().printToLog("BottomNavigationTest")
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent { BottomNavigationTest() }
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertExists().assertIsDisplayed()
    composeTestRule
        .onAllNodesWithTag("bottomNavigationItem")
        .assertCountEquals(LIST_TOP_LEVEL_DESTINATION.size)

    val navItems = composeTestRule.onAllNodesWithTag("bottomNavigationItem")

    for (i in LIST_TOP_LEVEL_DESTINATION.indices) {
      navItems[i].assertIsDisplayed()
    }
  }

  @Test
  fun displayNoComponents() {
    composeTestRule.setContent { EmptyBottomNavigationTest() }
    composeTestRule.onNodeWithTag("bottomNavigationMenu").assertExists().assertIsDisplayed()
    composeTestRule.onAllNodesWithTag("bottomNavigationItem").assertCountEquals(0)
    composeTestRule.onAllNodesWithTag("bottomNavIcon").assertCountEquals(0)
  }
}
