package com.android.streetworkapp.ui.event

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventConstants
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.moderation.TextModerationViewModel
import com.android.streetworkapp.model.park.ParkViewModel
import com.android.streetworkapp.model.parklocation.ParkLocationViewModel
import com.android.streetworkapp.model.progression.ScoreIncrease
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.theme.ColorPalette
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
    navigationActions: NavigationActions,
    parkViewModel: ParkViewModel,
    eventViewModel: EventViewModel,
    userViewModel: UserViewModel,
    textModerationViewModel: TextModerationViewModel,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {

  val context = LocalContext.current

  val eid = eventViewModel.getNewEid()
  val event =
      Event(
          eid,
          "",
          "",
          1, // set to one by default, because the owner is also a participant
          EventConstants.MIN_NUMBER_PARTICIPANTS,
          Timestamp(0, 0),
          "unknown")

  val owner = userViewModel.currentUser.collectAsState().value?.uid
  if (!owner.isNullOrEmpty()) {
    event.owner = owner
    event.listParticipants = listOf(owner)
  }

  val parkId = parkViewModel.currentPark.collectAsState().value?.pid
  if (!parkId.isNullOrEmpty()) {
    event.parkId = parkId
  }

  //Will be triggered if text is over thresholds
  var textModerationEvaluationIsOverThresholds = remember { mutableStateOf(false) } //we keep track of the mutable state since we need to reset it in the fields below (thus passing it as param)

  Box(
      modifier = Modifier
          .padding(paddingValues)
          .background(MaterialTheme.colorScheme.background),
  ) {
    Box(modifier = Modifier
        .padding(top = 25.dp)
        .fillMaxSize()
        .testTag("addEventScreen")) {
      Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.size(24.dp))
            EventTitleSelection(event, textModerationEvaluationIsOverThresholds)
            EventDescriptionSelection(event, textModerationEvaluationIsOverThresholds)
            TimeSelection(event)
            Text(
                text = "How many participants do you want?",
                fontSize = 18.sp,
            )
            ParticipantNumberSelection(event)
          }
      FloatingActionButton(
          onClick = {
            if (event.date.toDate() < Calendar.getInstance().time) {
              Toast.makeText(context, "Date cannot be in the past", Toast.LENGTH_SHORT).show()
            } else if (event.title.isEmpty()) {
              Toast.makeText(context, "Please fill the title of the event", Toast.LENGTH_SHORT)
                  .show()
            } else {
          //make sure the description & title are valid according to our evaluation thresholds
              val formattedTextInput = "Title: ${event.title}. Description: ${event.description}" //we format it to do only one api call
              textModerationViewModel.analyzeText(formattedTextInput,
                  { isTextUnderThresholds ->
                      if (isTextUnderThresholds) {
                          createEvent(event, navigationActions, eventViewModel, userViewModel, parkViewModel, context)
                      } else {
                          //do some kind of UI work here
                            textModerationEvaluationIsOverThresholds.value = true
                      }
                  }, { /*display the error message in someway*/ })
            }
          },
          modifier =
          Modifier
              .align(Alignment.Center)
              .offset(0.dp, 100.dp)
              .padding(40.dp)
              .size(width = 150.dp, height = 40.dp)
              .testTag("addEventButton"),
          containerColor = ColorPalette.INTERACTION_COLOR_DARK,
          contentColor = ColorPalette.PRIMARY_TEXT_COLOR) {
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
fun EventTitleSelection(event: Event, isError: MutableState<Boolean>) {
  var title by remember { mutableStateOf("") }

  OutlinedTextField(
      value = title,
      onValueChange = {
        title = it
        event.title = title
        isError.value = false //reset the field
      },
      label = { Text("What kind of event do you want to create?") },
      isError = isError.value,
      modifier = Modifier
          .testTag("titleTag")
          .fillMaxWidth(0.9f)
          .height(64.dp),
  )
}

/**
 * Used to change the description of the event
 *
 * @param event the event that will be updated
 */
@Composable
fun EventDescriptionSelection(event: Event, isError: MutableState<Boolean>) {
  var description by remember { mutableStateOf("") }

  OutlinedTextField(
      value = description,
      onValueChange = {
        description = it
        event.description = description
        isError.value = false //reset the field
      },
      label = { Text("Describe your event:") },
      isError = isError.value,
      modifier = Modifier
          .testTag("descriptionTag")
          .fillMaxWidth(0.9f)
          .height(128.dp))
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
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .testTag("sliderMaxParticipants"),
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
        Text(text = sliderPosition.toInt().toString(), color = Color.Black)
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
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp))

    if (showDatePicker) {
      Popup(onDismissRequest = { showDatePicker = false }, alignment = Alignment.TopStart) {
        Box(
            modifier =
            Modifier
                .fillMaxWidth()
                .shadow(elevation = 4.dp)
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)) {
              DatePicker(state = datePickerState, showModeToggle = false)
            }

        Button(
            modifier = Modifier
                .offset(x = 280.dp, y = 140.dp)
                .testTag("validateDate"),
            colors = ButtonColors(Color.Blue, Color.White, Color.Blue, Color.White),
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
            Modifier
                .fillMaxSize()
                .offset(y = 10.dp)
                .background(MaterialTheme.colorScheme.surface)
                .align(Alignment.Center)) {
              TimePicker(state = timePickerState, modifier = Modifier.fillMaxWidth())

              Button(
                  modifier = Modifier.testTag("validateTime"),
                  colors = ButtonColors(Color.Blue, Color.White, Color.Blue, Color.White),
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



private fun createEvent(event: Event, navigationActions: NavigationActions, eventViewModel: EventViewModel, userViewModel: UserViewModel, parkViewModel: ParkViewModel, context: Context) {
    eventViewModel.addEvent(event)
    parkViewModel.addEventToPark(event.parkId, event.eid)

    // Used for the gamification feature
    userViewModel.increaseUserScore(event.owner, ScoreIncrease.CREATE_EVENT.scoreAdded)
    // Note: temporary value to use the progression screen. Should be update once
    // the gamification is completed
    Toast.makeText(
        context,
        "+" + ScoreIncrease.CREATE_EVENT.scoreAdded + " Points",
        Toast.LENGTH_SHORT)
        .show()

    navigationActions.goBack()
}

