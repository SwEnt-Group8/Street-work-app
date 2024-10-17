package com.android.streetworkapp.ui.park

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventConstants
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/**
 * Display a view that is used to add a new Event to a given park.
 *
 * @param navigationActions used to navigate in the app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    navigationActions: NavigationActions
) { // TODO: add future ParkViewModel and EventViewModel

  // used until we have the corresponding viewModel TODO: update with new viewModel
  val event = Event("unknown", "unknown", "unknown", 0, 2, Timestamp(0, 0), "unknown")

  Scaffold(
      modifier = Modifier.background(MaterialTheme.colorScheme.background),
      topBar = {
        TopAppBar(
            title = {},
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background),
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("goBackButton")) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Arrow Back Icon")
                  }
            })
      }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize().testTag("addEventScreen")) {
          Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(18.dp),
              horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Event Creation",
                    fontSize = 24.sp,
                )
                EventTitleSelection(event)
                EventDescriptionSelection(event)
                TimeSelection(event)
                Text(
                    text = "How many participants do you want?",
                    fontSize = 18.sp,
                )
                ParticipantNumberSelection(event)
              }
          FloatingActionButton(
              onClick = {
                // TODO: use one of the future viewModel to add the event to firebase
                navigationActions.goBack()
              },
              modifier =
                  Modifier.align(Alignment.BottomCenter)
                      .padding(40.dp)
                      .size(width = 150.dp, height = 40.dp)
                      .testTag("addEventButton"),
          ) {
            Text("Add new event")
          }
        }
      }
}

/**
 * Used to change the title of the event
 *
 * @param event the event that will be updated
 */
@Composable
fun EventTitleSelection(event: Event) {
  var title by remember { mutableStateOf("") }

  OutlinedTextField(
      value = title,
      onValueChange = {
        title = it
        event.title = title
      },
      label = { Text("What kind of event do you want to create?") },
      modifier = Modifier.testTag("titleTag").fillMaxWidth(0.9f).height(64.dp))
}

/**
 * Used to change the description of the event
 *
 * @param event the event that will be updated
 */
@Composable
fun EventDescriptionSelection(event: Event) {
  var description by remember { mutableStateOf("") }

  OutlinedTextField(
      value = description,
      onValueChange = {
        description = it
        event.description = description
      },
      label = { Text("Describe your event:") },
      modifier = Modifier.testTag("descriptionTag").fillMaxWidth(0.9f).height(128.dp))
}

/**
 * Used to change the maximum number of participants of the event
 *
 * @param event the event that will be updated
 */
@Composable
fun ParticipantNumberSelection(event: Event) {
  var sliderPosition by remember {
    mutableFloatStateOf(EventConstants.MIN_NUMBER_PARTICIPANTS.toFloat())
  }

  Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()) {
        Slider(
            modifier = Modifier.fillMaxWidth(0.8f).testTag("sliderMaxParticipants"),
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
            valueRange =
                EventConstants.MIN_NUMBER_PARTICIPANTS.toFloat()..EventConstants
                        .MAX_NUMBER_PARTICIPANTS
                        .toFloat())
        Text(text = sliderPosition.toInt().toString())
      }
}

/**
 * Used to change the date and the hour of the event
 *
 * @param event the event that will be updated
 */
@ExperimentalMaterial3Api
@Composable
fun TimeSelection(event: Event) {
  var showDatePicker by remember { mutableStateOf(false) }
  var showTimePicker by remember { mutableStateOf(false) }

  val currentTime = Calendar.getInstance()
  val datePickerState = rememberDatePickerState(currentTime.timeInMillis)
  val timePickerState =
      rememberTimePickerState(
          initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
          initialMinute = currentTime.get(Calendar.MINUTE),
          is24Hour = false,
      )

  val currentTimeSelection =
      datePickerState.selectedDateMillis?.plus(
          (TimeUnit.HOURS.toMillis(timePickerState.hour.toLong()) +
              TimeUnit.MINUTES.toMillis(timePickerState.minute.toLong())))

  val selectedDate =
      datePickerState.selectedDateMillis?.let { convertMillisToDate(currentTimeSelection!!) } ?: ""

  Box(modifier = Modifier.fillMaxWidth(0.9f)) {
    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        label = { Text("When do you want to train?") },
        readOnly = true,
        trailingIcon = {
          Row {
            IconButton(
                modifier = Modifier.testTag("dateIcon"),
                onClick = { showDatePicker = !showDatePicker }) {
                  Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select date")
                }
            IconButton(
                modifier = Modifier.testTag("timeIcon"),
                onClick = { showTimePicker = !showTimePicker }) {
                  Icon(imageVector = Icons.Outlined.AccessTime, contentDescription = "Select time")
                }
          }
        },
        modifier = Modifier.fillMaxWidth().height(64.dp))

    if (showDatePicker) {
      Popup(onDismissRequest = { showDatePicker = false }, alignment = Alignment.TopStart) {
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .shadow(elevation = 4.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)) {
              DatePicker(state = datePickerState, showModeToggle = false)
            }

        Button(
            modifier = Modifier.offset(x = 280.dp, y = 140.dp).testTag("validateDate"),
            onClick = {
              showDatePicker = false

              event.date =
                  datePickerState.selectedDateMillis
                      ?.let { TimeUnit.MILLISECONDS.toSeconds(currentTimeSelection!!) }
                      ?.let { Timestamp(it, 0) }!!
            }) {
              Text("Validate")
            }
      }
    }

    if (showTimePicker) {
      Popup(onDismissRequest = { showTimePicker = false }, alignment = Alignment.TopStart) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier.fillMaxSize()
                    .offset(y = 10.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .align(Alignment.Center)) {
              TimePicker(state = timePickerState, modifier = Modifier.fillMaxWidth())

              Button(
                  modifier = Modifier.testTag("validateTime"),
                  onClick = {
                    showTimePicker = false

                    event.date =
                        datePickerState.selectedDateMillis
                            ?.let { TimeUnit.MILLISECONDS.toSeconds(currentTimeSelection!!) }
                            ?.let { Timestamp(it, 0) }!!
                  }) {
                    Text("Validate")
                  }
            }
      }
    }
  }
}

private fun convertMillisToDate(millis: Long): String {
  val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
  formatter.timeZone = TimeZone.getTimeZone("UTC")
  return formatter.format(Date(millis))
}
