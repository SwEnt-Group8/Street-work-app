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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.streetworkapp.model.progression.Achievement
import com.android.streetworkapp.model.progression.MedalsAchievement
import com.android.streetworkapp.model.progression.ProgressionViewModel
import com.android.streetworkapp.model.progression.SocialAchievement
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.theme.ColorPalette
import com.android.streetworkapp.utils.exerciseNameToIcon
import com.android.streetworkapp.utils.toFormattedString
import kotlinx.coroutines.flow.MutableStateFlow

object ProgressionScreenSettings {
  val PROGRESSION_COLOR_BLUE = Color(0xFF007BFF)
  val PROGRESSION_COLOR_GRAY = Color(0xFFDDDDDD)
  val ACHIEVEMEMENT_TYPE_SOCIAL = "SOCIAL"
  val progressBarSize = 145.dp
  val columnPadding = PaddingValues(0.dp, progressBarSize * 0.15f, 0.dp, 0.dp)
}

// Mutable dashboard state
private val uiState: MutableStateFlow<DashboardStateProgression> =
    MutableStateFlow(DashboardStateProgression.Training)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProgressScreen(
    navigationActions: NavigationActions, // Note: not used yet
    userViewModel: UserViewModel,
    progressionViewModel: ProgressionViewModel,
    workoutViewModel: WorkoutViewModel,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {

  val currentUser by userViewModel.currentUser.collectAsState()
  val currentProgression by progressionViewModel.currentProgression.collectAsState()

  val currentWorkout by workoutViewModel.workoutData.collectAsState()
  currentUser?.uid?.let { workoutViewModel.getOrAddWorkoutData(it) }

  LaunchedEffect(currentProgression) {
    currentUser?.let {
      progressionViewModel.getOrAddProgression(it.uid)
      progressionViewModel.checkAchievements(it.friends.size, it.score)
    }
  }

  val progressionPercentage = // in case of error set it to 0, otherwise score/currentGoal
      (if (currentUser == null || currentProgression.currentGoal == 0) 0f
      else currentUser?.score?.div(currentProgression.currentGoal.toFloat())) ?: 0f

  val scoreTextUnderCircularProgressBar = buildAnnotatedString {
    append("${currentUser?.score}")

    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
      append("/${currentProgression.currentGoal}")
    }
  }

  Box(modifier = Modifier.padding(paddingValues).testTag("progressionScreen")) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(ProgressionScreenSettings.columnPadding),
        horizontalAlignment = Alignment.CenterHorizontally) {
          item {
            // Title Text Above the Progress Bar
            Text(
                text = "Progress to reach next level",
                fontSize = 16.sp,
                color = ColorPalette.SECONDARY_TEXT_COLOR)

            Spacer(modifier = Modifier.height(26.dp))

            CircularProgressBar(
                progress = progressionPercentage, ProgressionScreenSettings.progressBarSize)

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = scoreTextUnderCircularProgressBar,
                modifier = Modifier.testTag("scoreTextUnderCircularProgressBar"),
                fontSize = 16.sp,
                color = Color.Gray)

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
                  value = "${currentUser?.score}",
                  testTagPrefix = "metricCardScore")

              MetricCard(
                  label = "Friends added",
                  value = "${currentUser?.friends?.size}",
                  testTagPrefix = "metricCardFriendsAdded")
            }

            Spacer(modifier = Modifier.height(15.dp))
          }

          item { DashBoardBarProgression() }

          item {
            when (uiState.collectAsState().value) {
              DashboardStateProgression.Achievement -> {
                currentUser?.let {
                  progressionViewModel.checkAchievements(it.friends.size, it.score)
                }

                AchievementColumn(currentProgression.achievements)
              }
              DashboardStateProgression.Training -> {

                currentWorkout?.workoutSessions?.forEach { workout ->
                  val date = workout.startTime.toFormattedString()
                  workout.exercises.forEach {
                    val newAchievement =
                        Achievement(
                            exerciseNameToIcon(it.name),
                            LocalContext.current.getString(R.string.Trained, it.name),
                            listOf("workout"),
                            date)

                    Box(modifier = Modifier.testTag("exerciseItem${it.name}")) {
                      AchievementItem(newAchievement, false)
                      HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                    }
                  }
                }
              }
            }
          }
        }
  }
}

@Composable
fun AchievementColumn(achievements: List<String>) {

  if (achievements.isEmpty()) {

    Text(
        text = LocalContext.current.getString(R.string.ProgressionEmptyAchievements),
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        color = ColorPalette.SECONDARY_TEXT_COLOR,
        modifier = Modifier.padding(top = 10.dp).testTag("emptyAchievementsText"))
  } else {

    Column {
      achievements.forEach { achievementName ->
        if (achievementName.contains(ProgressionScreenSettings.ACHIEVEMEMENT_TYPE_SOCIAL)) {
          val achievementEnum = enumValueOf<SocialAchievement>(achievementName)
          Box(modifier = Modifier.testTag("achievementItem$achievementName")) {
            AchievementItem(achievementEnum.achievement, false)
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
          }
        } else {

          val achievementEnum = enumValueOf<MedalsAchievement>(achievementName)
          Box(modifier = Modifier.testTag("achievementItem")) {
            AchievementItem(achievementEnum.achievement, false)
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
          }
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

          // Background Arc
          drawArc(
              color = ProgressionScreenSettings.PROGRESSION_COLOR_GRAY,
              startAngle = 0f,
              sweepAngle = 360f,
              useCenter = false,
              style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round))

          // Foreground Arc
          drawArc(
              color = ProgressionScreenSettings.PROGRESSION_COLOR_BLUE,
              startAngle = -90f,
              sweepAngle = 360 * progress,
              useCenter = false,
              style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round))
        }

        // Percentage Text
        Text(
            text = "${(progress * 100).toInt()}%",
            modifier = Modifier.testTag("percentageInsideCircularProgressBar"),
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = ColorPalette.PRIMARY_TEXT_COLOR)
      }
}

@Composable
fun MetricCard(label: String, value: String, testTagPrefix: String) {
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
              modifier = Modifier.testTag(testTagPrefix + "Value"),
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = ColorPalette.PRIMARY_TEXT_COLOR)
        }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AchievementItem(achievement: Achievement, navButton: Boolean) {
  Row(
      modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier =
                Modifier.size(64.dp).background(ColorPalette.BORDER_COLOR, shape = CircleShape),
            contentAlignment = Alignment.Center) {
              Image(
                  painter = painterResource(id = achievement.icon),
                  contentDescription = achievement.title,
                  contentScale = ContentScale.FillBounds,
                  modifier =
                      Modifier.fillMaxSize().clip(CircleShape) // Clip the image to be circular
                  )
            }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
          // Achievement Title
          Text(
              text = achievement.title,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = ColorPalette.PRIMARY_TEXT_COLOR)

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
                          Modifier.background(
                                  ColorPalette.BORDER_COLOR, shape = RoundedCornerShape(4.dp))
                              .padding(horizontal = 8.dp, vertical = 2.dp))
                }
              }

          Spacer(modifier = Modifier.height(4.dp))

          // Achievement Description
          Text(
              text = achievement.description,
              fontSize = 14.sp,
              color = ColorPalette.SECONDARY_TEXT_COLOR)
        }

        if (navButton) {
          Button(
              onClick = {
                // TODO: navigate to detail progression screen (not yet implemented)
              },
              modifier = Modifier.padding(horizontal = 4.dp).testTag("detailButton"),
              enabled = false, // Note: it is not enable until the corresponding screen is created
              colors = ColorPalette.BUTTON_COLOR) {
                Text(text = "Details")
              }
        }
      }
}

@Composable
fun DashBoardBarProgression() {
  NavigationBar(
      modifier = Modifier.testTag("dashboard").fillMaxWidth().height(56.dp),
      containerColor = ColorPalette.PRINCIPLE_BACKGROUND_COLOR) {
        val state = uiState.collectAsState().value

        NavigationBarItem(
            modifier = Modifier.testTag("TrainingTab"),
            icon = { Text("Training") },
            selected = state == DashboardStateProgression.Training,
            onClick = { uiState.value = DashboardStateProgression.Training },
            colors = ColorPalette.NAVIGATION_BAR_ITEM_COLORS)

        NavigationBarItem(
            modifier = Modifier.testTag("AchievementTab"),
            icon = { Text("Achievement") },
            selected = state == DashboardStateProgression.Achievement,
            onClick = { uiState.value = DashboardStateProgression.Achievement },
            colors = ColorPalette.NAVIGATION_BAR_ITEM_COLORS)
      }
}

/** Represents the different states of the progression dashboard */
sealed class DashboardStateProgression {
  data object Training : DashboardStateProgression()

  data object Achievement : DashboardStateProgression()
}
