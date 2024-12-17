package com.android.streetworkapp.ui.train

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.android.streetworkapp.model.workout.RequestStatus
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.theme.ColorPalette.BORDER_COLOR
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK
import com.android.streetworkapp.ui.theme.ColorPalette.PRIMARY_TEXT_COLOR
import com.android.streetworkapp.ui.theme.ColorPalette.PRINCIPLE_BACKGROUND_COLOR

private const val TAG = "TrainCoachScreen"

@Composable
fun TrainCoachScreen(
    activity: String,
    isTimeDependent: Boolean,
    reps: Int?,
    time: Int?,
    workoutViewModel: WorkoutViewModel,
    userViewModel: UserViewModel,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
  val currentUser by userViewModel.currentUser.collectAsState()
  val currentUserUid = currentUser?.uid ?: ""

  val showRoleDialog = remember { mutableStateOf(true) }
  val showFriendListDialog = remember { mutableStateOf(false) }
  val showWaitingDialog = remember { mutableStateOf(false) }
  var isAcceptedOnce = false
  val pairingRequests by workoutViewModel.pairingRequests.collectAsState()
  val workoutSessions by workoutViewModel.workoutSessions.collectAsState(initial = emptyList())

  val acceptedRequest =
      pairingRequests?.find {
        it.status == RequestStatus.ACCEPTED &&
            (it.fromUid == currentUserUid || it.toUid == currentUserUid)
      }

  val sessionId = acceptedRequest?.sessionId
  val currentSession = workoutSessions?.find { it.sessionId == sessionId }

  // Debugging state propagation
  Log.d(TAG, "Workout Sessions: ${workoutSessions?.map { it.sessionId }}")
  Log.d(TAG, "Accepted Request: $acceptedRequest")
  Log.d(TAG, "Current Session: $currentSession")

  LaunchedEffect(workoutSessions) { Log.d(TAG, "WorkoutSessions Updated: $workoutSessions") }
  // Observe pairing requests for the current user
  LaunchedEffect(currentUserUid) {
    Log.d(TAG, "Observing pairing requests for: $currentUserUid")
    workoutViewModel.observePairingRequests(currentUserUid)
  }

  // Observe workout sessions for the athlete
  LaunchedEffect(acceptedRequest?.toUid) {
    if (acceptedRequest != null) {
      Log.d(TAG, "Observing workout sessions for: ${acceptedRequest.toUid}")
      workoutViewModel.observeWorkoutSessions(acceptedRequest.toUid)
    }
  }

  // Handle accepted request and update UI states
  LaunchedEffect(acceptedRequest) {
    if (!isAcceptedOnce &&
        acceptedRequest != null &&
        acceptedRequest.status == RequestStatus.ACCEPTED) {
      Log.d(TAG, "Accepted Request Found. sessionId=$sessionId")
      showRoleDialog.value = false
      showFriendListDialog.value = false
      showWaitingDialog.value = false
      isAcceptedOnce = true
    }
  }

  // Show appropriate UI based on the current state
  when {
    showRoleDialog.value -> {
      Log.d(TAG, "Displaying Role Dialog Screen")
      TrainCoachDialog(
          onRoleSelected = { selectedCoach ->
            Log.d(TAG, "Role Selected: ${if (selectedCoach) "Coach" else "Athlete"}")
            showRoleDialog.value = false
            if (selectedCoach) showFriendListDialog.value = true else showWaitingDialog.value = true
          },
          onDismiss = { showRoleDialog.value = false })
    }
    showFriendListDialog.value -> {
      Log.d(TAG, "Displaying Friend List Dialog Screen")
      FriendListDialog(userViewModel, currentUserUid, workoutViewModel) {
        showFriendListDialog.value = false
      }
    }
    showWaitingDialog.value -> {
      Log.d(TAG, "Displaying Waiting Dialog Screen")
      WaitingDialog(workoutViewModel, userViewModel, currentUserUid) {
        showWaitingDialog.value = false
      }
    }
    currentSession != null -> {
      Log.d(TAG, "Displaying Session Screen for sessionId=${currentSession.sessionId}")
      if (acceptedRequest?.fromUid == currentUserUid) {
        CoachView(
            workoutViewModel,
            userViewModel,
            sessionId ?: "",
            activity,
            isTimeDependent,
            reps,
            paddingValues)
      } else {
        AthleteViewContent(
            sessionId ?: "",
            activity,
            isTimeDependent,
            reps,
            time,
            workoutViewModel,
            userViewModel,
            paddingValues)
      }
    }
    else -> {
      Log.d(TAG, "No UI state met. Displaying fallback content.")
      FallbackContent()
    }
  }
}

@Composable
fun FallbackContent() {
  Column(
      modifier = Modifier.fillMaxSize().padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Text("No session data available.")
      }
}

@Composable
fun CoachView(
    workoutViewModel: WorkoutViewModel,
    userViewModel: UserViewModel,
    sessionId: String,
    activity: String,
    isTimeDependent: Boolean,
    reps: Int?,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
  val currentUserUid = userViewModel.currentUser.value?.uid ?: ""
  var isStopped by remember { mutableStateOf(false) }
  var durationAchieved by remember { mutableIntStateOf(0) }
  var count by remember { mutableIntStateOf(0) }
  var isResetEnabled by remember { mutableStateOf(false) }

  Text(
      text =
          "debug: sessionId=$sessionId, activity=$activity, isTimeDependent=$isTimeDependent, reps=$reps, currentUserUid=$currentUserUid")

  Column(
      modifier =
          Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).testTag("CoachViewScreen"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween) {
        PerformanceHistoryGraph(
            getGraphData(
                workoutViewModel.workoutData.collectAsState().value,
                activity,
                isTimeDependent,
                reps),
            Modifier.padding(16.dp))

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              // Time-based activities
              if (isTimeDependent) {
                if (!isStopped) {
                  CircularTimer(
                      totalTime = (reps ?: 30).toFloat(),
                      onTimeUp = {
                        isStopped = true
                        durationAchieved = reps ?: 0
                        addExerciseToWorkout(
                            currentUserUid,
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
                            currentUserUid,
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
                // Repetition-based activities
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
                          colors =
                              ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
                          modifier = Modifier.testTag("DecrementButton")) {
                            Text(
                                stringResource(id = R.string.decrement_button_text),
                                color = PRINCIPLE_BACKGROUND_COLOR)
                          }

                      Button(
                          onClick = { count++ },
                          colors =
                              ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
                          modifier = Modifier.testTag("IncrementButton")) {
                            Text(
                                stringResource(id = R.string.increment_button_text),
                                color = PRINCIPLE_BACKGROUND_COLOR)
                          }
                    }

                Button(
                    onClick = {
                      addExerciseToWorkout(
                          currentUserUid,
                          workoutViewModel,
                          activity,
                          durationAchieved,
                          count,
                          count)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
                    modifier = Modifier.testTag("AddExerciseButton")) {
                      Text(
                          stringResource(id = R.string.end_exercise),
                          color = PRINCIPLE_BACKGROUND_COLOR)
                    }
              }
            }
      }
}

@Composable
fun AthleteViewContent(
    sessionId: String,
    activity: String,
    isTimeDependent: Boolean,
    reps: Int?,
    time: Int?,
    workoutViewModel: WorkoutViewModel,
    userViewModel: UserViewModel,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
  val currentUser by userViewModel.currentUser.collectAsState()
  val workoutData by workoutViewModel.workoutData.collectAsState()
  var isStopped by remember { mutableStateOf(false) }
  var durationAchieved by remember { mutableIntStateOf(0) }
  var count by remember { mutableIntStateOf(0) }

  val graphData = getGraphData(workoutData, activity, isTimeDependent, reps)
  Column(
      modifier =
          Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).testTag("AthleteViewScreen"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween) {
        // Display activity type
        if (isTimeDependent) {
          Text(
              text = stringResource(id = R.string.time_activity_in_progress),
              style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
              modifier = Modifier.padding(vertical = 8.dp).testTag("TimeActivityText"),
              color = PRIMARY_TEXT_COLOR)
        } else {
          Text(
              text = stringResource(id = R.string.reps_activity_in_progress),
              style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
              modifier = Modifier.padding(vertical = 8.dp).testTag("RepsActivityText"),
              color = PRIMARY_TEXT_COLOR)
        }

        // Display animated timer or counter based on activity type
        if (isTimeDependent) {
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
          AnimatedCounter(
              count = reps ?: 0,
              Modifier.testTag("CounterText"),
              style = androidx.compose.material3.MaterialTheme.typography.displayLarge)
        }

        // Add a horizontal divider
        HorizontalDivider(
            Modifier.padding(vertical = 16.dp).testTag("Divider"),
            thickness = 1.dp,
            color = BORDER_COLOR)

        // Display performance history graph
        PerformanceHistoryGraph(graphData, Modifier.padding(16.dp))
      }
}
