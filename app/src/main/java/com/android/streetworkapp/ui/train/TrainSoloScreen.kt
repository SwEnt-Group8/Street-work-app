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
    userViewModel: UserViewModel,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
  val currentUser by userViewModel.currentUser.collectAsState()
  var isStopped by remember { mutableStateOf(false) }
  var durationAchieved by remember { mutableIntStateOf(0) }
  var count by remember { mutableIntStateOf(0) }

  Column(
      modifier =
          Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).testTag("TrainSoloScreen"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Train Solo", modifier = Modifier.testTag("TrainSoloTitle"))
        Text("Activity: $activity", modifier = Modifier.testTag("ActivityText"))
        Text("Time Dependent: $isTimeDependent", modifier = Modifier.testTag("TimeDependentText"))

        if (isTimeDependent) {
          // Timer logic
          if (!isStopped) {
            CircularTimer(
                totalTime = (time ?: 30).toFloat(),
                onTimeUp = {
                  isStopped = true
                  durationAchieved = time ?: 0
                  addExerciseToWorkout(
                      currentUser?.uid, workoutViewModel, activity, durationAchieved, count, count)
                })
          } else {
            Text("Time's Up!", modifier = Modifier.testTag("TimeUpText"))
          }

          Button(
              onClick = {
                isStopped = true
                durationAchieved = time ?: 0
                addExerciseToWorkout(
                    currentUser?.uid, workoutViewModel, activity, durationAchieved, count, count)
              },
              colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
              modifier = Modifier.testTag("StopButton")) {
                Text("I stopped", color = PRINCIPLE_BACKGROUND_COLOR)
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

          // When the user stops, we add the exercise to the workout
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
