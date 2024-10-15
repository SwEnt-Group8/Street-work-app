package com.android.streetworkapp.ui.park

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.twotone.Face
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventList
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.model.parklocation.ParkLocation
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.utils.toFormattedString
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow

private val uiState: MutableStateFlow<OverviewUiState> = MutableStateFlow(OverviewUiState.Details)

/**
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventOverviewScreen(navigationActions: NavigationActions, event: Event, park: Park) {

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
            title = {
              Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.testTag("eventTitle"),
                    text = event.title,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline)
              }
            },
            navigationIcon = {
              IconButton(
                  modifier = Modifier.testTag("goBackButton"),
                  onClick = { navigationActions.goBack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                  }
            })
      },
      bottomBar = {
        BottomAppBar(containerColor = Color.Transparent) {
          Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {},
            ) {
              Text("Join an event")
            }
          }
        }
      }) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
          Column(modifier = Modifier.padding(padding).fillMaxHeight()) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                      Icons.Outlined.AccountCircle,
                      contentDescription = "User",
                      modifier = Modifier.padding(horizontal = 8.dp))
                  Text("Organized by: ${event.owner}", modifier = Modifier.testTag("eventOwner"))
                }

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                      Icons.Filled.DateRange,
                      contentDescription = "Date",
                      modifier = Modifier.padding(horizontal = 8.dp))
                  Text(event.date.toFormattedString(), modifier = Modifier.testTag("date"))
                }

            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                      Icons.TwoTone.Face,
                      contentDescription = "participants",
                      modifier = Modifier.padding(horizontal = 8.dp))
                  Text(
                      "Participants: ${event.participants}/${event.maxParticipants}",
                      modifier =
                          Modifier.testTag("participants"))
                }

            Spacer(modifier = Modifier.height(16.dp))

            EventDashboard(event)

            val parkLatLng = LatLng(park.location.lat, park.location.lon)

            // Create a CameraPositionState to control the camera position

            val cameraPositionState = rememberCameraPositionState {
              position = CameraPosition.Builder().target(parkLatLng).zoom(15f).build()
            }

            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                      Icons.Outlined.LocationOn,
                      contentDescription = "location",
                      modifier = Modifier.padding(horizontal = 8.dp))
                  Text("at ${park.name}", modifier = Modifier.testTag("location"))
                }

            GoogleMap(
                modifier = Modifier.testTag("googleMap").fillMaxWidth().height(300.dp),
                cameraPositionState = cameraPositionState) {
                  Marker(
                      contentDescription = "Marker",
                      state =
                          MarkerState(position = LatLng(parkLatLng.latitude, parkLatLng.longitude)))
                }
          }
        }
      }
}

@Composable
fun EventDashboard(event: Event) {
  Scaffold(
      topBar = { DashBoardBar() },
      modifier = Modifier.height(220.dp)) { padding ->
        Column(
            modifier =
                Modifier.padding(20.dp)
                    .wrapContentHeight()
                    .heightIn(max = 220.dp) // Set the maximum height
                    .verticalScroll(rememberScrollState())) {
              when (uiState.collectAsState().value) {
                OverviewUiState.Details ->
                    Text(event.description, modifier = Modifier.padding(padding))
                OverviewUiState.Participants ->
                    Text("show participants", modifier = Modifier.padding(padding))
              }
            }
      }
}

@Composable
fun DashBoardBar() {
  NavigationBar(
      modifier = Modifier.testTag("bottomNavigationMenu").fillMaxWidth().height(56.dp),
  ) {
    val state = uiState.collectAsState().value

    NavigationBarItem(
        modifier = Modifier.testTag("detailsTab"),
        icon = { Text("Details") },
        selected = state == OverviewUiState.Details,
        onClick = { uiState.value = OverviewUiState.Details })

    NavigationBarItem(
        modifier = Modifier.testTag("participantsTab"),
        icon = { Text("Participants") },
        selected = state == OverviewUiState.Participants,
        onClick = { uiState.value = OverviewUiState.Participants })
  }
}

sealed class OverviewUiState {
  data object Details : OverviewUiState()

  data object Participants : OverviewUiState()
}

@Preview
@Composable
fun PreviewEventOverviewScreen() {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)
  val eventList =
      EventList(
          events =
              listOf(
                  Event(
                      "1",
                      "Group workout",
                      "A fun group workout session to train new skills! \r\n\r\n" +
                              "Come and join the fun of training with other motivated street workers while progressing on your figures\r\n" +
                              "We accept all levels: newcomers welcome\r\n\r\n" +
                              "see https/street-work-app/thissitedoesnotexist for more details",
                      5,
                      10,
                      Timestamp.now(),
                      "Malick")))

  // Park with events
  val park =
      Park(
          pid = "1",
          name = "EPFL Esplanade",
          location = ParkLocation(46.519962, 6.633597, "park"),
          image = null,
          rating = 4.5f,
          nbrRating = 102,
          occupancy = 0.8f,
          events = eventList)
  EventOverviewScreen(
      navigationActions,
      park.events.events.first(),
      park)
}
