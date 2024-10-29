package com.android.streetworkapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWrapper(navigationActions: NavigationActions, topAppBarManager: TopAppBarManager?) {
    topAppBarManager?.let {
        TopAppBar(
            modifier = Modifier.testTag("topAppBar"),
            title = { Text(modifier = Modifier.testTag("topAppBarTitle"), text = topAppBarManager.getTopAppBarTitle()) },
            colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            navigationIcon = {
                if (topAppBarManager.hasNavigationIcon()) {
                    IconButton(
                        onClick = { navigationActions.goBack() },
                        modifier = Modifier.testTag("goBackButton")
                    ) {
                        Icon(
                            topAppBarManager.getNavigationIcon(), contentDescription = "Back Icon"
                        )
                    }
                }
            })
    }
}
