package com.android.streetworkapp.ui.utils

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
import com.android.streetworkapp.ui.theme.ColorPalette

/**
 * This function displays the a customized dialog with callback functions. Displayed depending on
 * the showDialog value. Will hide itself once interacted with.
 *
 * @param showDialog - MutableState to show the dialog
 * @param dialogType - Type of dialog to display ("Settings", "Rating", etc.)
 * @param Content - Content of the dialog
 * @param onSubmit - Submission callback
 * @param onDismiss - Dismiss callback
 */
@Composable
fun CustomDialog(
    showDialog: MutableState<Boolean>,
    dialogType: String = "",
    Content: @Composable () -> Unit = {},
    onSubmit: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
  val context = LocalContext.current

  if (showDialog.value) {
    AlertDialog(
        modifier = Modifier.testTag(dialogType + "Dialog"),
        onDismissRequest = {
          onDismiss()
          showDialog.value = false
        },
        confirmButton = {
          TextButton(
              onClick = {
                onSubmit()
                Toast.makeText(context, "$dialogType Dialog submitted", Toast.LENGTH_SHORT).show()
                showDialog.value = false
              },
              modifier = Modifier.testTag(dialogType + "DialogSubmitButton")) {
                Text("Submit", color = ColorPalette.SECONDARY_TEXT_COLOR)
              }
        },
        dismissButton = {
          TextButton(
              onClick = {
                onDismiss()
                showDialog.value = false
              },
              modifier = Modifier.testTag(dialogType + "DialogCancelButton")) {
                Text("Cancel", color = Color.Red)
              }
        },
        title = {
          Text(
              "Your $dialogType",
              color = ColorPalette.PRIMARY_TEXT_COLOR,
              modifier = Modifier.testTag(dialogType + "DialogTitle"))
        },
        text = { Content() },
        properties =
            DialogProperties(
                dismissOnClickOutside = true) // Makes dialog dismissible by clicking outside
        )
  }
}
