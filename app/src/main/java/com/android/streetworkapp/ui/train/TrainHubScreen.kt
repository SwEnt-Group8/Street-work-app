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

    val buttonSize =
        ButtonSize(
            width = if (screenWidth < 360.dp) 80.dp else 100.dp,
            height = if (screenHeight < 600.dp) 70.dp else 100.dp,
            spacing = if (screenWidth < 360.dp) 4.dp else 8.dp)

    Column(
        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween) {
          Column(modifier = Modifier.fillMaxWidth()) {
            // Role selection
            SelectionTitle(title = "Choose your training type", testTag = "RoleSelectionTitle")
            SelectionGrid(
                items = listOf("Solo", "Coach", "Challenge"),
                buttonSize = buttonSize,
                onItemSelected = { selectedType = it },
                getItemImageRes = { role ->
                  when (role) {
                    "Solo" -> R.drawable.training_solo
                    "Coach" -> R.drawable.training_coach
                    "Challenge" -> R.drawable.training_challenge
                    else -> R.drawable.pushup
                  }
                },
                isSelected = { it == selectedType },
                testTagPrefix = "Role_")

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp).testTag("Divider"),
                thickness = 1.dp,
                color = BORDER_COLOR)

            // Activity selection
            SelectionTitle(title = "Choose your activity", testTag = "ActivitySelectionTitle")
            SelectionGrid(
                items =
                    listOf(
                            "Push-ups" to false,
                            "Dips" to false,
                            "Burpee" to false,
                            "Lunge" to false,
                            "Planks" to true,
                            "Handstand" to true,
                            "Front lever" to true,
                            "Flag" to true,
                            "Muscle-up" to false)
                        .map { it.first },
                buttonSize = buttonSize,
                onItemSelected = { activity ->
                  selectedActivity =
                      listOf(
                              "Push-ups" to false,
                              "Dips" to false,
                              "Burpee" to false,
                              "Lunge" to false,
                              "Planks" to true,
                              "Handstand" to true,
                              "Front lever" to true,
                              "Flag" to true,
                              "Muscle-up" to false)
                          .first { it.first == activity }
                },
                getItemImageRes = { R.drawable.pushup },
                isSelected = { selectedActivity?.first == it },
                testTagPrefix = "Activity_")
          }

          ConfirmButtonSection(
              selectedType = selectedType,
              selectedActivity = selectedActivity,
              navigationActions = navigationActions)
        }
  }
}

@Composable
fun SelectionTitle(title: String, testTag: String) {
  Text(
      text = title,
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(bottom = 16.dp).testTag(testTag))
}

@Composable
fun SelectionGrid(
    items: List<String>,
    buttonSize: ButtonSize,
    onItemSelected: (String) -> Unit,
    getItemImageRes: (String) -> Int,
    isSelected: (String) -> Boolean,
    testTagPrefix: String
) {
  LazyVerticalGrid(
      columns = GridCells.Adaptive(buttonSize.width + buttonSize.spacing),
      modifier = Modifier.fillMaxWidth().testTag("${testTagPrefix}Grid"),
      horizontalArrangement = Arrangement.spacedBy(buttonSize.spacing),
      verticalArrangement = Arrangement.spacedBy(buttonSize.spacing)) {
        items(items) { item ->
          SelectionButton(
              text = item,
              imageResId = getItemImageRes(item),
              buttonSize = buttonSize,
              onClick = { onItemSelected(item) },
              isSelected = isSelected(item),
              testTag = "$testTagPrefix$item")
        }
      }
}

@Composable
fun SelectionButton(
    text: String,
    imageResId: Int,
    buttonSize: ButtonSize,
    onClick: () -> Unit,
    isSelected: Boolean,
    testTag: String
) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.width(buttonSize.width)) {
        Button(
            onClick = onClick,
            modifier =
                Modifier.size(buttonSize.width, buttonSize.height)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) INTERACTION_COLOR_DARK else Color.Gray,
                        shape = RoundedCornerShape(20.dp))
                    .testTag(testTag),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
              Image(
                  painter = painterResource(id = imageResId),
                  contentDescription = text,
                  modifier = Modifier.size(buttonSize.width * 0.4f))
            }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
      }
}

@Composable
fun ConfirmButtonSection(
    selectedType: String?,
    selectedActivity: Pair<String, Boolean>?,
    navigationActions: NavigationActions
) {
  Box(modifier = Modifier.fillMaxWidth()) {
    ConfirmButton(
        modifier = Modifier.align(Alignment.Center),
        onClick = {
          selectedType?.let { type ->
            selectedActivity?.let { (activity, isTimeDependent) ->
              when (type) {
                "Solo" -> navigationActions.navigateToSoloScreen(activity, isTimeDependent)
                "Coach" -> navigationActions.navigateToCoachScreen(activity, isTimeDependent)
                "Challenge" ->
                    navigationActions.navigateToChallengeScreen(activity, isTimeDependent)
              }
            }
          }
        })
  }
}

@Composable
fun ConfirmButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
  Button(
      onClick = onClick,
      modifier = modifier.fillMaxWidth(0.5f).testTag("ConfirmButton"),
      shape = RoundedCornerShape(50),
      colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK)) {
        Text(text = "Confirm", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
      }
}

data class ButtonSize(val width: Dp, val height: Dp, val spacing: Dp)
