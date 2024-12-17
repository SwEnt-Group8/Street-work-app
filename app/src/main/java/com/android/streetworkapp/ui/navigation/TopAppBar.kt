package com.android.streetworkapp.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.streetworkapp.ui.map.MapSearchBar
import com.android.streetworkapp.ui.theme.ColorPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWrapper(
    navigationActions: NavigationActions,
    topAppBarManager: TopAppBarManager?,
    query: MutableState<String> = mutableStateOf("")
) {
  topAppBarManager?.let {
    TopAppBar(
        modifier = Modifier.testTag("topAppBar"),
        title = {
          Text(modifier = Modifier.testTag("topAppBarTitle"), text = it.getTopAppBarTitle())
        },
        actions = {
          if (topAppBarManager.hasSearchBar()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
              MapSearchBar(query)
            }
          }
          Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.padding(8.dp)) {
                topAppBarManager.getTopAppBarActions().forEach { action ->
                  IconButton(
                      onClick = { topAppBarManager.onActionClick(action) },
                      modifier = Modifier.testTag(action.testTag).size(32.dp)) {
                        Icon(
                            painterResource(action.icon),
                            contentDescription = action.contentDescription,
                            modifier = Modifier.size(32.dp).fillMaxSize(),
                        )
                      }
                }
              }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = ColorPalette.PRINCIPLE_BACKGROUND_COLOR),
        navigationIcon = {
          if (it.hasNavigationIcon()) {
            IconButton(
                onClick = { navigationActions.goBack() },
                modifier = Modifier.testTag("goBackButtonTopAppBar")) {
                  Icon(it.getNavigationIcon(), contentDescription = "Back Icon")
                }
          }
        })
  }
}
