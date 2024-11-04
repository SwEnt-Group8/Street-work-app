package com.android.streetworkapp.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag

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
      modifier = Modifier.testTag(BottomNavigationMenuType.DEFAULT.getTopLevelTestTag()),
      containerColor = Color.Gray) {
        tabList.forEach { topLevelDestination ->
          NavigationBarItem(
              modifier = Modifier.testTag("bottomNavigationItem"),
              icon = {
                Icon(topLevelDestination.icon, contentDescription = topLevelDestination.textId)
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
  BottomAppBar(
      containerColor = Color.Transparent,
      modifier = Modifier.testTag(BottomNavigationMenuType.EVENT_OVERVIEW.getTopLevelTestTag())) {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
          Button(
              onClick = {},
              modifier = Modifier.testTag("joinEventButton"),
              enabled = participants < maxParticipants) {
                Text("Join this event", modifier = Modifier.testTag("joinEventButtonText"))
              }
        }
      }
}
