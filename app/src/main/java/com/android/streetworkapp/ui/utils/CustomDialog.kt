package com.android.streetworkapp.ui.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.DialogProperties
import com.android.sample.R
import com.android.streetworkapp.ui.theme.ColorPalette

/**
 * This function displays the customized dialog with callback functions. Displayed depending on the
 * showDialog value. Will hide itself once interacted with.
 *
 * @param showDialog - MutableState to show the dialog
 * @param dialogType - Type of dialog (QUERY, INFO, CONFIRM)
 * @param tag - Describe the instance ("Settings", "Rating", etc.)
 * @param title - Title of the dialog
 * @param Content - Content of the dialog
 * @param onSubmit - Submission callback
 * @param onDismiss - Dismiss callback
 */
@Composable
fun CustomDialog(
    showDialog: MutableState<Boolean>,
    dialogType: DialogType = DialogType.QUERY,
    tag: String = "",
    title: String = "Your $tag",
    Content: @Composable () -> Unit = {},
    onSubmit: () -> Unit = {},
    onDismiss: () -> Unit = {},
    verbose: Boolean = false
) {

  if (showDialog.value) {
    AlertDialog(
        modifier = Modifier.testTag("${tag}Dialog"),
        onDismissRequest = {
          onDismiss()
          showDialog.value = false
        },
        confirmButton = { HandleConfirmButton(dialogType, tag, showDialog, onSubmit, verbose) },
        dismissButton = { HandleDismissButton(dialogType, tag, showDialog, onDismiss, verbose) },
        title = {
          Text(
              title,
              color = ColorPalette.PRIMARY_TEXT_COLOR,
              modifier = Modifier.testTag("${tag}DialogTitle"))
        },
        text = { Content() },
        properties =
            DialogProperties(
                dismissOnClickOutside = true) // Makes dialog dismissible by clicking outside
        )
  }
}

enum class DialogType {
  QUERY, // Have {submit, cancel} buttons + use {onSubmit, onDismiss}.
  INFO, // Only display the content + use onDismiss.
  CONFIRM; // Display the content + {confirm} button + {onSubmit, onDismiss}.

  fun shouldShowButton(button: ButtonType): Boolean {
    return when (button) {
      ButtonType.SUBMIT -> this == QUERY
      ButtonType.CANCEL -> this == QUERY
      ButtonType.CONFIRM -> this == CONFIRM
    }
  }
}

/** Enum class for the button types. Each type has a response, tag, and color. */
enum class ButtonType {
  SUBMIT,
  CANCEL,
  CONFIRM;

  fun response(context: Context): String {
    return when (this) {
      SUBMIT -> context.getString(R.string.DialogSubmitButtonResponse)
      CANCEL -> context.getString(R.string.DialogCancelButtonResponse)
      CONFIRM -> context.getString(R.string.DialogConfirmButtonResponse)
    }
  }

  fun tag(context: Context): String {
    return when (this) {
      SUBMIT -> context.getString(R.string.DialogSubmitButtonTitle)
      CANCEL -> context.getString(R.string.DialogCancelButtonTitle)
      CONFIRM -> context.getString(R.string.DialogConfirmButtonTitle)
    }
  }

  fun color(): Color {
    return when (this) {
      CONFIRM -> Color.Red
      CANCEL -> Color.Red
      else -> ColorPalette.SECONDARY_TEXT_COLOR
    }
  }
}

/**
 * This function displays the button for the dialog. Will hide the dialog once interacted with.
 *
 * @param ButtonType - Type of button (SUBMIT, CANCEL, CONFIRM)
 * @param tag - Describe the instance of the dialog ("Settings", "Rating", etc.)
 * @param onSubmit - Submission callback
 * @param showDialog - MutableState to show the dialog
 * @param verbose - Whether to display a Toast message on interaction
 */
@Composable
fun DialogButton(
    ButtonType: ButtonType,
    tag: String,
    onSubmit: () -> Unit,
    showDialog: MutableState<Boolean>,
    verbose: Boolean
) {
  val context = LocalContext.current
  TextButton(
      onClick = {
        onSubmit()
        if (verbose)
            Toast.makeText(context, ButtonType.response(context), Toast.LENGTH_SHORT).show()
        showDialog.value = false
      },
      modifier = Modifier.testTag("${tag}Dialog${ButtonType.tag(context)}Button")) {
        Text(ButtonType.tag(context), color = ButtonType.color())
      }
}

/** Handles whether to display the confirm button or the submit button (or none). */
@Composable
fun HandleConfirmButton(
    dialogType: DialogType,
    dialogTag: String,
    showDialog: MutableState<Boolean>,
    onClick: () -> Unit,
    verbose: Boolean = false
) {
  if (dialogType.shouldShowButton(ButtonType.CONFIRM)) {
    DialogButton(ButtonType.CONFIRM, dialogTag, onClick, showDialog, true)
  } else if (dialogType.shouldShowButton(ButtonType.SUBMIT)) {
    DialogButton(ButtonType.SUBMIT, dialogTag, onClick, showDialog, verbose)
  }
}

/** Handles whether to display the cancel button (or none). */
@Composable
fun HandleDismissButton(
    dialogType: DialogType,
    dialogTag: String,
    showDialog: MutableState<Boolean>,
    onClick: () -> Unit,
    verbose: Boolean = false
) {
  if (dialogType.shouldShowButton(ButtonType.CANCEL)) {
    DialogButton(ButtonType.CANCEL, dialogTag, onClick, showDialog, verbose)
  }
}
