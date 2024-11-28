package com.android.streetworkapp.ui.train

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.streetworkapp.model.user.UserViewModel
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.theme.ColorPalette.BORDER_COLOR
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK

@Composable
fun TrainHubScreen(
    navigationActions: NavigationActions,
    workoutViewModel: WorkoutViewModel,
    userViewModel: UserViewModel,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
  var selectedType by remember { mutableStateOf<String?>(null) }
  var selectedActivity by remember { mutableStateOf<Pair<String, Boolean>?>(null) }

  val currentUser by userViewModel.currentUser.collectAsState()
  currentUser?.uid?.let { workoutViewModel.getOrAddWorkoutData(it) }

  BoxWithConstraints {
    val screenWidth = maxWidth
    val screenHeight = maxHeight

    val buttonHeight = if (screenHeight < 600.dp) 70.dp else 100.dp
    val buttonWidth = if (screenWidth < 360.dp) 80.dp else 100.dp
    val gridSpacing = if (screenWidth < 360.dp) 4.dp else 8.dp

    Column(
        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween) {
          Column(modifier = Modifier.fillMaxWidth()) {
            // Section: Choose Your Role
            Text(
                text = "Choose your training type",
                fontSize = if (screenWidth < 360.dp) 14.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp).testTag("RoleSelectionTitle"))

            RoleSelectionRow(
                roles = listOf("Solo", "Coach", "Challenge"),
                buttonWidth = buttonWidth,
                buttonHeight = buttonHeight,
                gridSpacing = gridSpacing,
                onRoleSelected = { role -> selectedType = role })

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp).testTag("Divider"),
                thickness = 1.dp,
                color = BORDER_COLOR)

            Text(
                text = "Choose your activity",
                fontSize = if (screenWidth < 360.dp) 14.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp).testTag("ActivitySelectionTitle"))

            ActivitySelectionGrid(
                activities =
                    listOf(
                        "Push-ups" to false,
                        "Dips" to false,
                        "Burpee" to false,
                        "Lunge" to false,
                        "Planks" to true,
                        "Handstand" to true,
                        "Front lever" to true,
                        "Flag" to true,
                        "Muscle-up" to false),
                buttonWidth = buttonWidth,
                buttonHeight = buttonHeight,
                gridSpacing = gridSpacing,
                onActivitySelected = { activity -> selectedActivity = activity })
          }

          // Confirm Button
          Button(
              onClick = {
                if (selectedType != null && selectedActivity != null) {
                  val (activity, isTimeDependent) = selectedActivity!!
                  when (selectedType) {
                    "Solo" -> navigationActions.navigateToSoloScreen(activity, isTimeDependent)
                    "Coach" -> navigationActions.navigateToCoachScreen(activity, isTimeDependent)
                    "Challenge" ->
                        navigationActions.navigateToChallengeScreen(activity, isTimeDependent)
                  }
                } else {
                  // Handle case where not all selections are made
                }
              },
              modifier =
                  Modifier.fillMaxWidth(0.5f)
                      .align(Alignment.CenterHorizontally)
                      .testTag("ConfirmButton"),
              shape = RoundedCornerShape(50),
              colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK)) {
                Text(
                    text = "Confirm",
                    color = Color.White,
                    fontSize = if (screenWidth < 360.dp) 12.sp else 14.sp,
                    fontWeight = FontWeight.Bold)
              }
        }
  }
}

@Composable
fun RoleSelectionRow(
    roles: List<String>,
    buttonWidth: Dp,
    buttonHeight: Dp,
    gridSpacing: Dp,
    onRoleSelected: (String) -> Unit
) {
  var selectedRole by remember { mutableStateOf<String?>(null) }

  val roleImages =
      mapOf(
          "Solo" to R.drawable.training_solo,
          "Coach" to R.drawable.training_coach,
          "Challenge" to R.drawable.training_challenge)

  LazyVerticalGrid(
      columns = GridCells.Adaptive(buttonWidth + gridSpacing),
      modifier = Modifier.fillMaxWidth().testTag("RoleSelectionGrid"),
      horizontalArrangement = Arrangement.spacedBy(gridSpacing),
      verticalArrangement = Arrangement.spacedBy(gridSpacing)) {
        items(roles) { role ->
          SelectionButton(
              text = role,
              isSelected = selectedRole == role,
              imageResId = roleImages[role] ?: R.drawable.pushup,
              buttonWidth = buttonWidth,
              buttonHeight = buttonHeight,
              onClick = {
                selectedRole = role
                onRoleSelected(role)
              },
              modifier = Modifier.testTag("Role_$role"))
        }
      }
}

@Composable
fun ActivitySelectionGrid(
    activities: List<Pair<String, Boolean>>,
    buttonWidth: Dp,
    buttonHeight: Dp,
    gridSpacing: Dp,
    onActivitySelected: (Pair<String, Boolean>) -> Unit
) {
  var selectedActivity by remember { mutableStateOf<Pair<String, Boolean>?>(null) }

  LazyVerticalGrid(
      columns = GridCells.Adaptive(buttonWidth + gridSpacing),
      modifier = Modifier.fillMaxWidth().testTag("ActivitySelectionGrid"),
      horizontalArrangement = Arrangement.spacedBy(gridSpacing),
      verticalArrangement = Arrangement.spacedBy(gridSpacing)) {
        items(activities) { (activity, isTimeDependent) ->
          ActivityButton(
              text = activity,
              isSelected = selectedActivity?.first == activity,
              imageResId = R.drawable.pushup,
              buttonWidth = buttonWidth,
              buttonHeight = buttonHeight,
              onClick = {
                selectedActivity = activity to isTimeDependent
                onActivitySelected(activity to isTimeDependent)
              },
              modifier = Modifier.testTag("Activity_$activity"))
        }
      }
}

@Composable
fun ActivityButton(
    text: String,
    isSelected: Boolean,
    imageResId: Int,
    buttonWidth: Dp,
    buttonHeight: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.width(buttonWidth)) {
        Button(
            onClick = onClick,
            modifier =
                Modifier.size(width = buttonWidth, height = buttonHeight)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) INTERACTION_COLOR_DARK else Color.Gray,
                        shape = RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
              Image(
                  painter = painterResource(id = imageResId),
                  contentDescription = text,
                  modifier = Modifier.size(buttonWidth * 0.4f))
            }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Medium)
      }
}

@Composable
fun SelectionButton(
    text: String,
    isSelected: Boolean,
    imageResId: Int,
    buttonWidth: Dp,
    buttonHeight: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.width(buttonWidth)) {
        Button(
            onClick = onClick,
            modifier =
                Modifier.size(width = buttonWidth, height = buttonHeight)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) INTERACTION_COLOR_DARK else Color.Gray,
                        shape = RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
              Image(
                  painter = painterResource(id = imageResId),
                  contentDescription = text,
                  modifier = Modifier.size(buttonWidth * 0.4f))
            }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
      }
}
