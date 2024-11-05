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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.streetworkapp.ui.navigation.NavigationActions

//TODO: we need to define a global color palette somewhere
const val PROGRESSION_COLOR_BLUE = 0xFF007BFF
const val PROGRESSION_COLOR_GRAY = 0xFFDDDDDD

data class Achievement(
    val icon: Int, // Resource ID for the icon
    val title: String,
    val tags: List<String>,
    val description: String
)

val sampleAchievements = listOf(
    Achievement(
        icon = R.drawable.park_default, // Replace with actual resource ID
        title = "Novice Explorer",
        tags = listOf("Exploration", "Beginner"),
        description = "Granted for completing your first adventure in the wild."
    ),
    Achievement(
        icon = R.drawable.park_default, // Replace with actual resource ID
        title = "First Place First Place First Place First Place First Place First Place First Place First Place",
        tags = listOf("Victory", "Competition", "Gold", "Gold", "Victory", "Competition", "Gold", "Gold", "Victory", "Competition", "Gold", "Gold"),
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
    ),
    Achievement(
        icon = R.drawable.park_default, // Replace with actual resource ID
        title = "Novice Explorer",
        tags = listOf("Exploration", "Beginner"),
        description = "Granted for completing your first adventure in the wild."
    ),
    Achievement(
        icon = R.drawable.park_default, // Replace with actual resource ID
        title = "Master of the Craft",
        tags = listOf("Crafting", "Expert"),
        description = "Recognized for mastering a complex crafting skill."
    ),
    Achievement(
        icon = R.drawable.park_default, // Replace with actual resource ID
        title = "Team Player",
        tags = listOf("Collaboration", "Teamwork"),
        description = "Earned by contributing significantly to a team project."
    )
)

@Composable
fun ProgressScreen(navigationActions: NavigationActions, paddingValues: PaddingValues = PaddingValues(0.dp)) {
    val progressBarSize = 145.dp

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = progressBarSize*0.8f),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        item {
            // Title Text Above the Progress Bar
            Text(
                text = "Progress to reach next level",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressBar(
                progress = 0.75f,
                progressBarSize
            ) // Set progress as a decimal (e.g., 75% = 0.75)

            Spacer(modifier = Modifier.height(60.dp))
        }

        item {
            // Metrics Section
            Text(
                text = "Metrics",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                color = Color.Black,
                modifier = Modifier
                    .padding(start = 20.dp, bottom = 8.dp)
                    .fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetricCard(label = "Total score", value = "4'321")
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
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            )
        }

        items(sampleAchievements) { achievement ->
            AchievementItem(achievement)
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
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
        modifier = Modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val sweepAngle = 360 * progress

            // Background Arc
            drawArc(
                color = Color(PROGRESSION_COLOR_GRAY),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // Foreground Arc
            drawArc(
                color = Color(PROGRESSION_COLOR_BLUE),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        // Percentage Text
        Text(
            text = "${(progress * 100).toInt()}%",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

/**
 * Cards under the progression bar (i.e Total score etc...)
 */
@Composable
fun MetricCard(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.border(
            width = 1.dp,
            color = Color.LightGray,
            shape = RoundedCornerShape(8.dp)
        ).padding(top = 12.dp, bottom = 12.dp, start = 10.dp, end = 36.dp )
    ) {
        Text(text = label, fontSize = 13.sp, color = Color.Gray)
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AchievementItem(achievement: Achievement) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.LightGray, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = achievement.icon),
                contentDescription = achievement.title,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape) //Clip the image to be circular
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Achievement Title
            Text(
                text = achievement.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Tags Row
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                achievement.tags.forEach { tag ->
                    Text(
                        text = tag,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Achievement Description
            Text(
                text = achievement.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun AchievementsList(achievements: List<Achievement>) {
    Column(modifier = Modifier.fillMaxSize()) {
        // "Achievements" Header
        Text(
            text = "Achievements",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.Start)
        )

        // LazyColumn for scrolling through achievements
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(achievements) { achievement ->
                AchievementItem(achievement)
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            }
        }
    }
}