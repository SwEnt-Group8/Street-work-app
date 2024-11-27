package com.android.streetworkapp.ui.event

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.progression.ScoreIncrease
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.ui.profile.DisplayUserPicture
import com.android.streetworkapp.ui.progress.updateAndDisplayPoints
import com.android.streetworkapp.ui.theme.ColorPalette
import com.android.streetworkapp.utils.toFormattedString
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

// Mutable dashboard state
private val uiState: MutableStateFlow<DashboardState> = MutableStateFlow(DashboardState.Details)

/**
 * This screen displays an overview of a selected event, including description location, location
 * and number of participants. A map is displayed at the bottom of the screen to show the location
 * of the event. The user can also choose to join the event.
 *
 * @param eventViewModel The event to display.
 * @param parkViewModel The park where the event takes place.
 * @param paddingValues The padding values used for the scaffold
 */
@Composable
fun EventOverviewScreen(
    eventViewModel: EventViewModel,
    parkViewModel: ParkViewModel,
    userViewModel: UserViewModel,
    navigationActions: NavigationActions,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {

  val event = eventViewModel.currentEvent.collectAsState()

  Box(modifier = Modifier.fillMaxSize().padding(paddingValues).testTag("eventOverviewScreen")) {
    Column(modifier = Modifier.fillMaxHeight().testTag("eventContent")) {
      event.value?.let { EventDetails(it, userViewModel) }

      HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

      event.value?.let { EventDashboard(it, userViewModel) }

      event.value?.let { EventMap(it, parkViewModel, navigationActions) }
    }
  }
}

/**
 * Displays the overview of the event, including the owner, date and number of participants.
 *
 * @param event The event to display.
 */
@Composable
fun EventDetails(event: Event, userViewModel: UserViewModel) {
  userViewModel.getUserByUid(event.owner)
  val user = userViewModel.user.collectAsState().value

  Column {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
      Text(
          event.title,
          modifier = Modifier.testTag("eventTitle"),
          fontSize = 24.sp,
          style = TextStyle(textDecoration = TextDecoration.Underline))
    }

    Row(
        modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
          DisplayUserPicture(user, 48.dp, "eventOwnerPicture")
          Text("Organized by: ${user?.username}", modifier = Modifier.testTag("eventOwner"))
        }

    Row(
        modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
          Icon(
              Icons.Filled.DateRange,
              contentDescription = "Date",
              modifier = Modifier.testTag("dateIcon"))
          Text(event.date.toFormattedString(), modifier = Modifier.padding(8.dp).testTag("date"))
          Icon(
              Icons.TwoTone.Face,
              contentDescription = "participants",
              modifier = Modifier.padding(start = 64.dp).testTag("participantsIcon"))
          Text(
              "Participants: ${event.listParticipants.size}/${event.maxParticipants}",
              modifier = Modifier.padding(8.dp).testTag("participants"))
        }
  }
}

/**
 * Displays a map showing the location of the event.
 *
 * @param event The event to display.
 * @param parkViewModel The park where the event takes place.
 */
@Composable
fun EventMap(event: Event, parkViewModel: ParkViewModel, navigationActions: NavigationActions) {
  parkViewModel.getParkByPid(event.parkId)
  val park = parkViewModel.park.collectAsState().value
  val parkLatLng = park?.location?.let { LatLng(it.lat, it.lon) }

  // Create a CameraPositionState to control the camera position
  val cameraPositionState = rememberCameraPositionState {
    position = parkLatLng?.let { CameraPosition.Builder().target(it).zoom(15f).build() }!!
  }

  Row(
      modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
      verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Outlined.LocationOn,
            contentDescription = "location",
            modifier = Modifier.padding(horizontal = 8.dp).testTag("locationIcon"))
        Text("at ${park?.name}", modifier = Modifier.testTag("location"))
      }

  GoogleMap(
      modifier = Modifier.testTag("googleMap").fillMaxSize(),
      cameraPositionState = cameraPositionState) {
        Marker(
            contentDescription = "Event location",
            state = MarkerState(position = parkLatLng ?: LatLng(0.0, 0.0)),
            onClick = {
              park?.location?.let {
                parkViewModel.getOrCreateParkByLocation(it)
                parkViewModel.setParkLocation(it)
                navigationActions.navigateTo(Screen.PARK_OVERVIEW)
              }
              true
            })
      }
}

/**
 * mutable EventDashboard displaying the details of an event as well as its participants.
 *
 * @param event The event to display
 */
@Composable
fun EventDashboard(event: Event, userViewModel: UserViewModel) {
  // Main container column with a fixed height and scrollable inner content
  Column(
      modifier =
          Modifier.fillMaxWidth()
              .height(220.dp) // Restrict the total height of the dashboard
              .background(ColorPalette.INTERACTION_COLOR_LIGHT) // Example background color
              .testTag("eventDashboard")) {

        // Top bar
        DashBoardBar()
        when (uiState.collectAsState().value) {
          DashboardState.Details -> {
            // Scrollable content area
            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(10.dp)
                        .testTag("dashboardContent")
                        .verticalScroll(rememberScrollState()) // Make the content scrollable
                ) {
                  Text(
                      text = event.description,
                      modifier = Modifier.padding(10.dp).testTag("eventDescription"))
                }
          }
          DashboardState.Participants -> {
            userViewModel.getUsersByUids(event.listParticipants)
            ParticipantsList(userViewModel.userList.collectAsState().value)
          }
        }
      }
}

/**
 * Navigation bar for the event dashboard, letting a user switch between the details or the
 * participants of the event
 */
@Composable
fun DashBoardBar() {
  NavigationBar(
      modifier = Modifier.testTag("dashboard").fillMaxWidth().height(56.dp),
      containerColor = ColorPalette.PRINCIPLE_BACKGROUND_COLOR) {
        val state = uiState.collectAsState().value

        NavigationBarItem(
            modifier = Modifier.testTag("detailsTab"),
            icon = { Text("Details") },
            selected = state == DashboardState.Details,
            onClick = { uiState.value = DashboardState.Details },
            colors = ColorPalette.NAVIGATION_BAR_ITEM_COLORS)

        NavigationBarItem(
            modifier = Modifier.testTag("participantsTab"),
            icon = { Text("Participants") },
            selected = state == DashboardState.Participants,
            onClick = { uiState.value = DashboardState.Participants },
            colors = ColorPalette.NAVIGATION_BAR_ITEM_COLORS)
      }
}

@Composable
fun JoinEventButton(
    event: Event,
    eventViewModel: EventViewModel,
    userViewModel: UserViewModel,
    user: User,
    navigationActions: NavigationActions,
    scope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState? = null,
) {
  Button(
      onClick = {
        if (snackbarHostState != null) {
          updateAndDisplayPoints(
              userViewModel,
              navigationActions,
              ScoreIncrease.JOIN_EVENT.points,
              scope,
              snackbarHostState)
        }

        eventViewModel.addParticipantToEvent(event.eid, user.uid)
        navigationActions.goBack()
      },
      modifier = Modifier.testTag("joinEventButton"),
      enabled = event.participants < event.maxParticipants,
      colors = ColorPalette.BUTTON_COLOR) {
        Text("Join this event")
      }
}

@Composable
fun LeaveEventButton(
    event: Event,
    eventViewModel: EventViewModel,
    user: User,
    navigationActions: NavigationActions
) {
  val context = LocalContext.current

  Button(
      onClick = {
        Toast.makeText(context, "You have left this event", Toast.LENGTH_LONG).show()
        eventViewModel.removeParticipantFromEvent(event.eid, user.uid)
        navigationActions.goBack()
      },
      enabled = event.owner != user.uid,
      modifier = Modifier.testTag("leaveEventButton"),
      colors = ColorPalette.BUTTON_COLOR.copy(containerColor = Color.Red)) {
        Text("Leave this event")
      }
}

/**
 * This function displays the friends list.
 *
 * @param participants - The list of friends to display.
 */
@Composable
fun ParticipantsList(participants: List<User?>) {
  LazyColumn(modifier = Modifier.fillMaxSize().testTag("friendList")) {
    items(participants) { friend ->
      if (friend != null) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically) {
              // Friend's avatar
              DisplayUserPicture(friend, 48.dp, "participantProfilePicture")

              // Friend's info (name, score, status)
              Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.username,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp).testTag("participantName"))
              }
            }
      }
    }
  }
}

/** Represents the different states of the event dashboard */
sealed class DashboardState {
  data object Details : DashboardState()

  data object Participants : DashboardState()
}
