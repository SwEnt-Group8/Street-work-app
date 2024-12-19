package com.android.streetworkapp.ui.navigation

import android.content.Context
import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.sample.R
import com.android.streetworkapp.ui.utils.CustomDialog
import com.android.streetworkapp.ui.utils.DialogType

/**
 * This class manages the InfoDialogs that are displayed when the user clicks on the info action. It
 * is responsible for displaying the correct dialog for the current screen.
 *
 * @param showDialog - The display state of the InfoDialogs.
 * @param screenName - The name of the current screen.
 * @param topAppBarManager - The TopAppBarManager instance (used to set the action callback).
 */
class InfoDialogManager(
    private val showDialog: MutableState<Boolean>,
    private val screenName: MutableState<String?>,
    private val topAppBarManager: TopAppBarManager?
) {
  private val screens = getScreens()

  // Mapping Screen to InfoDialog
  private val _infoDialogs: MutableMap<String, InfoDialog> =
      mutableMapOf(
          "default" to defaultInfoDialog(),
          screens.MAP to InfoDialog("Map", R.string.MapInfoTitle, R.string.MapInfoContent),
          screens.PROFILE to
              InfoDialog("Profile", R.string.ProfileInfoTitle, R.string.ProfileInfoContent),
          screens.ADD_FRIEND to
              InfoDialog("AddFriend", R.string.AddFriendInfoTitle, R.string.AddFriendInfoContent),
          screens.PARK_OVERVIEW to
              InfoDialog(
                  "ParkOverview", R.string.ParkOverviewInfoTitle, R.string.ParkOverviewInfoContent),
          screens.ADD_EVENT to
              InfoDialog("AddEvent", R.string.AddEventInfoTitle, R.string.AddEventInfoContent),
          screens.EDIT_EVENT to
              InfoDialog("EditEvent", R.string.EditEventInfoTitle, R.string.EditEventInfoContent),
          screens.EVENT_OVERVIEW to
              InfoDialog(
                  "EventOverview",
                  R.string.EventOverviewInfoTitle,
                  R.string.EventOverviewInfoContent),
          screens.PROGRESSION to
              InfoDialog(
                  "Progression", R.string.ProgressionInfoTitle, R.string.ProgressionInfoContent),
          screens.TRAIN_HUB to
              InfoDialog("TrainHub", R.string.TrainHubInfoTitle, R.string.TrainHubInfoContent)

          // Auth not done because unnecessary
          // Add more if needed
          )

  val infoDialogs: MutableMap<String, InfoDialog>
    get() = _infoDialogs

  /** Returns the default InfoDialog (for screens that are not supported). */
  fun defaultInfoDialog(): InfoDialog {
    return InfoDialog("Default", R.string.DefaultInfoTitle, R.string.DefaultInfoContent)
  }

  /**
   * This function sets up the INFO action callback for the dialog. Cannot be done in another way
   * because it needs the topAppBarManager to be instantiated.
   */
  fun setUp() {
    Log.d("InfoDialog", "Setting up actions callbacks with Manager $topAppBarManager")
    topAppBarManager!!.setActionCallback(TopAppBarManager.TopAppBarAction.INFO) {
      Log.d("InfoDialog", "Info callback start : show = ${showDialog.value}")
      showDialog.value = true
      Log.d("InfoDialog", "Info callback end : show = ${showDialog.value}")
    }
  }

  /**
   * This function displays the info dialog for the current screen. If the screen is not supported,
   * a default "non-supported" version is displayed.
   */
  @Composable
  fun Display(context: Context) {
    Log.d("InfoDialog", "Displaying info dialog ${showDialog.value} for screen ${screenName.value}")
    val func = _infoDialogs[screenName.value]
    if (func != null) {
      Log.d("InfoDialog", "Displaying info dialog : ${func.title}")
      func.DisplayInfoDialog(showDialog, context)
    } else {
      Log.d("InfoDialog", "Displaying default info dialog : ${defaultInfoDialog().title}")
      defaultInfoDialog().DisplayInfoDialog(showDialog, context)
    }
  }
}

class InfoDialog(val tag: String, val title: Int, val content: Int) {

  /**
   * This function displays the info dialog, which is a specific Type of CustomDialog.
   *
   * @param showDialog - The display state of the dialog.
   */
  @Composable
  fun DisplayInfoDialog(showDialog: MutableState<Boolean>, context: Context) {
    CustomDialog(
        showDialog,
        dialogType = DialogType.INFO,
        "${tag}Info",
        context.getString(title),
        Content = { DisplayInfoContent(context.getString(content)) })
  }

  /** Function wrapper to allow more customization later on */
  @Composable
  fun DisplayInfoContent(content: String) {
    Text(content, modifier = Modifier.testTag("${tag}InfoDialogContent"))
  }
}
