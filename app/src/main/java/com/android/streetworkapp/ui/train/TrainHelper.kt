package com.android.streetworkapp.ui.train

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.sample.R
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.PairingRequest
import com.android.streetworkapp.model.workout.RequestStatus
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK
import com.android.streetworkapp.ui.theme.ColorPalette.PRIMARY_TEXT_COLOR
import com.android.streetworkapp.ui.theme.ColorPalette.PRINCIPLE_BACKGROUND_COLOR

@Composable
fun TrainCoachDialog(onRoleSelected: (Boolean) -> Unit, onDismiss: () -> Unit) {
  val isCoach = remember { mutableStateOf(false) }

  AlertDialog(
      onDismissRequest = onDismiss,
      confirmButton = {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
            onClick = { onRoleSelected(isCoach.value) },
            modifier = Modifier.testTag("ConfirmButton")) {
              Text(
                  text = stringResource(id = R.string.alert_dialog_conf),
                  color = PRINCIPLE_BACKGROUND_COLOR)
            }
      },
      dismissButton = {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
            onClick = onDismiss,
            modifier = Modifier.testTag("CancelButton")) {
              Text(
                  text = stringResource(id = R.string.alert_dialog_cancel),
                  color = PRINCIPLE_BACKGROUND_COLOR)
            }
      },
      title = {
        Text(
            text = stringResource(id = R.string.role_selection_title),
            modifier = Modifier.testTag("DialogTitle"))
      },
      text = {
        Column(
            modifier = Modifier.fillMaxWidth().testTag("DialogContent"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = stringResource(id = R.string.role_selection_text))
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text =
                            if (isCoach.value) stringResource(id = R.string.coach)
                            else stringResource(id = R.string.athlete),
                        modifier = Modifier.testTag("RoleText"))
                    Switch(
                        checked = isCoach.value,
                        onCheckedChange = { isCoach.value = it },
                        colors =
                            SwitchDefaults.colors(
                                checkedThumbColor = INTERACTION_COLOR_DARK,
                                checkedTrackColor = INTERACTION_COLOR_DARK),
                        modifier = Modifier.testTag("RoleSwitch"))
                  }
            }
      })
}

@Composable
fun WaitingDialog(
    workoutViewModel: WorkoutViewModel,
    userViewModel: UserViewModel,
    currentUserUid: String,
    onDismiss: () -> Unit
) {
  val pairingRequests by workoutViewModel.pairingRequests.collectAsState()

  LaunchedEffect(currentUserUid) { workoutViewModel.observePairingRequests(currentUserUid) }

  AlertDialog(
      onDismissRequest = onDismiss,
      confirmButton = {},
      dismissButton = {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
            onClick = onDismiss,
            modifier = Modifier.testTag("CancelButton")) {
              Text(
                  text = stringResource(id = R.string.alert_dialog_cancel),
                  color = PRINCIPLE_BACKGROUND_COLOR)
            }
      },
      title = {
        Text(
            text = stringResource(id = R.string.pairing_requests_title),
            modifier = Modifier.testTag("DialogTitle"))
      },
      text = {
        if (pairingRequests.isNullOrEmpty()) {
          Text(
              text = stringResource(id = R.string.no_pairing_requests),
              modifier = Modifier.testTag("NoRequestsText"))
        } else {
          Column(
              verticalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.testTag("RequestsList")) {
                pairingRequests
                    ?.filter { it.toUid == currentUserUid && it.status == RequestStatus.PENDING }
                    ?.forEach { request ->
                      PairingRequestItem(
                          request = request,
                          onAccept = {
                            workoutViewModel.respondToPairingRequest(
                                request.requestId, true, currentUserUid, request.fromUid)
                          },
                          onReject = {
                            workoutViewModel.respondToPairingRequest(
                                request.requestId, false, currentUserUid, request.fromUid)
                          },
                          userViewModel)
                    }
              }
        }
      })
}

@Composable
fun PairingRequestItem(
    request: PairingRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    userViewModel: UserViewModel
) {
  userViewModel.getUserByUid(request.fromUid)
  val username = userViewModel.user.collectAsState().value?.username
  Column(
      modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("RequestItem"),
      verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(id = R.string.request_from, username ?: ""),
            modifier = Modifier.testTag("RequestFrom"))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Button(
              onClick = onAccept,
              colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
              modifier = Modifier.testTag("AcceptButton")) {
                Text(
                    text = stringResource(id = R.string.accept), color = PRINCIPLE_BACKGROUND_COLOR)
              }
          Button(
              onClick = onReject,
              colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
              modifier = Modifier.testTag("RejectButton")) {
                Text(
                    text = stringResource(id = R.string.reject), color = PRINCIPLE_BACKGROUND_COLOR)
              }
        }
      }
}

@Composable
fun FriendListDialog(
    userViewModel: UserViewModel,
    currentUserUid: String,
    workoutViewModel: WorkoutViewModel,
    onDismiss: () -> Unit
) {
  val friends = userViewModel.friends.collectAsState().value
  userViewModel.getFriendsByUid(currentUserUid)

  AlertDialog(
      onDismissRequest = onDismiss,
      confirmButton = {},
      dismissButton = {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK),
            onClick = onDismiss,
            modifier = Modifier.testTag("CancelButton")) {
              Text(
                  text = stringResource(id = R.string.alert_dialog_cancel),
                  color = PRINCIPLE_BACKGROUND_COLOR)
            }
      },
      title = {
        Text(
            text = stringResource(id = R.string.select_friends),
            modifier = Modifier.testTag("DialogTitle"))
      },
      text = {
        if (friends.isNotEmpty()) {
          Column(
              modifier = Modifier.fillMaxWidth().testTag("FriendsList"),
              verticalArrangement = Arrangement.spacedBy(8.dp)) {
                friends.forEach { friend ->
                  if (friend != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth().testTag("FriendItem"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                          Text(text = friend.username, modifier = Modifier.testTag("FriendName"))
                          Button(
                              onClick = {
                                workoutViewModel.sendPairingRequest(
                                    fromUid = currentUserUid, toUid = friend.uid)
                                workoutViewModel.observePairingRequests(currentUserUid)
                                onDismiss()
                              },
                              colors =
                                  ButtonDefaults.buttonColors(
                                      containerColor = INTERACTION_COLOR_DARK,
                                      contentColor = PRINCIPLE_BACKGROUND_COLOR),
                              modifier = Modifier.testTag("AddFriendButton")) {
                                Text(text = stringResource(id = R.string.request_ask))
                              }
                        }
                  }
                }
              }
        } else {
          Text(
              text = stringResource(id = R.string.no_friends_available),
              modifier = Modifier.testTag("NoFriendsText"))
        }
      })
}

@Composable
fun CommentVisualizer(sessionId: String, workoutViewModel: WorkoutViewModel) {
  val workoutSessions by workoutViewModel.workoutSessions.collectAsState(initial = emptyList())
  val session = workoutSessions?.find { it.sessionId == sessionId }
  val comments = session?.comments ?: emptyList()

  Column(
      modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("CommentVisualizer"),
      verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(id = R.string.comments_title),
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = PRIMARY_TEXT_COLOR)

        if (comments.isEmpty()) {
          Text(
              text = stringResource(id = R.string.no_comments),
              style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
              color = PRIMARY_TEXT_COLOR)
        } else {
          comments.forEach { comment ->
            Text(
                text = "${comment.authorUid}: ${comment.text}",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = PRIMARY_TEXT_COLOR)
          }
        }
      }
}
