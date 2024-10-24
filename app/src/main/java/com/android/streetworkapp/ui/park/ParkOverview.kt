package com.android.streetworkapp.ui.park

// Portions of this code were generated with the help of GitHub Copilot.

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.android.sample.R
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventList
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.Screen
import com.android.streetworkapp.utils.toFormattedString

/**
 * Display the overview of a park, including park details and a list of events.
 *
 * @param navigationActions navigation class
 * @param park The park data to display.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkOverview(navigationActions: NavigationActions, park: Park) {
  Scaffold(
      modifier = Modifier.testTag("ParkOverview"),
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("ParkOverviewTopBar"),
            title = {},
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface),
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("goBackButtonOverviewScreen")) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Arrow Back Icon")
                  }
            })
      }) { innerPadding ->
        ParkOverviewScreen(
            park,
            innerPadding,
            navigationActions) // we declare this so that it doesn't impact the tests in
        // PackOverviewScreen (exceptions wouldn't be caught as it's async and
        // tests would fail)
      }
}
/**
 * Display the overview of a park, including park details and a list of events.
 *
 * @param park The park data to display.
 */
@Composable
fun ParkOverviewScreen(
    park: Park,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    navigationActions: NavigationActions = NavigationActions(rememberNavController())
) {
  Box(modifier = Modifier.padding(innerPadding).fillMaxSize().testTag("parkOverviewScreen")) {
    Column {
      ImageTitle(image = null, title = park.name) // TODO: Fetch image from Firestore storage
      ParkDetails(park = park)
      EventItemList(eventList = EventList(emptyList())) // TODO: Fetch events from Firestore
    }
    FloatingActionButton(
        onClick = {
          navigationActions.navigateTo(Screen.ADD_EVENT)

          Log.d("ParkOverviewScreen", "Create event button clicked") // TODO: Handle button click
        },
        modifier =
            Modifier.align(Alignment.BottomCenter)
                .padding(40.dp)
                .size(width = 150.dp, height = 40.dp)
                .testTag("createEventButton"),
    ) {
      Text("Create an event")
    }
  }
}

/**
 * Display an image with a title overlay, if no image is provided, an AI generated default image is
 * displayed.
 *
 * @param image The image to display.
 * @param title The title to display.
 */
@Composable
fun ImageTitle(image: Painter?, title: String) {
  Box(modifier = Modifier.fillMaxWidth().height(220.dp).testTag("imageTitle")) {
    Image(
        // If no image is provided, use the default park image generated by Dall-E
        painter = image ?: painterResource(id = R.drawable.park_default),
        contentDescription = "Park Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth())
    Box(
        modifier =
            Modifier.fillMaxWidth()
                .height(220.dp)
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xCC000000)),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY)))
    Text(
        text = title,
        color = Color.White,
        fontSize = 24.sp,
        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp).testTag("title"))
  }
}

/**
 * Display the details of a park, including the park's rating and occupancy.
 *
 * @param park The park data to display.
 */
@Composable
fun ParkDetails(park: Park) {
  Column(modifier = Modifier.testTag("parkDetails")) {
    Text(
        text = "Details",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 6.dp, bottom = 2.dp))
    RatingComponent(rating = park.rating.toInt(), park.nbrRating) // Round the rating
    OccupancyBar(occupancy = (park.occupancy.toFloat() / park.capacity.toFloat()))
  }
}

/**
 * Display a star rating from 1 to 5 and the number of reviews.
 *
 * @param rating The rating to display from 1 to 5.
 * @param nbrReview The positive number of reviews.
 */
@Composable
fun RatingComponent(rating: Int, nbrReview: Int) {
  require(rating in 1..5) { "Rating must be between 1 and 5" }
  Row(
      modifier = Modifier.padding(start = 16.dp).testTag("ratingComponent"),
      verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) {
          Icon(
              imageVector = Icons.Default.Star,
              contentDescription = "Star",
              tint = if (i <= rating) Color(0xFF6650a4) else Color.Gray,
              modifier = Modifier.size(24.dp))
        }
        Text(
            text = "($nbrReview)",
            modifier = Modifier.padding(start = 8.dp).testTag("nbrReview"),
            fontSize = 16.sp,
            fontWeight = FontWeight.Light)
      }
}

/**
 * Display a progress bar showing the park's occupancy.
 *
 * @param occupancy The park's occupancy percentage from 0 to 1.
 */
@Composable
fun OccupancyBar(occupancy: Float) {
  require(occupancy in 0f..1f) { "Occupancy must be between 0 and 1" }
  Row(
      modifier = Modifier.padding(start = 16.dp, end = 16.dp).testTag("occupancyBar"),
      verticalAlignment = Alignment.CenterVertically) {
        LinearProgressIndicator(
            progress = { occupancy },
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "${(occupancy * 100).toInt()}% Occupancy",
            modifier = Modifier.padding(start = 8.dp).testTag("occupancyText"),
            fontSize = 16.sp,
            fontWeight = FontWeight.Light)
      }
}

/**
 * Display a list of events or a message if no there is no events.
 *
 * @param eventList The list of events to display.
 */
@Composable
fun EventItemList(eventList: EventList) {
  Column(modifier = Modifier.testTag("eventItemList")) {
    Text(
        text = "Events",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 6.dp, bottom = 2.dp))
    if (eventList.events.isEmpty()) {
      Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "No event is planned yet",
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            modifier =
                Modifier.align(Alignment.Center).padding(bottom = 40.dp).testTag("noEventText"))
      }
    } else {
      LazyColumn { items(eventList.events) { event -> EventItem(event = event) } }
    }
  }
}

/**
 * Display an event item, including the event title, date, and number of participants.
 *
 * @param event The event data to display.
 */
@Composable
fun EventItem(event: Event) {
  ListItem(
      modifier = Modifier.padding(0.dp).testTag("eventItem"),
      headlineContent = { Text(text = event.title) },
      supportingContent = {
        Text(
            "Participants ${event.participants}/${event.maxParticipants}",
            fontWeight = FontWeight.Light,
            modifier = Modifier.testTag("participantsText"))
      },
      overlineContent = {
        Text(
            text = event.date.toFormattedString(),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("dateText"))
      },
      leadingContent = {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile Icon",
            modifier = Modifier.size(56.dp).testTag("profileIcon"))
      },
      trailingContent = {
        Button(
            onClick = {
              Log.d("EventItem", "About event button clicked") // TODO: Handle button click
            },
            modifier = Modifier.size(width = 80.dp, height = 48.dp).testTag("eventButton"),
            colors = ButtonDefaults.buttonColors(),
            contentPadding = PaddingValues(0.dp)) {
              Text("About", modifier = Modifier.testTag("eventButtonText"))
            }
      })
  HorizontalDivider()
}
