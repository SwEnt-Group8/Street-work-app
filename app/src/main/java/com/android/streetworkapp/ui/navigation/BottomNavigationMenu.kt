package com.android.streetworkapp.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
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
                      painter = painterResource(id = R.drawable.trophy_24px),
                      contentDescription = topLevelDestination.textId,
                      modifier =
                          Modifier.size(
                              24.dp) // default icon size for material, keeping the same to match
                      )
                }
                topLevelDestination.icon?.let {
                  Icon(topLevelDestination.icon, contentDescription = topLevelDestination.textId)
                }
              },
              selected = false,
              onClick = { onTabSelect(topLevelDestination) })
        }
      }
}
