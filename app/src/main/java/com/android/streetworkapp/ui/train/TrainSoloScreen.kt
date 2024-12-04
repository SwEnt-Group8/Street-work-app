package com.android.streetworkapp.ui.train

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.Exercise
import com.android.streetworkapp.model.workout.SessionType
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.theme.ColorPalette.BORDER_COLOR
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK
import com.android.streetworkapp.ui.theme.ColorPalette.PRINCIPLE_BACKGROUND_COLOR
import com.android.streetworkapp.utils.Graph
import com.android.streetworkapp.utils.GraphConfiguration
import com.android.streetworkapp.utils.GraphData

@Composable
fun TrainSoloScreen(
    activity: String,
    isTimeDependent: Boolean,
    time: Int?,
    sets: Int?,
    reps: Int?,
    workoutViewModel: WorkoutViewModel,
    userViewModel: UserViewModel,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
  val currentUser by userViewModel.currentUser.collectAsState()
  val workoutData by workoutViewModel.workoutData.collectAsState()
  var isStopped by remember { mutableStateOf(false) }
  var durationAchieved by remember { mutableIntStateOf(0) }
  var count by remember { mutableIntStateOf(0) }
  var isResetEnabled by remember { mutableStateOf(false) }

  val graphData =
      workoutData?.workoutSessions?.mapIndexed { index, session ->
        val duration = session.exercises.find { it.name == activity }?.duration?.toFloat() ?: 0f
        GraphData(x = index.toFloat(), y = duration)
      } ?: emptyList()

  Column(
      modifier =
          Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).testTag("TrainSoloScreen"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween) {
        if (isTimeDependent) {
          if (!isStopped) {
            CircularTimer(
                totalTime = (time ?: 30).toFloat(),
                onTimeUp = {
                  isStopped = true
                  durationAchieved = time ?: 0
                  addExerciseToWorkout(
                      currentUser?.uid, workoutViewModel, activity, durationAchieved, count, count)
                },
                onTimeUpdate = { elapsedTime -> durationAchieved = elapsedTime.toInt() })
          } else {
            Text("Nice Work!", modifier = Modifier.testTag("TimeUp"))
          }

          if (!isResetEnabled) {
            Button(
                onClick = {
                  isStopped = true
                  isResetEnabled = true // Switch to "Reset" button
                  addExerciseToWorkout(
                      currentUser?.uid, workoutViewModel, activity, durationAchieved, count, count)
                },
                colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
                modifier = Modifier.testTag("StopButton")) {
                  Text("Stop", color = PRINCIPLE_BACKGROUND_COLOR)
                }
          } else {
            Button(
                onClick = {
                  // Reset timer and state
                  isStopped = false
                  isResetEnabled = false // Switch back to "Stop" button
                  durationAchieved = 0
                  count = 0
                },
                colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
                modifier = Modifier.testTag("ResetButton")) {
                  Text("Reset", color = PRINCIPLE_BACKGROUND_COLOR)
                }
          }
        } else {
          // Counter logic
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

          Button(
              onClick = {
                addExerciseToWorkout(
                    currentUser?.uid, workoutViewModel, activity, durationAchieved, count, count)
              },
              colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
              modifier = Modifier.testTag("AddExerciseButton")) {
                Text("Add Exercise", color = PRINCIPLE_BACKGROUND_COLOR)
              }
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp).testTag("Divider"),
            thickness = 1.dp,
            color = BORDER_COLOR)
        // Performance History Graph
        if (graphData.isNotEmpty()) {
          Text("Performance History")
          Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            Graph(
                graphConfiguration =
                    GraphConfiguration(
                        xUnitLabel = "Session", yUnitLabel = "Duration", dataPoints = graphData))
          }
        } else {
          Text("No performance history available.")
        }
      }
}

fun addExerciseToWorkout(
    userId: String?,
    workoutViewModel: WorkoutViewModel,
    activity: String,
    duration: Int?,
    reps: Int?,
    sets: Int?
) {
  if (userId.isNullOrEmpty()) {
    return
  }

  workoutViewModel.getOrAddExerciseToWorkout(
      uid = userId,
      sessionId = "solo_${System.currentTimeMillis()}",
      exercise = Exercise(activity, reps, sets, duration = duration),
      sessionType = SessionType.SOLO)
}
