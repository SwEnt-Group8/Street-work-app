package com.android.streetworkapp.ui.train

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.theme.ColorPalette.BORDER_COLOR
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK
import com.android.streetworkapp.ui.theme.ColorPalette.PRIMARY_TEXT_COLOR
import com.android.streetworkapp.ui.theme.ColorPalette.PRINCIPLE_BACKGROUND_COLOR

@Composable
fun TrainCoachScreen(
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
  val showDialog = remember { mutableStateOf(true) }
  val isCoach = remember { mutableStateOf(false) }
  var isStopped by remember { mutableStateOf(false) }
  var durationAchieved by remember { mutableIntStateOf(0) }
  var count by remember { mutableIntStateOf(0) }
  var isResetEnabled by remember { mutableStateOf(false) }

  val graphData = getGraphData(workoutData, activity, isTimeDependent, reps)
  if (showDialog.value) {
    TrainCoachDialog(
        onRoleSelected = {
          isCoach.value = it
          showDialog.value = false
        },
        onDismiss = { showDialog.value = false })
  } else {
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
                        currentUser?.uid,
                        workoutViewModel,
                        activity,
                        durationAchieved,
                        count,
                        count)
                  },
                  onTimeUpdate = { elapsedTime -> durationAchieved = elapsedTime.toInt() })
            } else {
              Text(
                  stringResource(id = R.string.time_up_message),
                  modifier = Modifier.testTag("TimeUpText"))
            }

            if (!isResetEnabled) {
              Button(
                  onClick = {
                    isStopped = true
                    isResetEnabled = true
                    addExerciseToWorkout(
                        currentUser?.uid,
                        workoutViewModel,
                        activity,
                        durationAchieved,
                        count,
                        count)
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
                  modifier = Modifier.testTag("StopButton")) {
                    Text(
                        stringResource(R.string.stop_button_text),
                        color = PRINCIPLE_BACKGROUND_COLOR)
                  }
            } else {
              Button(
                  onClick = {
                    isStopped = false
                    isResetEnabled = false
                    durationAchieved = 0
                    count = 0
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
                  modifier = Modifier.testTag("ResetButton")) {
                    Text(
                        stringResource(id = R.string.reset_button_text),
                        color = PRINCIPLE_BACKGROUND_COLOR)
                  }
            }
          } else {
            Text(
                text = stringResource(id = R.string.counter_explanation),
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                modifier =
                    Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("CounterExplanation"),
                color = PRIMARY_TEXT_COLOR)
            AnimatedCounter(
                count,
                Modifier.testTag("CounterText"),
                androidx.compose.material3.MaterialTheme.typography.displayLarge)

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                  Button(
                      onClick = { if (count > 0) count-- },
                      colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
                      modifier = Modifier.testTag("DecrementButton")) {
                        Text(
                            stringResource(id = R.string.decrement_button_text),
                            color = PRINCIPLE_BACKGROUND_COLOR)
                      }

                  Button(
                      onClick = { count++ },
                      colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
                      modifier = Modifier.testTag("IncrementButton")) {
                        Text(
                            stringResource(id = R.string.increment_button_text),
                            color = PRINCIPLE_BACKGROUND_COLOR)
                      }
                }

            Button(
                onClick = {
                  addExerciseToWorkout(
                      currentUser?.uid, workoutViewModel, activity, durationAchieved, count, count)
                },
                colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
                modifier = Modifier.testTag("AddExerciseButton")) {
                  Text(
                      stringResource(id = R.string.end_exercise),
                      color = PRINCIPLE_BACKGROUND_COLOR)
                }
          }
          HorizontalDivider(
              Modifier.padding(vertical = 16.dp).testTag("Divider"), 1.dp, BORDER_COLOR)
          if (isCoach.value) {
            CoachView()
          } else {
            PerformanceHistoryGraph(graphData, Modifier.padding(16.dp))
          }
        }
  }
}

@Composable
fun TrainCoachDialog(onRoleSelected: (Boolean) -> Unit, onDismiss: () -> Unit) {
  val isCoach = remember { mutableStateOf(false) }

  AlertDialog(
      onDismissRequest = onDismiss,
      confirmButton = {
        Button(onClick = { onRoleSelected(isCoach.value) }) {
          Text(text = "Confirm", color = PRINCIPLE_BACKGROUND_COLOR)
        }
      },
      dismissButton = {
        Button(onClick = onDismiss) { Text(text = "Cancel", color = PRINCIPLE_BACKGROUND_COLOR) }
      },
      title = { Text(text = "Choose Your Role") },
      text = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Are you the Coach or the athlete ?")
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = if (isCoach.value) "Coach" else "Athlete")
                    Switch(
                        checked = isCoach.value,
                        onCheckedChange = { isCoach.value = it },
                        colors =
                            SwitchDefaults.colors(
                                checkedThumbColor = INTERACTION_COLOR_DARK,
                                checkedTrackColor = INTERACTION_COLOR_DARK))
                  }
            }
      })
}

@Composable
fun CoachView() {
  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Coach Comments:")
        TextField(value = "", onValueChange = {}, modifier = Modifier.fillMaxWidth())
      }
}
