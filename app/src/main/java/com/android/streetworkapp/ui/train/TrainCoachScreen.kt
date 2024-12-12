package com.android.streetworkapp.ui.train

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.android.streetworkapp.model.workout.Comment
import com.android.streetworkapp.model.workout.RequestStatus
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.theme.ColorPalette.BORDER_COLOR
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK
import com.android.streetworkapp.ui.theme.ColorPalette.PRIMARY_TEXT_COLOR

private const val TAG = "TrainCoachScreen"

@Composable
fun TrainCoachScreen(
    activity: String,
    isTimeDependent: Boolean,
    reps: Int?,
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
  val athleteUid = acceptedRequest?.toUid ?: ""

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
      WaitingDialog(workoutViewModel, currentUserUid) { showWaitingDialog.value = false }
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
            athleteUid,
            paddingValues)
      } else {
        AthleteViewContent(
            sessionId ?: "",
            activity,
            isTimeDependent,
            reps,
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
    toUid: String,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
  val currentUserUid = userViewModel.currentUser.value?.uid ?: ""
  val currentUserName = userViewModel.currentUser.value?.username ?: ""
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
              Text(text = stringResource(id = R.string.coach_comments_title))

              // Display coach comments
              CoachCommentsSection(sessionId, workoutViewModel)

              // Input for coach message
              CoachMessageInput(workoutViewModel, sessionId, toUid, currentUserUid, currentUserName)
            }
      }
}

@Composable
fun AthleteViewContent(
    sessionId: String,
    activity: String,
    isTimeDependent: Boolean,
    reps: Int?,
    workoutViewModel: WorkoutViewModel,
    userViewModel: UserViewModel,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
  val workoutData by workoutViewModel.workoutData.collectAsState()

  val graphData = getGraphData(workoutData, activity, isTimeDependent, reps)

  Column(
      modifier =
          Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).testTag("AthleteViewScreen"),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween) {
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

        CommentVisualizer(sessionId, workoutViewModel)

        HorizontalDivider(Modifier.padding(vertical = 16.dp).testTag("Divider"), 1.dp, BORDER_COLOR)

        PerformanceHistoryGraph(graphData, Modifier.padding(16.dp))
      }
}

@Composable
fun CoachCommentsSection(sessionId: String, workoutViewModel: WorkoutViewModel) {
  val workoutSessions by workoutViewModel.workoutSessions.collectAsState(initial = emptyList())

  val session = workoutSessions?.find { it.sessionId == sessionId }
  val comments = session?.comments ?: emptyList()

  Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("CoachCommentsSection"),
      verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(id = R.string.coach_comments_title),
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = PRIMARY_TEXT_COLOR)

        comments.forEach { comment ->
          Text(
              text = "${comment.authorUid}: ${comment.text}",
              style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
              color = PRIMARY_TEXT_COLOR)
        }
      }
}

/** Composable function to display the coach message input. */
@Composable
fun CoachMessageInput(
    workoutViewModel: WorkoutViewModel,
    sessionId: String,
    toUid: String,
    currentUserUid: String,
    coachName: String
) {
  var message by remember { mutableStateOf("") }

  Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.Start) {
        TextField(
            value = message,
            onValueChange = { message = it },
            label = { Text(text = "Write a message as Coach ($coachName)") },
            modifier = Modifier.fillMaxWidth())

        Button(
            colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
            onClick = {
              if (message.isNotBlank()) {
                // Create a comment object and send it via ViewModel
                val comment =
                    Comment(authorUid = currentUserUid, text = "Coach ($coachName): $message")
                Log.d("DEBUGSWENT", "Adding comment to session: $sessionId")
                Log.d("DEBUGSWENT", "Comment: $comment")
                workoutViewModel.addCommentToSession(toUid, sessionId, comment)
                message = "" // Reset the message field
              }
            },
            modifier = Modifier.align(Alignment.End),
            enabled = message.isNotBlank()) {
              Text("Send")
            }
      }
}
