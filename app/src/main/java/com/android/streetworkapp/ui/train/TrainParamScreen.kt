package com.android.streetworkapp.ui.train

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.android.sample.R
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.TrainNavigationParams
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_LIGHT
import com.android.streetworkapp.ui.theme.ColorPalette.PRIMARY_TEXT_COLOR
import com.android.streetworkapp.ui.theme.ColorPalette.PRINCIPLE_BACKGROUND_COLOR
import com.android.streetworkapp.ui.theme.GoogleAuthButtonTextStyle

@SuppressLint("DefaultLocale")
@Composable
fun TrainParamScreen(
    navigationActions: NavigationActions,
    activity: String,
    isTimeDependent: Boolean,
    type: String,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
  var minutes by remember { mutableIntStateOf(0) }
  var seconds by remember { mutableIntStateOf(0) }
  var sets by remember { mutableIntStateOf(1) }
  var reps by remember { mutableIntStateOf(10) }

  Column(
      modifier =
          Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).testTag("TrainParamScreen"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        TrainParamHeader(isTimeDependent, minutes, seconds, sets, reps, activity)

        if (isTimeDependent) {
          TimerInputGrid(minutes, seconds, { minutes = it }, { seconds = it })
        } else {
          SetsAndRepsSection(sets, reps, { sets = it }, { reps = it })
        }

        ConfirmActionButton(
            navigationActions, activity, isTimeDependent, type, minutes, seconds, sets, reps)
      }
}

@SuppressLint("DefaultLocale", "StringFormatMatches")
@Composable
fun TrainParamHeader(
    isTimeDependent: Boolean,
    minutes: Int,
    seconds: Int,
    sets: Int,
    reps: Int,
    activity: String
) {
  Box(
      modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).testTag("ParamDisplay"),
      contentAlignment = Alignment.Center) {
        Text(
            text =
                if (isTimeDependent) {
                  LocalContext.current.getString(
                      R.string.WorkoutDurationMessage1, minutes, seconds, activity)
                } else {
                  LocalContext.current.getString(
                      R.string.WorkoutDurationMessage2, sets, reps, activity)
                },
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
      }
}

@Composable
fun SetsAndRepsSection(
    sets: Int,
    reps: Int,
    onUpdateSets: (Int) -> Unit,
    onUpdateReps: (Int) -> Unit
) {
  NumberPicker(
      label = "Number of sets:",
      value = sets,
      range = 0..100,
      onValueChange = onUpdateSets,
      modifier = Modifier.testTag("SetsPicker"))
  NumberPicker(
      label = "Number of reps:",
      value = reps,
      range = 0..100,
      onValueChange = onUpdateReps,
      modifier = Modifier.testTag("RepsPicker"))
}

@Composable
fun ConfirmActionButton(
    navigationActions: NavigationActions,
    activity: String,
    isTimeDependent: Boolean,
    type: String,
    minutes: Int,
    seconds: Int,
    sets: Int,
    reps: Int
) {
  Button(
      onClick = {
        val params =
            TrainNavigationParams(
                activity = activity,
                isTimeDependent = isTimeDependent,
                time = if (isTimeDependent) (minutes * 60 + seconds) else null,
                sets = if (!isTimeDependent) sets else null,
                reps = if (!isTimeDependent) reps else null)

        when (type) {
          "Solo" -> navigationActions.navigateToSoloScreen(params)
          "Coach" -> navigationActions.navigateToCoachScreen(params)
          "Challenge" -> navigationActions.navigateToChallengeScreen(params)
        }
      },
      colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
      modifier = Modifier.padding(top = 24.dp).testTag("ConfirmButton")) {
        Text("Confirm", color = PRINCIPLE_BACKGROUND_COLOR)
      }
}

@Composable
fun NumberPicker(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
  val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = range.indexOf(value))

  // Listen to scroll position changes and update the value accordingly
  LaunchedEffect(lazyListState) {
    snapshotFlow { lazyListState.firstVisibleItemIndex }
        .collect { index ->
          val newValue = range.elementAtOrNull(index + 1)
          newValue?.let { onValueChange(it) }
        }
  }

  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = modifier.padding(vertical = 16.dp)) {
        // Label for the picker
        Text(
            text = label,
            style = GoogleAuthButtonTextStyle,
            modifier = Modifier.padding(bottom = 8.dp))

        Box(
            modifier =
                Modifier.size(60.dp, 120.dp).border(1.dp, Color.Gray, RoundedCornerShape(8.dp))) {
              LazyColumn(
                  state = lazyListState,
                  modifier = Modifier.fillMaxSize(),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center) {
                    items(range.toList()) { number ->
                      Box(
                          modifier = Modifier.fillMaxWidth().height(40.dp),
                          contentAlignment = Alignment.Center) {
                            Text(
                                text = number.toString(),
                                style =
                                    androidx.compose.material3.MaterialTheme.typography
                                        .headlineMedium)
                          }
                    }
                  }
            }
      }
}

@Composable
fun TimerInputGrid(
    minutes: Int,
    seconds: Int,
    onUpdateMinutes: (Int) -> Unit,
    onUpdateSeconds: (Int) -> Unit
) {
  val buttonTexts =
      listOf(
          listOf("1", "2", "3"),
          listOf("4", "5", "6"),
          listOf("7", "8", "9"),
          listOf("00", "0", "⌫"))

  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.testTag("TimerInputGrid")) {
        buttonTexts.forEachIndexed { rowIndex, row ->
          Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.testTag("Row$rowIndex")) {
                row.forEach { char ->
                  SelectionButtonWithChar(
                      char = char,
                      buttonSize = ButtonSize(80.dp, 80.dp, 8.dp),
                      onClick = {
                        when (char) {
                          "⌫" ->
                              handleDeleteInput(minutes, seconds, onUpdateMinutes, onUpdateSeconds)
                          else ->
                              handleInput(char, minutes, seconds, onUpdateMinutes, onUpdateSeconds)
                        }
                      },
                      isSelected = false,
                      testTag = "Button$char")
                }
              }
        }
      }
}

/**
 * Delete the last input character from the timer
 *
 * @param minutes The current minutes value
 * @param seconds The current seconds value
 * @param onUpdateMinutes The callback to update the minutes value
 * @param onUpdateSeconds The callback to update the seconds value
 */
@SuppressLint("DefaultLocale")
internal fun handleDeleteInput(
    minutes: Int,
    seconds: Int,
    onUpdateMinutes: (Int) -> Unit,
    onUpdateSeconds: (Int) -> Unit
) {
  val currentTime = String.format("%02d%02d", minutes, seconds)

  val updatedTime = "0" + currentTime.dropLast(1)

  val updatedMinutes = updatedTime.substring(0, 2).toIntOrNull() ?: 0
  val updatedSeconds = updatedTime.substring(2).toIntOrNull() ?: 0

  onUpdateMinutes(updatedMinutes)
  onUpdateSeconds(updatedSeconds)
}

/**
 * Handle the input of a character into the timer
 *
 * @param input The input character
 * @param minutes The current minutes value
 * @param seconds The current seconds value
 * @param onUpdateMinutes The callback to update the minutes value
 * @param onUpdateSeconds The callback to update the seconds value
 */
@SuppressLint("DefaultLocale")
internal fun handleInput(
    input: String,
    minutes: Int,
    seconds: Int,
    onUpdateMinutes: (Int) -> Unit,
    onUpdateSeconds: (Int) -> Unit
) {
  val currentTime = String.format("%02d%02d", minutes, seconds)

  val updatedTime =
      if (input == "00") {
        currentTime.drop(2) + "00"
      } else {
        currentTime.drop(1) + input
      }

  val updatedMinutes = updatedTime.substring(0, 2).toIntOrNull() ?: 0
  val updatedSeconds = updatedTime.substring(2).toIntOrNull() ?: 0

  onUpdateMinutes(updatedMinutes)
  onUpdateSeconds(updatedSeconds)
}

@Composable
fun SelectionButtonWithChar(
    char: String,
    buttonSize: ButtonSize,
    onClick: () -> Unit,
    isSelected: Boolean,
    testTag: String
) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.width(buttonSize.width)) {
        Button(
            onClick = onClick,
            modifier =
                Modifier.size(buttonSize.width, buttonSize.height)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) INTERACTION_COLOR_DARK else Color.Gray,
                        shape = RoundedCornerShape(20.dp))
                    .testTag(testTag),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_LIGHT)) {
              // Display the character inside the button
              Text(
                  text = char,
                  fontSize = buttonSize.width.value * 0.3.sp, // Dynamically adjust font size
                  fontWeight = FontWeight.Bold,
                  color = PRIMARY_TEXT_COLOR)
            }
      }
}
