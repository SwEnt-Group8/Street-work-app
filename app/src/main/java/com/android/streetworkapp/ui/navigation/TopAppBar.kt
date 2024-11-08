package com.android.streetworkapp.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.streetworkapp.ui.theme.ColorPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWrapper(navigationActions: NavigationActions, topAppBarManager: TopAppBarManager?) {
  topAppBarManager?.let {
    TopAppBar(
        modifier = Modifier.testTag("topAppBar"),
        title = {
          Text(modifier = Modifier.testTag("topAppBarTitle"), text = it.getTopAppBarTitle())
        },
        colors =
            TopAppBarDefaults.topAppBarColors(containerColor = ColorPalette.PRINCIPLE_BACKGROUND_COLOR),
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
