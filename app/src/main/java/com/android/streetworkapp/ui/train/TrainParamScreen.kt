package com.android.streetworkapp.ui.train

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK
import com.android.streetworkapp.ui.theme.ColorPalette.PRINCIPLE_BACKGROUND_COLOR

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
      verticalArrangement = Arrangement.Center // Center vertically
      ) {
        // Prompt for the activity
        Text(
            text = "Set parameters for $activity training",
            modifier = Modifier.testTag("ActivityPrompt"))

        // Timer or repetitions display
        Text(
            text =
                if (isTimeDependent) {
                  String.format("%02d min %02d s", minutes, seconds)
                } else {
                  "Sets: $sets, Reps: $reps"
                },
            modifier = Modifier.padding(vertical = 16.dp).testTag("ParamDisplay"),
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)

        // Input grid for timer (time-dependent activities)
        if (isTimeDependent) {
          TimerInputGrid(
              minutes = minutes,
              seconds = seconds,
              onUpdateMinutes = { minutes = it },
              onUpdateSeconds = { seconds = it })
        } else {
          // Simple adjustment for sets and reps
          Row(
              horizontalArrangement = Arrangement.spacedBy(16.dp),
              verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("Sets")
                  Button(
                      onClick = { if (sets > 1) sets-- },
                      modifier = Modifier.testTag("DecrementSets")) {
                        Text("-")
                      }
                  Text("$sets")
                  Button(onClick = { sets++ }, modifier = Modifier.testTag("IncrementSets")) {
                    Text("+")
                  }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("Reps")
                  Button(
                      onClick = { if (reps > 1) reps-- },
                      modifier = Modifier.testTag("DecrementReps")) {
                        Text("-")
                      }
                  Text("$reps")
                  Button(onClick = { reps++ }, modifier = Modifier.testTag("IncrementReps")) {
                    Text("+")
                  }
                }
              }
        }

        // Confirm button
        Button(
            onClick = {
              val time = if (isTimeDependent) (minutes * 60 + seconds) else null
              when (type) {
                "Solo" ->
                    navigationActions.navigateToSoloScreen(
                        activity = activity,
                        isTimeDependent = isTimeDependent,
                        time = time,
                        sets = if (!isTimeDependent) sets else null,
                        reps = if (!isTimeDependent) reps else null)
                "Coach" ->
                    navigationActions.navigateToCoachScreen(
                        activity = activity,
                        isTimeDependent = isTimeDependent,
                        time = time,
                        sets = if (!isTimeDependent) sets else null,
                        reps = if (!isTimeDependent) reps else null)
                "Challenge" ->
                    navigationActions.navigateToChallengeScreen(
                        activity = activity,
                        isTimeDependent = isTimeDependent,
                        time = time,
                        sets = if (!isTimeDependent) sets else null,
                        reps = if (!isTimeDependent) reps else null)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
            modifier = Modifier.padding(top = 24.dp).testTag("ConfirmButton")) {
              Text("Confirm", color = PRINCIPLE_BACKGROUND_COLOR)
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
      verticalArrangement = Arrangement.spacedBy(8.dp)) {
        buttonTexts.forEach { row ->
          Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically) {
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
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
              // Display the character inside the button
              Text(
                  text = char,
                  fontSize = buttonSize.width.value * 0.3.sp, // Dynamically adjust font size
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) INTERACTION_COLOR_DARK else Color.Gray)
            }
      }
}
