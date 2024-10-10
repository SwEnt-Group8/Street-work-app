package com.android.streetworkapp.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    tabList: List<TopLevelDestination>,
    selectedItem: String
) {
  val navController = rememberNavController()

  NavigationBar(modifier = Modifier.testTag("bottomNavigationMenu"), containerColor = Color.Gray) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    tabList.forEach { topLevelDestination ->
      NavigationBarItem(
          modifier = Modifier.testTag("bottomNavigationItem"),
          icon = {
            Icon(topLevelDestination.icon, contentDescription = topLevelDestination.textId)
          },
          label = { Text(topLevelDestination.textId) },
          selected =
              currentDestination?.hierarchy?.any { it.route == topLevelDestination.route } == true,
          onClick = { onTabSelect(topLevelDestination) })
    }
  }
}
