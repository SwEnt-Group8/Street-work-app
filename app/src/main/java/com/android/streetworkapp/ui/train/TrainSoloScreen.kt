package com.android.streetworkapp.ui.train

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK
import com.android.streetworkapp.ui.theme.ColorPalette.PRINCIPLE_BACKGROUND_COLOR

@Composable
fun TrainSoloScreen(
    activity: String,
    isTimeDependent: Boolean,
    time: Int?,
    sets: Int?,
    reps: Int?,
    workoutViewModel: WorkoutViewModel,
    paddingValues: PaddingValues = PaddingValues(0.dp)

) {
  var isStopped by remember { mutableStateOf(false) }

  Column(
      modifier =
          Modifier.fillMaxSize()
              .padding(paddingValues)
              .padding(16.dp) // Apply padding once here for the entire column
              .testTag("TrainSoloScreen"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp) // Space between items
      ) {
        Text("Train Solo", modifier = Modifier.testTag("TrainSoloTitle"))
        Text("Activity: $activity", modifier = Modifier.testTag("ActivityText"))
        Text("Time Dependent: $isTimeDependent", modifier = Modifier.testTag("TimeDependentText"))

        if (isTimeDependent) {
          // Timer
          if (!isStopped) {
            CircularTimer(totalTime = 30f, onTimeUp = { isStopped = true })
          } else {
            Text("Time's Up!", modifier = Modifier.testTag("TimeUpText"))
          }

          Button(
              onClick = { isStopped = true },
              colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
              modifier = Modifier.testTag("StopButton")) {
                Text("I stopped", color = PRINCIPLE_BACKGROUND_COLOR)
              }
        } else {
          // Counter
          var count by remember { mutableIntStateOf(0) }

          AnimatedCounter(
              count = count,
              style = androidx.compose.material3.MaterialTheme.typography.displayLarge,
              modifier = Modifier.testTag("CounterText"))

          Row(
              horizontalArrangement = Arrangement.spacedBy(16.dp),
              verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { if (count > 0) count-- },
                    colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
                    modifier = Modifier.testTag("DecrementButton")) {
                      Text("-1", color = PRINCIPLE_BACKGROUND_COLOR)
                    }

                Button(
                    onClick = { count++ },
                    colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
                    modifier = Modifier.testTag("IncrementButton")) {
                      Text("+1", color = PRINCIPLE_BACKGROUND_COLOR)
                    }
              }
        }
      }
}
