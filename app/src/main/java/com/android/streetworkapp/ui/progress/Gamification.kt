package com.android.streetworkapp.ui.progress

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.navigation.TopLevelDestinations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Use this function to give points to the user and to display it with a SnackBar.
 *
 * @param userViewModel: The userViewModel
 * @param navigationActions: Used to go see the progression screen.
 * @param points: Points obtained by the user.
 * @param scope: Used to display the SnackBar.
 * @param snackbarHostState: Used to display the SnackBar.
 */
fun updateAndDisplayPoints(
    userViewModel: UserViewModel,
    navigationActions: NavigationActions,
    points: Int,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {

  userViewModel.currentUser.value?.let { userViewModel.increaseUserScore(it.uid, points) }
  scope.launch {
    val result =
        snackbarHostState.showSnackbar(
            message = "You obtained $points Points!",
            actionLabel = "See Progression",
            withDismissAction = true,
            duration = SnackbarDuration.Short)

    when (result) {
      SnackbarResult.Dismissed -> {}
      SnackbarResult.ActionPerformed -> {
        navigationActions.navigateTo(TopLevelDestinations.PROGRESSION)
      }
    }
  }
}
