package com.android.streetworkapp.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK
import kotlin.math.absoluteValue

data class GraphData(val x: Float, val y: Float)

/**
 * Data class for configuring the properties of a graph.
 *
 * @property graphColor The color of the graph lines. Default is [INTERACTION_COLOR_DARK].
 * @property axisColor The color of the X and Y axes. Default is [Color.Black].
 * @property xUnitLabel The label for the X-axis. Default is "Time".
 * @property yUnitLabel The label for the Y-axis. Default is "Reps".
 * @property dataPoints A list of [GraphData] points (x, y) to plot on the graph. Default is an
 *   empty list.
 * @property strokeWidth The width of the graph lines in pixels. Default is 4f.
 * @property showDashedLines Determines whether dashed gridlines are shown. Default is true.
 * @property hoverFirstPart The first part of the hover text. Default is "Session".
 * @property hoverSecondPart The second part of the hover text. Default is "sec".
 */
class GraphConfiguration(
    var graphColor: Color = INTERACTION_COLOR_DARK,
    var axisColor: Color = Color.Black,
    var xUnitLabel: String = "Time",
    var yUnitLabel: String = "Reps",
    var dataPoints: List<GraphData> = emptyList(),
    var strokeWidth: Float = 4f,
    var showDashedLines: Boolean = true,
    var hoverFirstPart: String = "Session",
    var hoverSecondPart: String = "sec"
)

/**
 * A composable function that renders an interactive graph based on the provided configuration.
 *
 * @param modifier The [Modifier] to apply layout attributes such as size and padding. Default is an
 *   empty modifier.
 * @param graphConfiguration The [GraphConfiguration] object containing the graph's settings
 */
@Composable
fun Graph(modifier: Modifier = Modifier, graphConfiguration: GraphConfiguration) {
  val graphColor = graphConfiguration.graphColor
  val axisColor = graphConfiguration.axisColor
  val dataPoints = graphConfiguration.dataPoints
  val xUnitLabel = graphConfiguration.xUnitLabel
  val yUnitLabel = graphConfiguration.yUnitLabel
  val strokeWidth = graphConfiguration.strokeWidth
  val hoverfirstpart = graphConfiguration.hoverFirstPart
  val hoversecondpart = graphConfiguration.hoverSecondPart

  // State variables for hover functionality
  var hoverPosition by remember { mutableStateOf<Offset?>(null) }
  var hoverValue by remember { mutableStateOf<String?>(null) }

  // Reset hover state when data points change
  LaunchedEffect(dataPoints) {
    hoverPosition = null
    hoverValue = null
  }

  if (dataPoints.isNotEmpty()) {
    val yValues = dataPoints.map { it.y }
    val yMin = yValues.minOrNull() ?: 0f
    val yMax = yValues.maxOrNull() ?: 1f

    Box(modifier = modifier.padding(16.dp)) {
      Canvas(
          modifier =
              Modifier.fillMaxSize().pointerInput(dataPoints) {
                detectTapGestures { offset ->
                  val width = size.width
                  val height = size.height
                  val xOffset = 32.dp.toPx()
                  val yOffset = 32.dp.toPx()
                  val graphWidth = width - xOffset * 2
                  val graphHeight = height - yOffset * 2

                  // Find the closest data point to the tap position
                  val closestPoint =
                      dataPoints.minByOrNull {
                        val x = xOffset + (it.x / (dataPoints.size - 1)) * graphWidth
                        val y =
                            yOffset + graphHeight - ((it.y - yMin) / (yMax - yMin)) * graphHeight
                        (x - offset.x).absoluteValue + (y - offset.y).absoluteValue
                      }

                  hoverPosition =
                      closestPoint?.let {
                        Offset(
                            xOffset + (it.x / (dataPoints.size - 1)) * graphWidth,
                            yOffset + graphHeight - ((it.y - yMin) / (yMax - yMin)) * graphHeight)
                      }
                  hoverValue =
                      closestPoint?.let { point ->
                        "$hoverfirstpart ${point.x.toInt() + 1}, ${point.y.toInt()} $hoversecondpart"
                      }
                }
              }) {
            val width = size.width
            val height = size.height
            val xOffset = 32.dp.toPx()
            val yOffset = 32.dp.toPx()
            val graphWidth = width - xOffset * 2
            val graphHeight = height - yOffset * 2
            val xSpacing = graphWidth / (dataPoints.size - 1).coerceAtLeast(1)

            // Draw X-axis
            drawLine(
                color = axisColor,
                start = Offset(xOffset, height - yOffset),
                end = Offset(width - xOffset, height - yOffset),
                strokeWidth = 2.dp.toPx())

            // Draw Y-axis
            drawLine(
                color = axisColor,
                start = Offset(xOffset, height - yOffset),
                end = Offset(xOffset, yOffset),
                strokeWidth = 2.dp.toPx())

            // Add triangle to X-axis tip
            val xTrianglePath =
                androidx.compose.ui.graphics.Path().apply {
                  moveTo(width - xOffset + 5.dp.toPx(), height - yOffset)
                  lineTo(width - xOffset - 5.dp.toPx(), height - yOffset - 5.dp.toPx())
                  lineTo(width - xOffset - 5.dp.toPx(), height - yOffset + 5.dp.toPx())
                  close()
                }
            drawPath(path = xTrianglePath, color = axisColor)

            // Add triangle to Y-axis tip
            val yTrianglePath =
                androidx.compose.ui.graphics.Path().apply {
                  moveTo(xOffset, yOffset - 5.dp.toPx())
                  lineTo(xOffset - 5.dp.toPx(), yOffset + 5.dp.toPx())
                  lineTo(xOffset + 5.dp.toPx(), yOffset + 5.dp.toPx())
                  close()
                }
            drawPath(path = yTrianglePath, color = axisColor)

            // Draw the graph line
            for (i in 1 until dataPoints.size) {
              val startX = xOffset + xSpacing * (i - 1)
              val startY =
                  yOffset + graphHeight -
                      ((dataPoints[i - 1].y - yMin) / (yMax - yMin)) * graphHeight
              val endX = xOffset + xSpacing * i
              val endY =
                  yOffset + graphHeight - ((dataPoints[i].y - yMin) / (yMax - yMin)) * graphHeight

              drawLine(
                  color = graphColor,
                  start = Offset(startX, startY),
                  end = Offset(endX, endY),
                  strokeWidth = strokeWidth)
            }

            // Draw hover indicator
            hoverPosition?.let { position ->
              drawCircle(color = graphColor, radius = 5.dp.toPx(), center = position)
            }
          }

      // Display hover value relative to hoverPosition
      hoverPosition?.let { position ->
        hoverValue?.let { value ->
          Box(
              modifier =
                  Modifier.absoluteOffset(
                          x = with(LocalDensity.current) { position.x.toDp() - 50.dp },
                          y = with(LocalDensity.current) { position.y.toDp() - 30.dp })
                      .padding(8.dp)) {
                Text(text = value, color = Color.Black, modifier = Modifier.padding(4.dp))
              }
        }
      }

      // X-axis label
      Text(
          text = xUnitLabel,
          color = axisColor,
          modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp))

      // Y-axis label
      Text(
          text = yUnitLabel,
          color = axisColor,
          modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp).rotate(-90f))
    }
  }
}
