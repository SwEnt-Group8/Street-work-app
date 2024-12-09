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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.Exercise
import com.android.streetworkapp.model.workout.SessionType
import com.android.streetworkapp.model.workout.WorkoutData
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.theme.ColorPalette.BORDER_COLOR
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK
import com.android.streetworkapp.ui.theme.ColorPalette.PRIMARY_TEXT_COLOR
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

  val graphData = getGraphData(workoutData, activity, isTimeDependent, reps)

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
                      currentUser?.uid, workoutViewModel, activity, durationAchieved, count, count)
                },
                colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
                modifier = Modifier.testTag("StopButton")) {
                  Text(
                      stringResource(R.string.stop_button_text), color = PRINCIPLE_BACKGROUND_COLOR)
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
                Text(stringResource(id = R.string.end_exercise), color = PRINCIPLE_BACKGROUND_COLOR)
              }
        }
        HorizontalDivider(Modifier.padding(vertical = 16.dp).testTag("Divider"), 1.dp, BORDER_COLOR)

        PerformanceHistoryGraph(graphData, Modifier.padding(16.dp))
      }
}

/**
 * Displays the performance history graph.
 *
 * @param graphData The list of [GraphData] instances.
 * @param modifier The [Modifier] to apply layout attributes such as size and padding. Default is an
 *   empty modifier.
 * @param graphHeight The height of the graph. Default is 200.dp.
 */
@Composable
fun PerformanceHistoryGraph(
    graphData: List<GraphData>,
    modifier: Modifier = Modifier,
    graphHeight: Dp = 200.dp
) {
  if (graphData.isNotEmpty()) {
    Text(text = stringResource(id = R.string.performance_history))
    Box(modifier = modifier.fillMaxWidth().height(graphHeight)) {
      Graph(
          graphConfiguration =
              GraphConfiguration(
                  xUnitLabel = stringResource(id = R.string.x_axis_label_session),
                  yUnitLabel = stringResource(id = R.string.y_axis_label_total_reps),
                  dataPoints = graphData))
    }
  } else {
    Text(text = stringResource(id = R.string.no_performance_history))
  }
}

/**
 * Gets the graph data for the specified activity.
 *
 * @param workoutData The [WorkoutData] instance.
 * @param activity The activity name.
 * @param isTimeDependent Whether the activity is time-dependent.
 * @param reps The number of reps.
 * @return The list of [GraphData] instances.
 */
fun getGraphData(
    workoutData: WorkoutData?,
    activity: String,
    isTimeDependent: Boolean,
    reps: Int? = 0
): List<GraphData> {
  return workoutData
      ?.workoutSessions
      ?.filter { session -> session.exercises.any { it.name == activity } }
      ?.mapIndexed { index, session ->
        val sessionReps =
            if (!isTimeDependent) {
              session.exercises.find { it.name == activity }?.sets?.times(reps ?: 0) ?: 0
            } else {
              session.exercises.find { it.name == activity }?.duration?.toFloat() ?: 0f
            }
        GraphData(x = index.toFloat(), y = sessionReps.toFloat())
      } ?: emptyList()
}

/**
 * Adds an exercise to the workout.
 *
 * @param userId The user ID.
 * @param workoutViewModel The [WorkoutViewModel] instance.
 * @param activity The activity name.
 * @param duration The duration of the exercise.
 * @param reps The number of reps.
 * @param sets The number of sets.
 */
fun addExerciseToWorkout(
    userId: String?,
    workoutViewModel: WorkoutViewModel,
    activity: String,
    duration: Int?,
    reps: Int?,
    sets: Int?
) {
  if (!userId.isNullOrEmpty()) {

    workoutViewModel.getOrAddExerciseToWorkout(
        userId,
        "solo_${System.currentTimeMillis()}",
        Exercise(activity, reps, sets, duration = duration),
        SessionType.SOLO)
  }
}
