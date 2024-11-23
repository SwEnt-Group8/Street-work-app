package com.android.streetworkapp.ui.train

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK
import com.android.streetworkapp.utils.Graph
import com.android.streetworkapp.utils.GraphConfiguration
import com.android.streetworkapp.utils.GraphData

@Composable
fun TrainHubScreen(
    navigationActions: NavigationActions,
    workoutViewModel: WorkoutViewModel,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
  Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
    // Section: Choose Your Role
    Text(
        text = "Choose your role",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp))

    RoleSelectionRow(
        roles = listOf("Solo", "Coach", "Challenge"),
        onRoleSelected = { /* TODO: Handle Role Selection */})

    Spacer(modifier = Modifier.height(32.dp))

    // Section: Choose Your Activity
    Text(
        text = "Choose your activity",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp))

    ActivitySelectionGrid(
        activities = listOf("Push-ups", "Planks"),
        onActivitySelected = { /* TODO: Handle Activity Selection */})
    SampleGraphDisplay()
  }
}

@Composable
fun RoleSelectionRow(roles: List<String>, onRoleSelected: (String) -> Unit) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
    roles.forEach { role -> RoleItem(role = role, onClick = { onRoleSelected(role) }) }
  }
}

@Composable
fun RoleItem(role: String, onClick: () -> Unit) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(horizontal = 8.dp)) {
        Button(
            onClick = onClick,
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {}

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = role, fontSize = 14.sp, fontWeight = FontWeight.Medium)
      }
}

@Composable
fun ActivitySelectionGrid(activities: List<String>, onActivitySelected: (String) -> Unit) {
  Column {
    activities.chunked(4).forEach { row ->
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        row.forEach { activity ->
          ActivityItem(activity = activity, onClick = { onActivitySelected(activity) })
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
fun ActivityItem(activity: String, onClick: () -> Unit) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(horizontal = 8.dp)) {
        Button(
            onClick = onClick,
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {}

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = activity, fontSize = 14.sp, fontWeight = FontWeight.Medium)
      }
}

@Composable
fun SampleGraphDisplay() {
  val graphData =
      listOf(
          GraphData(x = 0f, y = 1f),
          GraphData(x = 1f, y = 2f),
          GraphData(x = 2f, y = 4f),
          GraphData(x = 3f, y = 3f),
          GraphData(x = 4f, y = 5f))

  Graph(
      modifier = Modifier.fillMaxWidth().height(200.dp),
      graphConfiguration =
          GraphConfiguration(
              graphColor = INTERACTION_COLOR_DARK,
              xUnitLabel = "Time (s)",
              yUnitLabel = "Reps",
              dataPoints = graphData,
              strokeWidth = 2f))
}
