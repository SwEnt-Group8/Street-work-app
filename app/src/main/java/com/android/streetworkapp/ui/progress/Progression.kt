package com.android.streetworkapp.ui.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.theme.ColorPalette

val PROGRESSION_COLOR_BLUE = Color(0xFF007BFF)
val PROGRESSION_COLOR_GRAY = Color(0xFFDDDDDD)

data class Achievement(
    val icon: Int, // Resource ID for the icon
    val title: String,
    val tags: List<String>,
    val description: String
)

val sampleAchievements =
    listOf(
        Achievement(
            icon = R.drawable.park_default, // Replace with actual resource ID
            title = "Novice Explorer",
            tags = listOf("Exploration", "Beginner"),
            description = "Granted for completing your first adventure in the wild."),
        Achievement(
            icon = R.drawable.park_default, // Replace with actual resource ID
            title =
                "First Place First Place First Place First Place First Place First Place First Place First Place",
            tags =
                listOf(
                    "Victory",
                    "Competition",
                    "Gold",
                    "Gold",
                    "Victory",
                    "Competition",
                    "Gold",
                    "Gold",
                    "Victory",
                    "Competition",
                    "Gold",
                    "Gold"),
            description =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."),
        Achievement(
            icon = R.drawable.park_default, // Replace with actual resource ID
            title = "Novice Explorer",
            tags = listOf("Exploration", "Beginner"),
            description = "Granted for completing your first adventure in the wild."),
        Achievement(
            icon = R.drawable.park_default, // Replace with actual resource ID
            title = "Master of the Craft",
            tags = listOf("Crafting", "Expert"),
            description = "Recognized for mastering a complex crafting skill."),
        Achievement(
            icon = R.drawable.park_default, // Replace with actual resource ID
            title = "Team Player",
            tags = listOf("Collaboration", "Teamwork"),
            description = "Earned by contributing significantly to a team project."))

// note: I haven' tested big values in the metrics tabs, the assumption was that those shouldn't
// overflow the boxes either way (unless we put some stupid score system)
// I did test for the rest thought

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProgressScreen(
    navigationActions: NavigationActions,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
  val progressBarSize = 145.dp

  val scoreText = buildAnnotatedString {
    append("780")

    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) { append("/1000") }
  }

  Box(modifier = Modifier.padding(paddingValues).testTag("progressionScreen")) {
    LazyColumn(
        modifier =
            Modifier.fillMaxSize()
                .padding(PaddingValues(0.dp, progressBarSize * 0.15f, 0.dp, 0.dp)),
        horizontalAlignment = Alignment.CenterHorizontally) {
          item {
            // Title Text Above the Progress Bar
            Text(
                text = "Progress to reach next level",
                fontSize = 16.sp,
                color = ColorPalette.SECONDARY_TEXT_COLOR)

            Spacer(modifier = Modifier.height(26.dp))

            CircularProgressBar(progress = 0.78f, progressBarSize)

            Spacer(modifier = Modifier.height(15.dp))

            Text(text = scoreText, fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(40.dp))
          }

          item {
            Text(
                text = "Metrics",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorPalette.PRIMARY_TEXT_COLOR)
            FlowRow(
                modifier = Modifier.testTag("metricCards"),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            ) {
              MetricCard(
                  label = "Total score",
                  value = "4'321") // TODO: format text pulled from db to this style
              MetricCard(label = "Parks visited", value = "3")
              MetricCard(label = "Friends added", value = "9")
            }

            Spacer(modifier = Modifier.height(15.dp))
          }

          item {
            Text(
                text = "Achievements",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth())
          }

          itemsIndexed(sampleAchievements) { index, achievement ->
            Box(modifier = Modifier.testTag("achievementItem${index}")) {
              AchievementItem(achievement)
              HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            }
          }
        }
  }
}

@Composable
fun CircularProgressBar(
    progress: Float, // Value between 0 and 1
    size: Dp,
    strokeWidth: Dp = 12.dp,
) {
  Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.size(size).testTag("circularProgressBar")) {
        Canvas(modifier = Modifier.size(size)) {
          val sweepAngle = 360 * progress

          // Background Arc
          drawArc(
              color = PROGRESSION_COLOR_GRAY,
              startAngle = 0f,
              sweepAngle = 360f,
              useCenter = false,
              style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round))

          // Foreground Arc
          drawArc(
              color = PROGRESSION_COLOR_BLUE,
              startAngle = -90f,
              sweepAngle = sweepAngle,
              useCenter = false,
              style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round))
        }

        // Percentage Text
        Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = ColorPalette.PRIMARY_TEXT_COLOR)
      }
}

@Composable
fun MetricCard(label: String, value: String) {
  Box(modifier = Modifier.padding(vertical = 4.dp)) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier =
            Modifier.border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(8.dp))
                .size(120.dp, 50.dp)) {
          Text(text = label, fontSize = 13.sp, color = ColorPalette.SECONDARY_TEXT_COLOR)
          Text(
              text = value,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = ColorPalette.PRIMARY_TEXT_COLOR)
        }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AchievementItem(achievement: Achievement) {
  Row(
      modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(48.dp).background(Color.LightGray, shape = CircleShape),
            contentAlignment = Alignment.Center) {
              Image(
                  painter = painterResource(id = achievement.icon),
                  contentDescription = achievement.title,
                  modifier = Modifier.size(64.dp).clip(CircleShape) // Clip the image to be circular
                  )
            }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
          // Achievement Title
          Text(
              text = achievement.title,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = ColorPalette.PRIMARY_TEXT_COLOR
              )

          // Tags Row
          FlowRow(
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              verticalArrangement = Arrangement.spacedBy(5.dp),
              modifier = Modifier.padding(top = 4.dp)) {
                achievement.tags.forEach { tag ->
                  Text(
                      text = tag,
                      fontSize = 12.sp,
                      color = ColorPalette.SECONDARY_TEXT_COLOR,
                      modifier =
                          Modifier.background(ColorPalette.BORDER_COLOR, shape = RoundedCornerShape(4.dp))
                              .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
              }

          Spacer(modifier = Modifier.height(4.dp))

          // Achievement Description
          Text(
              text = achievement.description,
              fontSize = 14.sp,
              color = ColorPalette.SECONDARY_TEXT_COLOR)
        }
      }
}
