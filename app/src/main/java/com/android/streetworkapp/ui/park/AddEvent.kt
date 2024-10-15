package com.android.streetworkapp.ui.park

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.android.streetworkapp.model.event.Event

/** Display a view that is used to add a new Event to a given park. */
@Composable fun AddEventScreen() {}

@Composable fun EventTypeSelection() {}

@Composable
fun ParticipantNumberSelection(event: Event) {
  val minParticipants = 2 // We need at least 2 users for every event
  val maxParticipants = 10 // An event can not have more then 10 participants
  var sliderPosition by remember { mutableFloatStateOf(minParticipants.toFloat()) }

  Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()) {
        Slider(
            modifier = Modifier.fillMaxWidth(0.8f),
            value = sliderPosition,
            onValueChange = {
              sliderPosition = it
              event.maxParticipants = sliderPosition.toInt()
            },
            colors =
                SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
            steps = 7,
            valueRange = minParticipants.toFloat()..maxParticipants.toFloat())
        Text(text = sliderPosition.toInt().toString())
      }
}

@Composable fun TimeSelection() {}
