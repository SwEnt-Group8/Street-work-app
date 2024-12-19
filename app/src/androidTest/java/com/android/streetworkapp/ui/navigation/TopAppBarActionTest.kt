package com.android.streetworkapp.ui.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class TopAppBarActionTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun isTopAppBarManagerActionCorrectlyDefined() {
    val settingsAction = TopAppBarManager.TopAppBarAction.SETTINGS

    val topAppBarManager = TopAppBarManager("any title", actions = listOf(settingsAction))

    // Using the manager correctly to set the actions should assign the actions inside the manager
    // Also verifies that the getter works
    assert(topAppBarManager.getTopAppBarActions() == listOf(settingsAction))
  }

  @Test
  fun isActionSetterCorrect() {
    val settingsAction = TopAppBarManager.TopAppBarAction.SETTINGS

    val topAppBarManager = TopAppBarManager("any title")

    topAppBarManager.setTopAppBarActions(listOf(settingsAction))
    assert(topAppBarManager.getTopAppBarActions() == listOf(settingsAction))
  }

  @Test
  fun isActionDisplayed() {
    // When creating new actions, make sure to test them also !
    val settingsAction = TopAppBarManager.TopAppBarAction.SETTINGS

    val topAppBarManager = TopAppBarManager("any title", actions = listOf(settingsAction))

    composeTestRule.setContent { TopAppBarWrapper(NavigationActions(mockk()), topAppBarManager) }

    // Verify that the action (IconButton) is displayed
    composeTestRule
        .onNodeWithTag(settingsAction.testTag) // No children (Icon not included).
        .assertIsDisplayed()
        .assertHasClickAction()

    // Verify that the Icon of the IconButton is displayed :
    composeTestRule
        .onNodeWithContentDescription(settingsAction.contentDescription)
        .assertExists()
        .assertIsDisplayed()
  }

  @Test
  fun areAllActionsDisplayed() {
    val actions = TopAppBarManager.TopAppBarAction.entries.toList()

    val topAppBarManager = TopAppBarManager("any title", actions = actions.toList())

    composeTestRule.setContent { TopAppBarWrapper(NavigationActions(mockk()), topAppBarManager) }

    actions.forEach { action ->
      // Verify that the action (IconButton) is displayed
      composeTestRule.onNodeWithTag(action.testTag).assertIsDisplayed().assertHasClickAction()

      // Verify that the Icon of the IconButton is displayed :
      composeTestRule
          .onNodeWithContentDescription(action.contentDescription)
          .assertExists()
          .assertIsDisplayed()
    }
  }

  @Test
  fun isActionCallBackWorkingCorrectly() {
    val settingsAction = TopAppBarManager.TopAppBarAction.SETTINGS

    val topAppBarManager = TopAppBarManager("any title", actions = listOf(settingsAction))

    val logicValue = mutableStateOf(false)

    topAppBarManager.setActionCallback(settingsAction) { logicValue.value = true }

    composeTestRule.setContent { TopAppBarWrapper(NavigationActions(mockk()), topAppBarManager) }

    // Verify that clicking the action triggers the callback
    assert(!logicValue.value)
    composeTestRule.onNodeWithTag(settingsAction.testTag).performClick()
    composeTestRule.waitForIdle()
    assert(logicValue.value)
  }

  @Test
  fun isSetAllActionCallBacksWorkingCorrectly() {
    val settingsAction = TopAppBarManager.TopAppBarAction.SETTINGS

    val topAppBarManager = TopAppBarManager("any title", actions = listOf(settingsAction))

    val logicValue = mutableStateOf(false)

    topAppBarManager.setAllActionCallbacks(mapOf(settingsAction to { logicValue.value = true }))

    composeTestRule.setContent { TopAppBarWrapper(NavigationActions(mockk()), topAppBarManager) }

    // Verify that clicking the action triggers the callback
    assert(!logicValue.value)
    composeTestRule.onNodeWithTag(settingsAction.testTag).performClick()
    composeTestRule.waitForIdle()
    assert(logicValue.value)
  }

  @Test
  fun areDefaultCallBackValueWorking() {
    val actions = TopAppBarManager.TopAppBarAction.entries.toList()

    val topAppBarManager = TopAppBarManager("any title", actions = actions)

    val logicValue = mutableStateOf(false)
    topAppBarManager.setAllActionCallbacks(actions.associateWith { { logicValue.value = true } })
    topAppBarManager.setAllActionCallbacks() // This should reset everything to () -> {}

    composeTestRule.setContent { TopAppBarWrapper(NavigationActions(mockk()), topAppBarManager) }

    // Verify that the default value is working
    // Warning : This test might break when new actions are added (resetting the value)
    for (action in actions) {
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithTag(action.testTag).performClick()
      composeTestRule.waitForIdle()
      assert(!logicValue.value) // Value should not have changed
      logicValue.value = false // Reset the value
    }
  }

  @Test
  fun isOnActionClickWorking() {
    val settingsAction = TopAppBarManager.TopAppBarAction.SETTINGS

    val topAppBarManager = TopAppBarManager("any title", actions = listOf(settingsAction))

    val logicValue = mutableStateOf(false)

    topAppBarManager.setActionCallback(settingsAction) { logicValue.value = true }

    topAppBarManager.onActionClick(settingsAction)

    assert(logicValue.value)
  }

  @Test
  fun isSearchBarDisplayed() {

    val topAppBarManager = TopAppBarManager("any title", hasSearchBar = true)

    composeTestRule.setContent { TopAppBarWrapper(NavigationActions(mockk()), topAppBarManager) }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("searchBar").assertIsDisplayed()
  }
}
