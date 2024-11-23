package com.android.streetworkapp.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK
import kotlin.math.abs

data class GraphData(val x: Float, val y: Float)

class GraphConfiguration(
    var graphColor: Color = Color.Blue,
    var axisColor: Color = Color.Black,
    var xUnitLabel: String = "Time",
    var yUnitLabel: String = "Reps",
    var dataPoints: List<GraphData> = emptyList(),
    var strokeWidth: Float = 4f,
    var showDashedLines: Boolean = true
)

@SuppressLint("DefaultLocale")
@Composable
fun Graph(modifier: Modifier = Modifier, graphConfiguration: GraphConfiguration) {
  val graphColor = graphConfiguration.graphColor
  val axisColor = graphConfiguration.axisColor
  val dataPoints = graphConfiguration.dataPoints
  val xUnitLabel = graphConfiguration.xUnitLabel
  val yUnitLabel = graphConfiguration.yUnitLabel
  val strokeWidth = graphConfiguration.strokeWidth

  var hoverPosition by remember { mutableStateOf<Offset?>(null) }
  var hoverValue by remember { mutableStateOf<String?>(null) }

  val density = LocalDensity.current

  if (dataPoints.isNotEmpty()) {
    val xValues = dataPoints.map { it.x }
    val yValues = dataPoints.map { it.y }
    val xMin = xValues.minOrNull() ?: 0f
    val xMax = xValues.maxOrNull() ?: 1f
    val yMin = yValues.minOrNull() ?: 0f
    val yMax = yValues.maxOrNull() ?: 1f

    Box(modifier = modifier.padding(16.dp)) {
      // Add X-axis and Y-axis labels as Text Composables
      Text(
          text = xUnitLabel,
          modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
          color = Color.Black)
      Text(
          text = yUnitLabel,
          modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp),
          color = Color.Black)

      Canvas(
          modifier =
              Modifier.fillMaxSize().pointerInput(Unit) {
                detectTapGestures { offset ->
                  val graphWidth = with(density) { size.width - 32.dp.toPx() }
                  val graphHeight = with(density) { size.height - 32.dp.toPx() }

                  // Find the closest point
                  val closestPoint =
                      dataPoints.minByOrNull {
                        val x =
                            ((it.x - xMin) / (xMax - xMin)) * graphWidth +
                                with(density) { 16.dp.toPx() }
                        val y =
                            graphHeight + with(density) { 16.dp.toPx() } -
                                ((it.y - yMin) / (yMax - yMin)) * graphHeight
                        abs(x - offset.x) + abs(y - offset.y)
                      }

                  hoverPosition =
                      closestPoint?.let {
                        Offset(
                            ((it.x - xMin) / (xMax - xMin)) * graphWidth +
                                with(density) { 16.dp.toPx() },
                            graphHeight + with(density) { 16.dp.toPx() } -
                                ((it.y - yMin) / (yMax - yMin)) * graphHeight)
                      }
                  hoverValue =
                      closestPoint?.let { point ->
                        "(${String.format("%.1f", point.x)}, ${String.format("%.1f", point.y)})"
                      }
                }
              }) {
            val width = with(density) { size.width - 32.dp.toPx() }
            val height = with(density) { size.height - 32.dp.toPx() }

            val xOffset = with(density) { 16.dp.toPx() }
            val yOffset = with(density) { 16.dp.toPx() }

            // Draw X and Y axes
            drawLine(
                color = axisColor,
                start = Offset(xOffset, height + yOffset),
                end = Offset(width + xOffset, height + yOffset),
                strokeWidth = with(density) { 2.dp.toPx() })
            drawLine(
                color = axisColor,
                start = Offset(xOffset, height + yOffset),
                end = Offset(xOffset, yOffset),
                strokeWidth = with(density) { 2.dp.toPx() })

            // Draw data lines
            for (i in 1 until dataPoints.size) {
              val start = dataPoints[i - 1]
              val end = dataPoints[i]

              val startX = ((start.x - xMin) / (xMax - xMin)) * width + xOffset
              val startY = height + yOffset - ((start.y - yMin) / (yMax - yMin)) * height

              val endX = ((end.x - xMin) / (xMax - xMin)) * width + xOffset
              val endY = height + yOffset - ((end.y - yMin) / (yMax - yMin)) * height

              drawLine(
                  color = graphColor,
                  start = Offset(startX, startY),
                  end = Offset(endX, endY),
                  strokeWidth = with(density) { strokeWidth.dp.toPx() },
                  cap = StrokeCap.Round)
            }

            // Draw hover position
            hoverPosition?.let { position ->
              drawCircle(
                  color = INTERACTION_COLOR_DARK,
                  radius = with(density) { 5.dp.toPx() },
                  center = position)
            }
          }
    }
  }
}
