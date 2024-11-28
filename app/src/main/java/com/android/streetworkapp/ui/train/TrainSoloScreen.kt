package com.android.streetworkapp.ui.train

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK

@Composable
fun TrainSoloScreen(
    activity: String,
    isTimeDependent: Boolean,
    workoutViewModel: WorkoutViewModel,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
  var isStopped by remember { mutableStateOf(false) }

  Column(
      modifier = Modifier.fillMaxSize().padding(paddingValues),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Text("Train Solo")
        Text("Activity: $activity")
        Text("Time Dependent: $isTimeDependent")

        Spacer(modifier = Modifier.height(16.dp))

        // Show the timer if the activity is time-dependent
        if (isTimeDependent) {
          if (!isStopped) {
            CircularTimer(totalTime = 30, onTimeUp = { isStopped = true })
          } else {
            Text("Time's Up!")
          }

          Spacer(modifier = Modifier.height(16.dp))

          Button(
              onClick = { isStopped = true },
              colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK)) {
                Text("I stopped", color = Color.White)
              }
        } else {
          // Manual increment/decrement buttons for non-time-dependent activities
          var count by remember { mutableStateOf(0) }

          AnimatedCounter(
              count = count,
              style = androidx.compose.material3.MaterialTheme.typography.displayLarge)

          Spacer(modifier = Modifier.height(16.dp))

          Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { if (count > 0) count-- }, // Decrement
                colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK)) {
                  Text("-1", color = Color.White)
                }

            Button(
                onClick = { count++ }, // Increment
                colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK)) {
                  Text("+1", color = Color.White)
                }
          }
        }
      }
}
