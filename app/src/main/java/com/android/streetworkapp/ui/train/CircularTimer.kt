package com.android.streetworkapp.ui.train

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK
import com.android.streetworkapp.ui.theme.ColorPalette.PRIMARY_TEXT_COLOR
import com.android.streetworkapp.ui.theme.ColorPalette.PRINCIPLE_BACKGROUND_COLOR
import kotlinx.coroutines.delay

/**
 * The CircularTimer composable displays a circular timer that counts down from a specified time.
 */
@Composable
fun CircularTimer(totalTime: Int = 30, onTimeUp: () -> Unit = {}) {
  var timeRemaining by remember { mutableStateOf(totalTime) }
  val progress = remember { Animatable(1f) }
  var isTimeUp by remember { mutableStateOf(false) }

  LaunchedEffect(key1 = timeRemaining) {
    if (timeRemaining > 0) {
      progress.animateTo(
          targetValue = timeRemaining / totalTime.toFloat(),
          animationSpec = tween(durationMillis = 1000) // Smooth animation for progress
          )
      delay(1000L)
      timeRemaining--
    } else if (!isTimeUp) {
      isTimeUp = true
      onTimeUp()
    }
  }

  Box(modifier = Modifier.size(200.dp).padding(16.dp), contentAlignment = Alignment.Center) {
    Canvas(modifier = Modifier.size(200.dp)) {
      // Background circle
      drawArc(
          color = PRINCIPLE_BACKGROUND_COLOR,
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
    if (!isTimeUp) {
      Text(text = "${timeRemaining}s", color = PRIMARY_TEXT_COLOR, fontSize = 24.sp)
    } else {
      Text(text = "Time's Up!", color = PRIMARY_TEXT_COLOR, fontSize = 24.sp)
    }
  }
}
