package com.android.streetworkapp.ui.park

// Portions of this code were generated with the help of GitHub Copilot.

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventList
import com.android.streetworkapp.model.park.Park
import com.android.streetworkapp.utils.toFormattedString

/**
 * Display the overview of a park, including park details and a list of events.
 *
 * @param park The park data to display.
 */
@Composable
fun ParkOverviewScreen(park: Park) {
  Column {
    ImageTitle(image = park.image, title = park.name)
    ParkDetails(park = park)
    EventItemList(eventList = park.events)
  }
}

/**
 * Display an image with a title overlay.
 *
 * @param image The image to display.
 * @param title The title to display.
 */
@Composable
fun ImageTitle(image: Painter, title: String) {
  Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
    Image(
        painter = image,
        contentDescription = null,
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
        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp))
  }
}

/**
 * Display the details of a park, including the park's rating and occupancy.
 *
 * @param park The park data to display.
 */
@Composable
fun ParkDetails(park: Park) {
  Column {
    Text(
        text = "Details",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 6.dp, bottom = 2.dp))
    RatingComponent(rating = park.rating.toInt(), park.nbrRating)
    OccupancyBar(occupancy = park.occupancy)
  }
}

/**
 * Display a star rating from 1 to 5 and the number of reviews.
 *
 * @param rating The rating to display.
 * @param nbrReview The number of reviews.
 */
@Composable
fun RatingComponent(rating: Int, nbrReview: Int) {
  Row(modifier = Modifier.padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically) {
    for (i in 1..5) {
      Icon(
          imageVector = Icons.Default.Star,
          contentDescription = "Star",
          tint = if (i <= rating) Color(0xFF6650a4) else Color.Gray,
          modifier = Modifier.size(24.dp))
    }
    Text(
        text = "($nbrReview)",
        modifier = Modifier.padding(start = 8.dp),
        fontSize = 16.sp,
        fontWeight = FontWeight.Light)
  }
}

/**
 * Display a progress bar showing the park's occupancy.
 *
 * @param occupancy The park's occupancy percentage.
 */
@Composable
fun OccupancyBar(occupancy: Float) {
  Row(
      modifier = Modifier.padding(start = 16.dp, end = 16.dp),
      verticalAlignment = Alignment.CenterVertically) {
        LinearProgressIndicator(progress = occupancy / 100, modifier = Modifier.weight(1f))
        Text(
            text = "${occupancy.toInt()}% Occupancy",
            modifier = Modifier.padding(start = 8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Light)
      }
}

/**
 * Display a list of events.
 *
 * @param eventList The list of events to display.
 */
@Composable
fun EventItemList(eventList: EventList) {
  Column {
    Text(
        text = "Events",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 6.dp, bottom = 2.dp))
    LazyColumn { items(eventList.events) { event -> EventItem(event = event) } }
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
      headlineContent = { Text(text = event.title) },
      supportingContent = {
        Text(
            "Participants ${event.participants}/${event.maxParticipants}",
            fontWeight = FontWeight.Light)
      },
      overlineContent = {
        Text(text = event.date.toFormattedString(), fontWeight = FontWeight.Bold)
      },
      leadingContent = {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile Icon",
            modifier = Modifier.size(56.dp))
      },
      trailingContent = {
        Button(
            onClick = { TODO("Go to the event overview") },
            modifier = Modifier.size(width = 80.dp, height = 48.dp),
            colors = ButtonDefaults.buttonColors(),
            contentPadding = PaddingValues(0.dp)) {
              Text("About")
            }
      },
      modifier = Modifier.padding(0.dp))
  HorizontalDivider()
}
