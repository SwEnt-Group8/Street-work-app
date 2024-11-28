package com.android.streetworkapp.ui.navigation

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

class InfoDialogsTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun isInfoDialogCorrectlyDisplayed() {
    val infoTag = "Tag"
    val title = "Title"
    val content = "Message"

    val dialog = InfoDialog(infoTag, title, content)
    val dialogTag = dialog.tag + "Info"
    val show = mutableStateOf(true)

    composeTestRule.setContent { dialog.DisplayInfoDialog(show) }

    // Verifying that everything is displayed correctly :
    composeTestRule.onNodeWithTag(dialogTag + "Dialog").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(dialogTag + "DialogTitle")
        .assertIsDisplayed()
        .assertTextEquals(title)
    composeTestRule
        .onNodeWithTag(dialogTag + "DialogContent")
        .assertIsDisplayed()
        .assertTextEquals(content)

    // Changing the state will hide the dialog :
    show.value = false
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag(dialogTag + "Dialog").assertIsNotDisplayed()
  }

  @Test
  fun isDisplayInfoContentCorrect() {
    val dialog = InfoDialog("Tag", "Title", "Content")

    composeTestRule.setContent { dialog.DisplayInfoContent(dialog.content) }
    composeTestRule
        .onNodeWithTag(dialog.tag + "InfoDialogContent")
        .assertIsDisplayed()
        .assertTextEquals(dialog.content)
  }

  @Test
  fun isManagerWorkingCorrectly() {
    val topAppBarManager =
        TopAppBarManager("any title", actions = listOf(TopAppBarManager.TopAppBarAction.INFO))

    val showDialog = mutableStateOf(false)
    val screenName: MutableState<String?> = mutableStateOf("WRONG_SCREEN_NAME")

    val infoDialogManager = InfoDialogManager(showDialog, screenName, topAppBarManager)

    infoDialogManager.setUp() // callback set show to true

    composeTestRule.setContent { infoDialogManager.Display() }
    assert(!showDialog.value) // should be false so far
    topAppBarManager.onActionClick(TopAppBarManager.TopAppBarAction.INFO)

    val infoDialogs = infoDialogManager.getInfoDialogs()

    // Verifying correct behavior when screen changing :

    val allScreens = getScreens()
    // No clean way of extracting the const vals from the Screen object :
    val screenList =
        listOf(
            "default",
            allScreens.AUTH,
            allScreens.MAP,
            allScreens.PROFILE,
            allScreens.ADD_FRIEND,
            allScreens.PARK_OVERVIEW,
            allScreens.ADD_EVENT,
            allScreens.EVENT_OVERVIEW,
            allScreens.PROGRESSION,
            "unsupported_screen_will_yield_default")

    Log.d("InfoDialogsTest", "[] Starting tests for screen list : $screenList")

    for (screen in screenList) {
      Log.d("InfoDialogsTest", "-Testing screen : $screen")
      screenName.value = screen // set the current screen as the map screen
      composeTestRule.waitForIdle()

      var dialog = infoDialogs[screen]
      Log.d("InfoDialogsTest", "Fetching dialog : : $dialog")
      if (dialog == null) dialog = infoDialogManager.defaultInfoDialog() // if not supported

      val screenTag = dialog.tag + "Info"
      val screenTestTag = screenTag + "Dialog"

      Log.d(
          "InfoDialogsTest",
          "Dialog for screen $screen : ${dialog.title} ; ${dialog.tag} \n ${dialog.content}")

      composeTestRule.onNodeWithTag(screenTestTag).assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(screenTestTag + "Title")
          .assertIsDisplayed()
          .assertTextEquals(dialog.title)
      Log.d(
          "InfoDialogsTest",
          "Verifying content with testTag : ${screenTestTag + "Content"} == ${dialog.tag + "InfoDialogContent"}")
      composeTestRule
          .onNodeWithTag(screenTestTag + "Content") // tag + "InfoDialogContent"
          .assertIsDisplayed()
          .assertTextEquals(dialog.content)
    }
  }
}
