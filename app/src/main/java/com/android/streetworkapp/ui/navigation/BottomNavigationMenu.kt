package com.android.streetworkapp.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.event.EditEventButton
import com.android.streetworkapp.ui.event.JoinEventButton
import com.android.streetworkapp.ui.event.LeaveEventButton
import com.android.streetworkapp.ui.event.StatusButton
import com.android.streetworkapp.ui.theme.ColorPalette
import kotlinx.coroutines.CoroutineScope

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
              modifier = Modifier.testTag("bottomNavigationItem${topLevelDestination.route}"),
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
                          Color.Black) // same here as in the trophy xml, will need to modify this
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
 * @param eventViewModel The event view model.
 * @param userViewModel The user view model.
 * @param navigationActions The navigation actions.
 */
@Composable
fun EventBottomBar(
    eventViewModel: EventViewModel,
    userViewModel: UserViewModel,
    parkViewModel: ParkViewModel,
    navigationActions: NavigationActions,
    scope: CoroutineScope = rememberCoroutineScope(),
    snackBarHostState: SnackbarHostState? = null
) {
  val event = eventViewModel.currentEvent.collectAsState()
  val user = userViewModel.currentUser.collectAsState()

  BottomAppBar(
      containerColor = Color.Transparent,
      modifier = Modifier.testTag(BottomNavigationMenuType.EVENT_OVERVIEW.getTopLevelTestTag())) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(16.dp)) {
              user.value?.let { user ->
                event.value?.let { event ->
                  if (user.uid == event.owner) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()) {
                          EditEventButton(event, eventViewModel, navigationActions)
                          StatusButton(event, eventViewModel, parkViewModel, navigationActions)
                        }
                  } else if (event.listParticipants.contains(user.uid)) {
                    LeaveEventButton(event, eventViewModel, user, navigationActions)
                  } else {
                    JoinEventButton(
                        event,
                        eventViewModel,
                        userViewModel,
                        user,
                        navigationActions,
                        scope,
                        snackBarHostState)
                  }
                }
              }
            }
      }
}
