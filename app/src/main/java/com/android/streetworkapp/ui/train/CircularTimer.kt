package com.android.streetworkapp.ui.train

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK
import com.android.streetworkapp.ui.theme.ColorPalette.PRIMARY_TEXT_COLOR
import com.android.streetworkapp.ui.theme.ColorPalette.SHADOW_GREY
import kotlinx.coroutines.delay

/**
 * The CircularTimer composable displays a circular timer that counts down from a specified time.
 */
@SuppressLint("DefaultLocale")
@Composable
fun CircularTimer(
    totalTime: Float = 30f,
    onTimeUp: () -> Unit = {},
    onTimeUpdate: (Float) -> Unit = {},
    onStop: (Float) -> Unit = {}
) {
  val startTime = remember { System.currentTimeMillis() }
  var timeRemaining by remember { mutableFloatStateOf(totalTime) }
  val progress = remember { Animatable(1f) }
  var isTimeUp by remember { mutableStateOf(false) }
  var isStopped by remember { mutableStateOf(false) }

  // Launch a timer effect
  LaunchedEffect(Unit) {
    while (timeRemaining > 0 && !isTimeUp && !isStopped) {
      val elapsedTime = (System.currentTimeMillis() - startTime) / 1000f
      timeRemaining = (totalTime - elapsedTime).coerceAtLeast(0f)
      progress.snapTo(timeRemaining / totalTime)
      onTimeUpdate(totalTime - timeRemaining) // Report elapsed time
      delay(16L)
    }

    if (!isTimeUp && !isStopped) {
      isTimeUp = true
      onTimeUp()
    }
  }

  Box(modifier = Modifier.size(200.dp).padding(16.dp), contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.size(200.dp)) {
      // Background circle
      drawArc(
          color = SHADOW_GREY,
          startAngle = -90f,
          sweepAngle = 360f,
          useCenter = false,
          style = androidx.compose.ui.graphics.drawscope.Stroke(12.dp.toPx()))

      // Progress circle
      drawArc(
          color = INTERACTION_COLOR_DARK,
          startAngle = -90f,
          sweepAngle = 360f * progress.value,
          useCenter = false,
          style =
              androidx.compose.ui.graphics.drawscope.Stroke(12.dp.toPx(), cap = StrokeCap.Round))
    }

    // Display remaining time or message when time is up
    Text(
        text = if (!isTimeUp) String.format("%.0fs", timeRemaining) else "Time's Up!",
        modifier = Modifier.testTag("TimeRemainingText"),
        color = PRIMARY_TEXT_COLOR,
        fontSize = 24.sp)
  }

  // Stop the timer when the user manually stops it
  if (isStopped) {
    onStop(totalTime - timeRemaining) // Pass the elapsed time when stopped
  }
}
