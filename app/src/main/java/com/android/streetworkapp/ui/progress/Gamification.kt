package com.android.streetworkapp.ui.progress

import androidx.compose.material3.SnackbarHostState
import com.android.streetworkapp.model.user.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun updateAndDisplayPoints(
    userViewModel: UserViewModel,
    points: Int,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {

  userViewModel.currentUser.value?.let { userViewModel.increaseUserScore(it.uid, points) }
  scope.launch { snackbarHostState.showSnackbar("Snackbar") }
}
