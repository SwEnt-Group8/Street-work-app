package com.android.streetworkapp.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag

/**
 * Bottom navigation menu for the app
 *
 * @param onTabSelect callback to be called when an icon is selected
 * @param tabList list of top level destinations to be displayed
 */
@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>
) {

  NavigationBar(modifier = Modifier.testTag("bottomNavigationMenu"), containerColor = Color.Gray) {
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
