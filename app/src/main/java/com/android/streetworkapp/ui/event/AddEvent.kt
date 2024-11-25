package com.android.streetworkapp.ui.event

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.android.streetworkapp.model.event.Event
import com.android.streetworkapp.model.event.EventConstants
import com.android.streetworkapp.model.event.EventViewModel
import com.android.streetworkapp.model.moderation.TextModerationViewModel
import com.android.streetworkapp.model.park.ParkViewModel
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
import kotlinx.coroutines.delay

object AddEventParams {
  const val TEXT_EVALUATION_DISPLAY_TIME = 5000L // in ns
  const val EVENT_INIT_TIME_CREATION_OFFSET = 1 // in hours
}

object AddEventFormErrorMessages {
  const val TITLE_EMPTY_ERROR_MESSAGE = "Event title cannot be empty."
  const val DATE_BACK_IN_TIME_ERROR = "Date cannot be in the past."
  const val TEXT_EVALUATION_OVER_THRESHOLDS_ERROR =
      "Your event's title and/or description contains language that may not meet our guidelines. Please review and make sure it's respectful and appropriate before submitting again."
  const val TEXT_EVALUATION_ERROR =
      "It looks like something went wrong on our end. This could be due to a network issue or an unexpected error. Please try again in a moment."
}

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

  val event by remember {
    mutableStateOf<Event>(
        Event(
            eid,
            "",
            "",
            1, // set to one by default, because the owner is also a participant
            EventConstants.MIN_NUMBER_PARTICIPANTS,
            Timestamp(
                Calendar.getInstance()
                    .apply {
                      add(Calendar.HOUR_OF_DAY, AddEventParams.EVENT_INIT_TIME_CREATION_OFFSET)
                    }
                    .time), // this is the time that's used as default in time selection as well
            "unknown"))
  }

  val owner = userViewModel.currentUser.collectAsState().value?.uid
  if (!owner.isNullOrEmpty()) {
    event.owner = owner
    event.listParticipants = listOf(owner)
  }

  val parkId = parkViewModel.currentPark.collectAsState().value?.pid
  if (!parkId.isNullOrEmpty()) {
    event.parkId = parkId
  }

  val isTitleEmptyError = remember {
    mutableStateOf(false)
  } // we keep track of the mutable state since we need to reset it in the fields below (thus
  // passing it as param)
  val isTextEvaluationOverThresholdsError = remember { mutableStateOf(false) } // same here
  val isDateBackInTimeError = remember { mutableStateOf(false) } // same here
  var isTextEvaluationError by remember {
    mutableStateOf(false)
  } // for api error, deserialization error or network issues
  var formErrorMessage by remember {
    mutableStateOf("")
  } // message to be displayed at bottom of form in case of form error

  /*
  If we add another error, just add it in the list for the text error to appear at bottom of form. The formErrorMessage is shown if any one of these is true
   */
  val errorStates =
      listOf(
          isTitleEmptyError.value,
          isTextEvaluationOverThresholdsError.value,
          isDateBackInTimeError.value,
          isTextEvaluationError)

  Box(
      modifier = Modifier.padding(paddingValues).background(MaterialTheme.colorScheme.background),
  ) {
    Box(modifier = Modifier.fillMaxSize().testTag("addEventScreen").padding(vertical = 15.dp)) {
      Column(
          modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally) {
            EventTitleSelection(event, isTitleEmptyError, isTextEvaluationOverThresholdsError)
            EventDescriptionSelection(event, isTextEvaluationOverThresholdsError)
            TimeSelection(event, isDateBackInTimeError)

            Text(
                text = "* Required fields",
                color = ColorPalette.SECONDARY_TEXT_COLOR,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth(0.9f))
            Text(
                text = "How many participants do you want?",
                fontSize = 18.sp,
            )
            ParticipantNumberSelection(event)

            if (errorStates.any { it }) {
              Text(
                  modifier =
                      Modifier.padding(vertical = 0.dp, horizontal = 25.dp).testTag("errorMessage"),
                  textAlign = TextAlign.Center,
                  color = MaterialTheme.colorScheme.error,
                  text = formErrorMessage)
            }
            // for non users-related-input errors, we only show the message for a brief amount of
            // time
            LaunchedEffect(isTextEvaluationError) {
              delay(AddEventParams.TEXT_EVALUATION_DISPLAY_TIME)
              isTextEvaluationError = false
            }

            Button(
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = ColorPalette.INTERACTION_COLOR_DARK,
                        contentColor = ColorPalette.PRIMARY_TEXT_COLOR),
                modifier = Modifier.testTag("addEventButton"),
                onClick = {
                  if (event.date.seconds < Timestamp(Calendar.getInstance().time).seconds) {
                    isDateBackInTimeError.value = true
                    formErrorMessage = AddEventFormErrorMessages.DATE_BACK_IN_TIME_ERROR
                  } else if (event.title.isEmpty()) {
                    isTitleEmptyError.value = true
                    formErrorMessage = AddEventFormErrorMessages.TITLE_EMPTY_ERROR_MESSAGE
                  } else {
                    // making sure the description & title are valid according to our evaluation
                    // thresholds
                    val formattedTextInput =
                        "Title: ${event.title}. Description: ${event.description}" // we format it
                    // to do only one
                    // api call
                    textModerationViewModel.analyzeText(
                        formattedTextInput,
                        { isTextUnderThresholds ->
                          if (isTextUnderThresholds) {
                            createEvent(
                                event,
                                navigationActions,
                                eventViewModel,
                                userViewModel,
                                parkViewModel,
                                context)
                          } else {
                            isTextEvaluationOverThresholdsError.value = true
                            formErrorMessage =
                                AddEventFormErrorMessages.TEXT_EVALUATION_OVER_THRESHOLDS_ERROR
                          }
                        },
                        {
                          isTextEvaluationError = true
                          formErrorMessage = AddEventFormErrorMessages.TEXT_EVALUATION_ERROR
                        })
                  }
                }) {
                  Text("Add new event")
                }
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
fun EventTitleSelection(
    event: Event,
    isTitleEmptyError: MutableState<Boolean>,
    isTextEvaluationError: MutableState<Boolean>
) {
  var title by remember { mutableStateOf("") }

  OutlinedTextField(
      value = title,
      onValueChange = {
        title = it
        event.title = title
        // reset the field errors
        isTitleEmptyError.value = false
        isTextEvaluationError.value = false
      },
      label = { Text("What's your event's title?*") },
      isError = isTitleEmptyError.value || isTextEvaluationError.value,
      modifier = Modifier.testTag("titleTag").fillMaxWidth(0.9f))
}

/**
 * Used to change the description of the event
 *
 * @param event the event that will be updated
 */
@Composable
fun EventDescriptionSelection(event: Event, isTextEvaluationError: MutableState<Boolean>) {
  var description by remember { mutableStateOf("") }

  OutlinedTextField(
      value = description,
      onValueChange = {
        description = it
        event.description = description
        isTextEvaluationError.value = false // reset the field error
      },
      label = { Text("Describe your event") },
      isError = isTextEvaluationError.value,
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
fun TimeSelection(event: Event, isDateError: MutableState<Boolean>) {
  var showDatePicker by remember { mutableStateOf(false) }
  var showTimePicker by remember { mutableStateOf(false) }

  val initialTime = Calendar.getInstance().apply { timeInMillis = event.date.toDate().time }
  val datePickerState = rememberDatePickerState(initialTime.timeInMillis)
  val timePickerState =
      rememberTimePickerState(
          initialHour = initialTime.get(Calendar.HOUR_OF_DAY),
          initialMinute = initialTime.get(Calendar.MINUTE),
          is24Hour = false,
      )

  val currentTimeSelection =
      datePickerState.selectedDateMillis?.let { selectedDateMillis ->
        val calendar =
            Calendar.getInstance().apply {
              timeInMillis = selectedDateMillis
              set(Calendar.HOUR_OF_DAY, timePickerState.hour)
              set(Calendar.MINUTE, timePickerState.minute)
              set(Calendar.SECOND, 0)
              set(Calendar.MILLISECOND, 0)
            }
        calendar.timeInMillis
      }

  val selectedDate =
      datePickerState.selectedDateMillis?.let { convertMillisToDate(currentTimeSelection!!) } ?: ""

  Box(modifier = Modifier.fillMaxWidth(0.9f).padding(bottom = 0.dp)) {
    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        isError = isDateError.value,
        label = { Text("When do you want your event to be?*") },
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
        modifier = Modifier.fillMaxWidth())

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
                Modifier.fillMaxSize()
                    .offset(y = 10.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .align(Alignment.Center)) {
              TimePicker(state = timePickerState, modifier = Modifier.fillMaxWidth())

              Button(
                  modifier = Modifier.testTag("validateTime"),
                  colors = ButtonColors(Color.Blue, Color.White, Color.Blue, Color.White),
                  onClick = {
                    showTimePicker = false
                    isDateError.value = false // reset the field error

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
  formatter.timeZone = TimeZone.getDefault()
  return formatter.format(Date(millis))
}

private fun createEvent(
    event: Event,
    navigationActions: NavigationActions,
    eventViewModel: EventViewModel,
    userViewModel: UserViewModel,
    parkViewModel: ParkViewModel,
    context: Context
) {
  eventViewModel.addEvent(event)
  parkViewModel.addEventToPark(event.parkId, event.eid)

  // Used for the gamification feature
  userViewModel.increaseUserScore(event.owner, ScoreIncrease.CREATE_EVENT.scoreAdded)
  // Note: temporary value to use the progression screen. Should be update once
  // the gamification is completed
  Toast.makeText(
          context, "+" + ScoreIncrease.CREATE_EVENT.scoreAdded + " Points", Toast.LENGTH_SHORT)
      .show()

  navigationActions.goBack()
}
