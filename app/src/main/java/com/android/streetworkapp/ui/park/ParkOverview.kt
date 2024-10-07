package com.android.streetworkapp.ui.park

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.utils.toFormattedString

@Composable
fun EventItemList(events: List<Event>) {
  LazyColumn { items(events) { event -> EventItem(event = event) } }
}

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
      modifier = Modifier.padding(8.dp))
  HorizontalDivider()
}
