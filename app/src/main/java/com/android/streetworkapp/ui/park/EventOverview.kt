package com.android.streetworkapp.ui.park

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.theme.Snowflake
import com.android.streetworkapp.utils.toFormattedString
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow

// Mutable dashboard state
private val uiState: MutableStateFlow<OverviewUiState> = MutableStateFlow(OverviewUiState.Details)

/**
 * This screen displays an overview of a selcted event, including description location, location and
 * number of participants. A map is displayed at the bottom of the screen to show the location of
 * the event. The user can also choose to join the event.
 *
 * @param navigationActions The navigation actions.
 * @param event The event to display.
 * @param park The park where the event takes place.
 */
@Composable
fun EventOverviewScreen(navigationActions: NavigationActions, event: Event, park: Park) {

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("eventOverviewScreen"),
      topBar = { EventTopBar(title = event.title, navigationActions = navigationActions) },
      bottomBar = {
        EventBottomBar(participants = event.participants, maxParticipants = event.maxParticipants)
      }) { padding ->
        Box(modifier = Modifier.fillMaxSize().testTag("eventContent")) {
          Column(modifier = Modifier.padding(padding).fillMaxHeight()) {
            Row {
              EventDetails(event)

              Spacer(modifier = Modifier.width(16.dp))

              Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center) {
                    Image(
                        // If no image is provided, use the default park image generated by Dall-E
                        painter =
                            painterResource(
                                id =
                                    R.drawable.park_default), // TODO: fetch the image from Firebase
                        // storage
                        contentDescription = "Park Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(150.dp).testTag("eventImage"))
                  }
            }
            Spacer(modifier = Modifier.height(16.dp))

            EventDashboard(event)

            Spacer(modifier = Modifier.height(16.dp))

            EventMap(park)
          }
        }
      }
}

/**
 * Top bar for the event screen, displaying the title of the event and a back button.
 *
 * @param title The title of the event.
 * @param navigationActions The navigation actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTopBar(title: String, navigationActions: NavigationActions) {
  CenterAlignedTopAppBar(
      colors =
          TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.testTag("EventTopBar"),
      title = {
        Text(modifier = Modifier.testTag("eventTitle"), text = title, fontWeight = FontWeight.Bold)
      },
      navigationIcon = {
        IconButton(
            modifier = Modifier.testTag("goBackButton"), onClick = { navigationActions.goBack() }) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
      })
}

/**
 * Bottom bar for the event screen, displaying a button to join the event.
 *
 * @param participants The number of participants that have joined the event.
 * @param maxParticipants The maximum number of participants allowed in the event.
 */
@Composable
fun EventBottomBar(participants: Int, maxParticipants: Int) {
  BottomAppBar(containerColor = Color.Transparent, modifier = Modifier.testTag("eventBottomBar")) {
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
      Button(
          onClick = {},
          modifier = Modifier.testTag("joinEventButton"),
          enabled = participants < maxParticipants) {
            Text("Join this event", modifier = Modifier.testTag("joinEventButtonText"))
          }
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
    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
      Icon(
          Icons.Outlined.AccountCircle,
          contentDescription = "User",
          modifier = Modifier.padding(horizontal = 8.dp).testTag("ownerIcon"))
      Text("Organized by: ${event.owner}", modifier = Modifier.testTag("eventOwner"))
    }

    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
      Icon(
          Icons.Filled.DateRange,
          contentDescription = "Date",
          modifier = Modifier.padding(horizontal = 8.dp).testTag("dateIcon"))
      Text(event.date.toFormattedString(), modifier = Modifier.testTag("date"))
    }

    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
      Icon(
          Icons.TwoTone.Face,
          contentDescription = "participants",
          modifier = Modifier.padding(horizontal = 8.dp).testTag("participantsIcon"))
      Text(
          "Participants: ${event.participants}/${event.maxParticipants}",
          modifier = Modifier.testTag("participants"))
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

  Row(modifier = Modifier.padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
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
  Scaffold(
      topBar = { DashBoardBar() },
      modifier = Modifier.height(220.dp).testTag("evenDashboard"),
      containerColor = Snowflake) { padding ->
        Column(
            modifier =
                Modifier.padding(20.dp)
                    .testTag("dashBoardContent")
                    .wrapContentHeight()
                    .heightIn(max = 220.dp) // Set the maximum height
                    .verticalScroll(rememberScrollState())) {
              when (uiState.collectAsState().value) {
                OverviewUiState.Details ->
                    Text(
                        event.description,
                        modifier = Modifier.padding(padding).testTag("eventDescription"))
                OverviewUiState.Participants ->
                    Text(
                        "show participants",
                        modifier = Modifier.padding(padding).testTag("participantsList"))
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
      modifier = Modifier.testTag("dashBoard").fillMaxWidth().height(56.dp),
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

/** Represents the different states of the event dashboard */
sealed class OverviewUiState {
  data object Details : OverviewUiState()

  data object Participants : OverviewUiState()
}
