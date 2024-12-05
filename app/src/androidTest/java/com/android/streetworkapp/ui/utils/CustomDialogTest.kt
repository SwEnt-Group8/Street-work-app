package com.android.streetworkapp.ui.utils

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
  private var dialogTag = "Type"

  @Composable
  private fun SetUpCustomDialog(
      showDialog: MutableState<Boolean>,
      onSubmit: () -> Unit = {},
      onDismiss: () -> Unit = {}
  ) {
    CustomDialog(
        showDialog,
        DialogType.QUERY,
        tag = dialogTag,
        Content = { Text("Content", modifier = Modifier.testTag("content")) },
        onSubmit = onSubmit,
        onDismiss = onDismiss)
  }

  @Test
  fun isDialogCorrectlyDisplayed() {
    showDialog = mutableStateOf(false)

    composeTestRule.setContent { SetUpCustomDialog(showDialog) }

    val dialog = composeTestRule.onNodeWithTag(dialogTag + "Dialog")

    dialog.assertIsNotDisplayed()

    showDialog.value = true
    composeTestRule.waitForIdle() // Wait for recomposition

    dialog.assertIsDisplayed()

    // Title is displayed
    composeTestRule
        .onNodeWithTag(dialogTag + "DialogTitle")
        .assertIsDisplayed()
        .assertTextEquals("Your $dialogTag")

    // Submit button is displayed
    composeTestRule
        .onNodeWithTag(dialogTag + "DialogSubmitButton")
        .assertIsDisplayed()
        .assertHasClickAction()
        .assertTextEquals("Submit")

    // Cancel Button is displayed
    composeTestRule
        .onNodeWithTag(dialogTag + "DialogCancelButton")
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
    composeTestRule.onNodeWithTag(dialogTag + "DialogCancelButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag(dialogTag + "Dialog").assertIsNotDisplayed()
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
    composeTestRule.onNodeWithTag(dialogTag + "DialogSubmitButton").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag(dialogTag + "Dialog").assertIsNotDisplayed()
    assert(onSubmitCalled.value)
  }

  @Test
  fun isCustomTitleDisplayed() {
    // Custom title is displayed
    val customTitle = "Custom Title"
    showDialog = mutableStateOf(true)

    composeTestRule.setContent {
      CustomDialog(
          showDialog,
          DialogType.QUERY,
          tag = dialogTag,
          title = customTitle,
          Content = { Text("Content", modifier = Modifier.testTag("content")) })
    }

    composeTestRule.waitForIdle() // Wait for recomposition
    composeTestRule
        .onNodeWithTag(dialogTag + "DialogTitle")
        .assertIsDisplayed()
        .assertTextEquals(customTitle)
  }

  @Test
  fun isInfoTypeDialogCorrectlyDisplayed() {
    // Info type dialog is displayed correctly
    showDialog = mutableStateOf(true)

    composeTestRule.setContent {
      CustomDialog(
          showDialog,
          DialogType.INFO,
          tag = dialogTag,
          Content = { Text("Content", modifier = Modifier.testTag("content")) })
    }
    composeTestRule.waitForIdle()
    // Title is displayed
    composeTestRule
        .onNodeWithTag(dialogTag + "DialogTitle")
        .assertIsDisplayed()
        .assertTextEquals("Your $dialogTag")

    // Submit button should not be displayed
    composeTestRule.onNodeWithTag(dialogTag + "DialogSubmitButton").assertIsNotDisplayed()

    // Cancel button should not be displayed
    composeTestRule.onNodeWithTag(dialogTag + "DialogCancelButton").assertIsNotDisplayed()

    // Content is displayed
    composeTestRule.onNodeWithTag("content").assertIsDisplayed()
  }

  @Composable
  fun SetUpConfirmButtonHandler(
      dialogType: DialogType,
      tag: String,
      showDialog: MutableState<Boolean>,
      context: MutableState<Context?>,
      onClick: () -> Unit = {}
  ) {
    HandleConfirmButton(dialogType, tag, showDialog, onClick)
    context.value = LocalContext.current
  }

  @Test
  fun isHandleConfirmButtonWorkingCorrectlyForINFO() {
    val tag = "MyTag"
    val context: MutableState<Context?> = mutableStateOf(null)

    composeTestRule.setContent {
      SetUpConfirmButtonHandler(DialogType.INFO, tag, mutableStateOf(true), context)
    }

    val SUBMIT_BUTTON_TAG = "${tag}Dialog${ButtonType.SUBMIT.tag(context.value!!)}Button"
    val CONFIRM_BUTTON_TAG = "${tag}Dialog${ButtonType.CONFIRM.tag(context.value!!)}Button"
    val CANCEL_BUTTON_TAG = "${tag}Dialog${ButtonType.CANCEL.tag(context.value!!)}Button"

    // No buttons should be displayed in INFO
    composeTestRule.onNodeWithTag(SUBMIT_BUTTON_TAG).assertDoesNotExist()
    composeTestRule.onNodeWithTag(CONFIRM_BUTTON_TAG).assertDoesNotExist()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON_TAG).assertDoesNotExist()
  }

  @Test
  fun isHandleConfirmButtonWorkingCorrectlyForCONFIRM() {
    val tag = "MyTag"
    val showDialog = mutableStateOf(true)
    val submitted = mutableStateOf(false)
    val onSubmit = { submitted.value = true }
    val context: MutableState<Context?> = mutableStateOf(null)

    composeTestRule.setContent {
      SetUpConfirmButtonHandler(DialogType.CONFIRM, tag, showDialog, context, onSubmit)
    }

    val SUBMIT_BUTTON_TAG = "${tag}Dialog${ButtonType.SUBMIT.tag(context.value!!)}Button"
    val CONFIRM_BUTTON_TAG = "${tag}Dialog${ButtonType.CONFIRM.tag(context.value!!)}Button"
    val CANCEL_BUTTON_TAG = "${tag}Dialog${ButtonType.CANCEL.tag(context.value!!)}Button"

    // Confirm button should be displayed in CONFIRM
    composeTestRule.onNodeWithTag(SUBMIT_BUTTON_TAG).assertDoesNotExist()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON_TAG).assertDoesNotExist()
    composeTestRule
        .onNodeWithTag(CONFIRM_BUTTON_TAG)
        .assertIsDisplayed()
        .assertHasClickAction()
        .assertTextEquals(ButtonType.CONFIRM.tag(context.value!!))

    composeTestRule.onNodeWithTag(CONFIRM_BUTTON_TAG).performClick()
    composeTestRule.waitForIdle()
    assert(submitted.value)
    assert(!showDialog.value)
  }

  @Test
  fun isHandleConfirmButtonWorkingCorrectlyForQUERY() {
    val tag = "MyTag"
    val showDialog = mutableStateOf(true)
    val submitted = mutableStateOf(false)
    val onSubmit = { submitted.value = true }
    val context: MutableState<Context?> = mutableStateOf(null)

    composeTestRule.setContent {
      SetUpConfirmButtonHandler(DialogType.QUERY, tag, showDialog, context, onSubmit)
    }

    val SUBMIT_BUTTON_TAG = "${tag}Dialog${ButtonType.SUBMIT.tag(context.value!!)}Button"
    val CONFIRM_BUTTON_TAG = "${tag}Dialog${ButtonType.CONFIRM.tag(context.value!!)}Button"
    val CANCEL_BUTTON_TAG = "${tag}Dialog${ButtonType.CANCEL.tag(context.value!!)}Button"

    // Only submit button should be displayed in QUERY
    composeTestRule.onNodeWithTag(CONFIRM_BUTTON_TAG).assertDoesNotExist()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON_TAG).assertDoesNotExist()
    composeTestRule
        .onNodeWithTag(SUBMIT_BUTTON_TAG)
        .assertIsDisplayed()
        .assertHasClickAction()
        .assertTextEquals(ButtonType.SUBMIT.tag(context.value!!))

    composeTestRule.onNodeWithTag(SUBMIT_BUTTON_TAG).performClick()
    composeTestRule.waitForIdle()
    assert(submitted.value)
    assert(!showDialog.value)
  }

  @Composable
  fun SetUpDismissButtonHandler(
      dialogType: DialogType,
      tag: String,
      showDialog: MutableState<Boolean>,
      context: MutableState<Context?>,
      onClick: () -> Unit = {}
  ) {
    HandleDismissButton(dialogType, tag, showDialog, onClick)
    context.value = LocalContext.current
  }

  @Test
  fun isHandleDismissButtonWorkingCorrectlyForINFO() {
    val tag = "MyTag"
    val context: MutableState<Context?> = mutableStateOf(null)

    composeTestRule.setContent {
      SetUpDismissButtonHandler(DialogType.INFO, tag, mutableStateOf(true), context)
    }

    val SUBMIT_BUTTON_TAG = "${tag}Dialog${ButtonType.SUBMIT.tag(context.value!!)}Button"
    val CONFIRM_BUTTON_TAG = "${tag}Dialog${ButtonType.CONFIRM.tag(context.value!!)}Button"
    val CANCEL_BUTTON_TAG = "${tag}Dialog${ButtonType.CANCEL.tag(context.value!!)}Button"

    // No buttons should be displayed in INFO
    composeTestRule.onNodeWithTag(SUBMIT_BUTTON_TAG).assertDoesNotExist()
    composeTestRule.onNodeWithTag(CONFIRM_BUTTON_TAG).assertDoesNotExist()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON_TAG).assertDoesNotExist()
  }

  @Test
  fun isHandleDismissButtonWorkingCorrectlyForCONFRIM() {
    val tag = "MyTag"
    val context: MutableState<Context?> = mutableStateOf(null)

    composeTestRule.setContent {
      SetUpDismissButtonHandler(DialogType.CONFIRM, tag, mutableStateOf(true), context)
    }

    val SUBMIT_BUTTON_TAG = "${tag}Dialog${ButtonType.SUBMIT.tag(context.value!!)}Button"
    val CONFIRM_BUTTON_TAG = "${tag}Dialog${ButtonType.CONFIRM.tag(context.value!!)}Button"
    val CANCEL_BUTTON_TAG = "${tag}Dialog${ButtonType.CANCEL.tag(context.value!!)}Button"

    // No buttons should be displayed in CONFIRM
    composeTestRule.onNodeWithTag(SUBMIT_BUTTON_TAG).assertDoesNotExist()
    composeTestRule.onNodeWithTag(CONFIRM_BUTTON_TAG).assertDoesNotExist()
    composeTestRule.onNodeWithTag(CANCEL_BUTTON_TAG).assertDoesNotExist()
  }

  @Test
  fun isHandleDismissButtonWorkingCorrectlyForQUERRY() {
    val tag = "MyTag"
    val showDialog = mutableStateOf(true)
    val dismissed = mutableStateOf(false)
    val onDismiss = { dismissed.value = true }
    val context: MutableState<Context?> = mutableStateOf(null)

    composeTestRule.setContent {
      SetUpDismissButtonHandler(DialogType.QUERY, tag, showDialog, context, onDismiss)
    }

    val SUBMIT_BUTTON_TAG = "${tag}Dialog${ButtonType.SUBMIT.tag(context.value!!)}Button"
    val CONFIRM_BUTTON_TAG = "${tag}Dialog${ButtonType.CONFIRM.tag(context.value!!)}Button"
    val CANCEL_BUTTON_TAG = "${tag}Dialog${ButtonType.CANCEL.tag(context.value!!)}Button"

    // Only cancel button should be displayed in QUERY
    composeTestRule.onNodeWithTag(SUBMIT_BUTTON_TAG).assertDoesNotExist()
    composeTestRule.onNodeWithTag(CONFIRM_BUTTON_TAG).assertDoesNotExist()
    composeTestRule
        .onNodeWithTag(CANCEL_BUTTON_TAG)
        .assertIsDisplayed()
        .assertHasClickAction()
        .assertTextEquals(ButtonType.CANCEL.tag(context.value!!))

    composeTestRule.onNodeWithTag(CANCEL_BUTTON_TAG).performClick()
    composeTestRule.waitForIdle()
    assert(dismissed.value)
    assert(!showDialog.value)
  }
}
