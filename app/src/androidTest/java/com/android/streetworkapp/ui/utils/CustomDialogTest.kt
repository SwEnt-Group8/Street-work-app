package com.android.streetworkapp.ui.utils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class CustomDialogTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var showDialog: MutableState<Boolean>
  private var dialogType = "Type"

  @Composable
  private fun SetUpCustomDialog(
      showDialog: MutableState<Boolean>,
      onSubmit: () -> Unit = {},
      onDismiss: () -> Unit = {}
  ) {
    CustomDialog(
        showDialog,
        dialogType,
        Content = { Text("Content", modifier = Modifier.testTag("content")) },
        onSubmit = onSubmit,
        onDismiss = onDismiss)
  }

  @Test
  fun isDialogCorrectlyDisplayed() {
    showDialog = mutableStateOf(false)

    composeTestRule.setContent { SetUpCustomDialog(showDialog) }

    val dialog = composeTestRule.onNodeWithTag(dialogType + "Dialog")

    dialog.assertIsNotDisplayed()

    showDialog.value = true
    composeTestRule.waitForIdle() // Wait for recomposition

    dialog.assertIsDisplayed()

    // Title is displayed
    composeTestRule
        .onNodeWithTag(dialogType + "DialogTitle")
        .assertIsDisplayed()
        .assertTextEquals("Your $dialogType")

    // Submit button is displayed
    composeTestRule
        .onNodeWithTag(dialogType + "DialogSubmitButton")
        .assertIsDisplayed()
        .assertHasClickAction()
        .assertTextEquals("Submit")

    // Cancel Button is displayed
    composeTestRule
        .onNodeWithTag(dialogType + "DialogCancelButton")
        .assertIsDisplayed()
        .assertHasClickAction()
        .assertTextEquals("Cancel")

    // Content is displayed
    composeTestRule.onNodeWithTag("content").assertIsDisplayed()
  }

  @Test
  fun isDialogCorrectlyClosedByCancelling() {
    // Cancelling will close the dialog and call the onDismiss function

    showDialog = mutableStateOf(true)
    val onDismissCalled = mutableStateOf(false)

    composeTestRule.setContent {
      SetUpCustomDialog(showDialog, onDismiss = { onDismissCalled.value = true })
    }

    composeTestRule.waitForIdle() // Wait for recomposition
    composeTestRule.onNodeWithTag(dialogType + "DialogCancelButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag(dialogType + "Dialog").assertIsNotDisplayed()
    assert(onDismissCalled.value)
  }

  @Test
  fun isDialogCorrectlyClosedBySubmitting() {
    // Submitting will close the dialog + call the onSubmit function
    showDialog = mutableStateOf(true)
    val onSubmitCalled = mutableStateOf(false)

    composeTestRule.setContent {
      SetUpCustomDialog(showDialog, onSubmit = { onSubmitCalled.value = true })
    }

    composeTestRule.waitForIdle() // Wait for recomposition
    composeTestRule.onNodeWithTag(dialogType + "DialogSubmitButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag(dialogType + "Dialog").assertIsNotDisplayed()
    assert(onSubmitCalled.value)
  }
}
