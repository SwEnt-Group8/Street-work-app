package com.android.streetworkapp.ui.navigation

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
  private val infoDialogs: MutableMap<String, InfoDialog> =
      mutableMapOf(
          screens.MAP to
              InfoDialog(
                  "MapInfo",
                  "How does the map work ?",
                  "This is the map, here you can see each park that is near you. \n\n You can navigate to see more. Click on a park to see its details, such as their events !"),
          screens.PROFILE to
              InfoDialog(
                  "ProfileInfo",
                  "What is the Profile ?",
                  "This is your profile page, you can see your friends and your score. \n\n You can also add friends using the 'Add friend' button and you can also access your settings by clicking the gear icon on the top right."),
          screens.ADD_FRIEND to
              InfoDialog(
                  "AddFriendInfo",
                  "How do friends work ?",
                  "This is where you can add a friend. \n\n To add, you need to have a friend nearby and activate bluetooth and location services. \n\n Once you are friends you will be able to do workouts with each other. \n\n Have fun together !"),
          screens.PARK_OVERVIEW to
              InfoDialog(
                  "ParkOverviewInfo",
                  "How does parks work ?",
                  "This is the park overview, here you can see the park's details and the events that are planned in it. \n\n You can create an event or join an already existing one. You can also rate the park !"),
          screens.ADD_EVENT to
              InfoDialog(
                  "AddEventInfo",
                  "How to create an event ?",
                  "This is your firsts steps towards creating an event ! \n\n You simply have to add a cool title, then, describe your event : What are you going to do there ? \n\n Finally, set the date and time and you are good to go !"),
          screens.EVENT_OVERVIEW to
              InfoDialog(
                  "EventOverviewInfo",
                  "What is the Event Overview ?",
                  "This is the event overview, here you can access all of the information that you need. \n\n You are free to join the event if you want to participate !"),
          screens.PROGRESSION to
              InfoDialog(
                  "ProgressionInfo",
                  "What is the Progression ?",
                  "This is the progression screen, here you can see your progression and achievements. \n\n There is your record for each exercise in Training, click on it to see more details !")

          // Auth not done because unnecessary
          // Add more if needed
          )

  private fun defaultInfoDialog(): InfoDialog {
    return InfoDialog(
        "DefaultInfo",
        "Screen not supported yet",
        "Information about this screen is not available. Please explore to learn more!")
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
  fun Display() {
    Log.d("InfoDialog", "Displaying info dialog ${showDialog.value} for screen ${screenName.value}")
    val func = infoDialogs[screenName.value]
    if (func != null) {
      Log.d("InfoDialog", "Displaying info dialog : ${func.title}")
      func.DisplayInfoDialog(showDialog)
    } else {
      Log.d("InfoDialog", "Displaying default info dialog : ${defaultInfoDialog().title}")
      defaultInfoDialog().DisplayInfoDialog(showDialog)
    }
  }
}

class InfoDialog(val tag: String, val title: String, val content: String) {

  /**
   * This function displays the info dialog, which is a specific Type of CustomDialog.
   *
   * @param showDialog - The display state of the dialog.
   */
  @Composable
  fun DisplayInfoDialog(showDialog: MutableState<Boolean>) {
    CustomDialog(
        showDialog,
        dialogType = DialogType.INFO,
        tag,
        title,
        Content = { DisplayInfoContent(content) })
  }

  // Allow more customization later on
  @Composable
  private fun DisplayInfoContent(content: String) {
    Text(content)
  }
}
