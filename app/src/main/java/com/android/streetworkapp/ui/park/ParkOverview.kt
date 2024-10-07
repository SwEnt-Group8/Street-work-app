package com.android.streetworkapp.ui.park

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.streetworkapp.model.event.Event

@Composable
fun EventItem(event: Event) {
  ListItem(
      headlineContent = { Text(text = event.title, lineHeight = 18.sp) },
      supportingContent = {
        Text(
            "Participants ${event.participants}/${event.maxParticipants}",
            fontWeight = FontWeight.Light)
      },
      overlineContent = { Text("31/10/2024 17:00", fontWeight = FontWeight.Bold) },
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
            shape = RectangleShape,
            contentPadding = PaddingValues(0.dp)) {
              Text("About")
            }
      },
      modifier = Modifier.padding(8.dp))
  HorizontalDivider()
}
