package com.android.streetworkapp.ui.Progression

import android.annotation.SuppressLint
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.streetworkapp.model.user.User
import com.android.streetworkapp.model.user.UserRepository
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.progress.updateAndDisplayPoints
import com.android.streetworkapp.ui.theme.ColorPalette
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

class GamificationTest {

  private lateinit var navigationActions: NavigationActions

  private lateinit var repository: UserRepository
  private lateinit var userViewModel: UserViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    repository = mock()
    userViewModel = UserViewModel(repository)
    navigationActions = mock()

    userViewModel.setCurrentUser(User("test", "test", "test", 0, emptyList(), ""))
  }

  @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
  @Test
  fun updateAndDisplayPointsCallsIncreaseScore() = runTest {
    composeTestRule.setContent {
      val scope = rememberCoroutineScope()
      val snackbarHostState = remember { SnackbarHostState() }

      // basic scaffold with a button to use updateAndDisplayPoints
      Scaffold(
          snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                  Snackbar(actionColor = ColorPalette.INTERACTION_COLOR_DARK, snackbarData = data)
                })
          }) {
            Button(
                modifier = Modifier.testTag("buttonTestSnackbar"),
                onClick = {
                  updateAndDisplayPoints(
                      userViewModel, navigationActions, 10, scope, snackbarHostState)
                }) {}
          }
    }

    composeTestRule.onNodeWithTag("buttonTestSnackbar").performClick()

    org.mockito.kotlin.verify(repository).increaseUserScore(any(), any())
  }
}
