package com.android.streetworkapp.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.streetworkapp.ui.theme.ColorPalette

enum class BottomNavigationMenuType {
  NONE,
  DEFAULT,
  EVENT_OVERVIEW;

  fun getTopLevelTestTag(): String {
    return when (this) {
      NONE -> ""
      DEFAULT -> "bottomNavigationMenu"
      EVENT_OVERVIEW -> "eventBottomBar"
    }
  }
}

/**
 * Default Bottom navigation menu for the app
 *
 * @param onTabSelect callback to be called when an icon is selected
 * @param tabList list of top level destinations to be displayed
 */
@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>
) {

  NavigationBar(
      modifier =
          Modifier.height(65.dp).testTag(BottomNavigationMenuType.DEFAULT.getTopLevelTestTag()),
      containerColor = ColorPalette.PRINCIPLE_BACKGROUND_COLOR) {
        tabList.forEach { topLevelDestination ->
          NavigationBarItem(
              modifier = Modifier.testTag("bottomNavigationItem"),
              icon = {
                topLevelDestination.imagePainter?.let {
                  Image(
                      painter = painterResource(id = topLevelDestination.imagePainter),
                      contentDescription = topLevelDestination.textId,
                      modifier =
                          Modifier.size(
                              24.dp) // default icon size for material, keeping the same to match
                      )
                }
                topLevelDestination.icon?.let {
                  Icon(
                      topLevelDestination.icon,
                      contentDescription = topLevelDestination.textId,
                      tint =
                          Color
                              .Black) // same here as in the trophy xml, will need to modify this
                                      // once we setup light/dark mode
                }
              },
              selected = false,
              onClick = { onTabSelect(topLevelDestination) })
        }
      }
}

/**
 * Bottom bar for the event screen, displaying a button to join the event.
 *
 * @param participants The number of participants that have joined the event.
 * @param maxParticipants The maximum number of participants allowed in the event.
 */
@Composable
fun EventBottomBar(participants: Int, maxParticipants: Int) {
  val context = LocalContext.current
  BottomAppBar(
      containerColor = Color.Transparent,
      modifier = Modifier.testTag(BottomNavigationMenuType.EVENT_OVERVIEW.getTopLevelTestTag())) {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
          Button(
              onClick = {
                Toast.makeText(context, "not yet implemented", Toast.LENGTH_LONG).show()
              },
              modifier = Modifier.testTag("joinEventButton"),
              enabled = participants < maxParticipants,
              colors = ColorPalette.BUTTON_COLOR) {
                Text("Join this event", modifier = Modifier.testTag("joinEventButtonText"))
              }
        }
      }
}
