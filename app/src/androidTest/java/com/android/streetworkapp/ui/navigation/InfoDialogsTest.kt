package com.android.streetworkapp.ui.navigation

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class InfoDialogsTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var topAppBarManager: TopAppBarManager
  private lateinit var showDialog: MutableState<Boolean>
  private lateinit var screenName: MutableState<String?>
  private var context: Context? = null
  private lateinit var infoDialogManager: InfoDialogManager

  @Before
  fun setUp() {
    topAppBarManager =
        TopAppBarManager("any title", actions = listOf(TopAppBarManager.TopAppBarAction.INFO))
    showDialog = mutableStateOf(true)
    screenName = mutableStateOf("WRONG_SCREEN_NAME")
    infoDialogManager = InfoDialogManager(showDialog, screenName, topAppBarManager)
  }

  @Test
  fun isInfoDialogCorrectlyDisplayed() {
    val dialog = infoDialogManager.defaultInfoDialog()

    val dialogTag = dialog.tag + "Info"

    composeTestRule.setContent {
      context = LocalContext.current
      dialog.DisplayInfoDialog(showDialog, context!!)
    }

    // Verifying that everything is displayed correctly :
    composeTestRule.onNodeWithTag(dialogTag + "Dialog").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("${dialogTag}DialogTitle")
        .assertIsDisplayed()
        .assertTextEquals(context!!.getString(dialog.title))
    composeTestRule
        .onNodeWithTag("${dialogTag}DialogContent")
        .assertIsDisplayed()
        .assertTextEquals(context!!.getString(dialog.content))

    // Changing the state will hide the dialog :
    showDialog.value = false
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("${dialogTag}Dialog").assertIsNotDisplayed()
  }

  @Test
  fun isDisplayInfoContentCorrect() {
    val dialog = infoDialogManager.defaultInfoDialog()

    composeTestRule.setContent {
      context = LocalContext.current
      dialog.DisplayInfoContent(context!!.getString(dialog.content))
    }
    composeTestRule
        .onNodeWithTag("${dialog.tag}InfoDialogContent")
        .assertIsDisplayed()
        .assertTextEquals(context!!.getString(dialog.content))
  }

  @Test
  fun isManagerWorkingCorrectly() {
    showDialog.value = false // dialog should not be displayed

    infoDialogManager.setUp() // callback set show to true

    composeTestRule.setContent {
      context = LocalContext.current
      infoDialogManager.Display(context!!)
    }
    assert(!showDialog.value) // should be false so far
    topAppBarManager.onActionClick(TopAppBarManager.TopAppBarAction.INFO)

    val infoDialogs = infoDialogManager.infoDialogs

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

      val screenTag = "${dialog.tag}Info"
      val screenTestTag = screenTag + "Dialog"

      Log.d(
          "InfoDialogsTest",
          "Dialog for screen $screen : ${dialog.title} ; ${dialog.tag} \n ${dialog.content}")

      composeTestRule.onNodeWithTag(screenTestTag).assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(screenTestTag + "Title")
          .assertIsDisplayed()
          .assertTextEquals(context!!.getString(dialog.title))
      Log.d(
          "InfoDialogsTest",
          "Verifying content with testTag : ${screenTestTag}Content == ${dialog.tag}InfoDialogContent")
      composeTestRule
          .onNodeWithTag("${screenTestTag}Content") // tag + "InfoDialogContent"
          .assertIsDisplayed()
          .assertTextEquals(context!!.getString(dialog.content))
    }
  }
}
