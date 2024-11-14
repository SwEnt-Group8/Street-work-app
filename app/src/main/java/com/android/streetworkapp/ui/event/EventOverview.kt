package com.android.streetworkapp.ui.event

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.ui.theme.ColorPalette
import com.android.streetworkapp.utils.toFormattedString
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
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
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {

  val event = eventViewModel.currentEvent.collectAsState().value!!
  val park = parkViewModel.currentPark.collectAsState()

  Box(modifier = Modifier.fillMaxSize().padding(paddingValues).testTag("eventOverviewScreen")) {
    Column(modifier = Modifier.fillMaxHeight().testTag("eventContent")) {
      EventDetails(event)

      HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(8.dp))

      EventDashboard(event)

      park.value?.let { EventMap(it) }
    }
  }
}

/**
 * Displays the overview of the event, including the owner, date and number of participants.
 *
 * @param event The event to display.
 */
@Composable
fun EventDetails(event: Event) {
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
          Icon(
              Icons.Outlined.AccountCircle,
              contentDescription = "User",
              modifier = Modifier.padding(horizontal = 8.dp).testTag("ownerIcon"))
          Text("Organized by: ${event.owner}", modifier = Modifier.testTag("eventOwner"))
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
              "Participants: ${event.participants}/${event.maxParticipants}",
              modifier = Modifier.padding(8.dp).testTag("participants"))
        }
  }
}

/**
 * Displays a map showing the location of the event.
 *
 * @param park The park where the event takes place.
 */
@Composable
fun EventMap(park: Park) {
  val parkLatLng = LatLng(park.location.lat, park.location.lon)

  // Create a CameraPositionState to control the camera position
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.Builder().target(parkLatLng).zoom(15f).build()
  }

  Row(
      modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
      verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Outlined.LocationOn,
            contentDescription = "location",
            modifier = Modifier.padding(horizontal = 8.dp).testTag("locationIcon"))
        Text("at ${park.name}", modifier = Modifier.testTag("location"))
      }

  GoogleMap(
      modifier = Modifier.testTag("googleMap").fillMaxSize(),
      cameraPositionState = cameraPositionState) {
        Marker(
            contentDescription = "Marker",
            state = MarkerState(position = LatLng(parkLatLng.latitude, parkLatLng.longitude)))
      }
}

/**
 * mutable EventDashboard displaying the details of an event as well as its participants.
 *
 * @param event The event to display
 */
@Composable
fun EventDashboard(event: Event) {
  // Main container column with a fixed height and scrollable inner content
  Column(
      modifier =
          Modifier.fillMaxWidth()
              .height(220.dp) // Restrict the total height of the dashboard
              .background(ColorPalette.INTERACTION_COLOR_LIGHT) // Example background color
              .testTag("eventDashboard")) {

        // Top bar
        DashBoardBar()

        // Scrollable content area
        Column(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(10.dp)
                    .testTag("dashboardContent")
                    .verticalScroll(rememberScrollState()) // Make the content scrollable
            ) {
              when (uiState.collectAsState().value) {
                DashboardState.Details -> {
                  Text(
                      text = event.description,
                      modifier = Modifier.padding(10.dp).testTag("eventDescription"))
                }
                DashboardState.Participants -> {
                  Text(
                      text = "Show participants",
                      modifier = Modifier.padding(10.dp).testTag("participantsList"))
                }
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

/** Represents the different states of the event dashboard */
sealed class DashboardState {
  data object Details : DashboardState()

  data object Participants : DashboardState()
}
