package com.android.streetworkapp.ui.train

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
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

  Column(
      modifier =
          Modifier.fillMaxSize()
              .padding(paddingValues)
              .padding(16.dp)
              .testTag("TimerParameterScreen"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Prompt for the activity
        Text(
            text = "How much time would you do $activity?",
            modifier = Modifier.testTag("ActivityPrompt"))

        // Display the timer values
        Text(
            text = String.format("%02d min %02d s", minutes, seconds),
            modifier = Modifier.testTag("TimerDisplay"))

        // Grid of buttons for input
        TimerInputGrid(
            minutes = minutes,
            seconds = seconds,
            onUpdateMinutes = { minutes = it },
            onUpdateSeconds = { seconds = it })

        // Confirm button
        Button(
            onClick = {
              when (type) {
                "Solo" -> navigationActions.navigateToSoloScreen(activity, isTimeDependent)
                "Coach" -> navigationActions.navigateToCoachScreen(activity, isTimeDependent)
                "Challenge" ->
                    navigationActions.navigateToChallengeScreen(activity, isTimeDependent)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
            modifier = Modifier.testTag("ConfirmButton")) {
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
                row.forEach { text ->
                  Button(
                      onClick = {
                        when (text) {
                          "⌫" ->
                              handleDeleteInput(minutes, seconds, onUpdateMinutes, onUpdateSeconds)
                          else ->
                              handleInput(text, minutes, seconds, onUpdateMinutes, onUpdateSeconds)
                        }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
                      modifier = Modifier.weight(1f).testTag("Button$text")) {
                        Text(text, color = PRINCIPLE_BACKGROUND_COLOR)
                      }
                }
              }
        }
      }
}

private fun handleDeleteInput(
    minutes: Int,
    seconds: Int,
    onUpdateMinutes: (Int) -> Unit,
    onUpdateSeconds: (Int) -> Unit
) {
  if (seconds > 0) {
    onUpdateSeconds(seconds / 10)
  } else if (minutes > 0) {
    onUpdateSeconds(minutes % 10)
    onUpdateMinutes(minutes / 10)
  }
}

private fun handleInput(
    input: String,
    minutes: Int,
    seconds: Int,
    onUpdateMinutes: (Int) -> Unit,
    onUpdateSeconds: (Int) -> Unit
) {
  val num = input.toIntOrNull() ?: return
  if (seconds < 60) {
    onUpdateSeconds((seconds * 10 + num) % 60)
  } else if (minutes < 100) {
    onUpdateMinutes((minutes * 10 + num) % 100)
  }
}
