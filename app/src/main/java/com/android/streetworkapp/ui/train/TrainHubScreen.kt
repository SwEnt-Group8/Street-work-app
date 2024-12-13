package com.android.streetworkapp.ui.train

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_LIGHT

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

    // Button size
    val buttonSize = remember {
        ButtonSize(width = 100.dp, height = 100.dp, spacing = 8.dp)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Training Type Section
        item {
            SelectionTitle(title = "Choose your training type", testTag = "RoleSelectionTitle")
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                SelectionButton(
                    text = "Solo",
                    imageResId = R.drawable.training_solo,
                    buttonSize = buttonSize,
                    onClick = { selectedType = "Solo" },
                    isSelected = selectedType == "Solo",
                    testTag = "Role_Solo"
                )
                Spacer(modifier = Modifier.width(buttonSize.spacing))
                SelectionButton(
                    text = "Coach",
                    imageResId = R.drawable.training_coach,
                    buttonSize = buttonSize,
                    onClick = { selectedType = "Coach" },
                    isSelected = selectedType == "Coach",
                    testTag = "Role_Coach"
                )
            }
        }

        // Divider
        item {
            Spacer(modifier = Modifier.height(5.dp))
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                thickness = 1.dp,
                color = BORDER_COLOR
            )
        }

        // Activity Selection Section Title
        item {
            SelectionTitle(title = "Choose your activity", testTag = "ActivitySelectionTitle")
        }

        // Activity Grid
        items(
            listOf(
                "Push-ups", "Dips", "Burpee", "Lunge", "Planks",
                "Handstand", "Front lever", "Flag", "Muscle-up"
            ).chunked(3) // Chunk activities into pairs for layout
        ) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSize.spacing, Alignment.CenterHorizontally)
            ) {
                rowItems.forEach { activity ->
                    SelectionButton(
                        text = activity,
                        imageResId = getActivityIcon(activity),
                        buttonSize = buttonSize,
                        onClick = {
                            selectedActivity = when (activity) {
                                "Push-ups" -> activity to false
                                "Dips" -> activity to false
                                "Burpee" -> activity to false
                                "Lunge" -> activity to false
                                "Planks" -> activity to true
                                "Handstand" -> activity to true
                                "Front lever" -> activity to true
                                "Flag" -> activity to true
                                "Muscle-up" -> activity to true
                                else -> activity to false
                            }
                        },
                        isSelected = selectedActivity?.first == activity,
                        testTag = "Activity_$activity"
                    )
                }
            }
        }

        // Confirm Button Section
        item {
            Spacer(modifier = Modifier.height(8.dp))
            ConfirmButtonSection(
                selectedType = selectedType,
                selectedActivity = selectedActivity,
                navigationActions = navigationActions
            )
        }
    }
}

// Utility to fetch activity icon
@Composable
fun getActivityIcon(activity: String): Int {
    return when (activity) {
        "Push-ups" -> R.drawable.train_pushup
        "Dips" -> R.drawable.train_dips
        "Burpee" -> R.drawable.train_burpee
        "Lunge" -> R.drawable.train_lunge
        "Planks" -> R.drawable.train_planks
        "Handstand" -> R.drawable.train_hand_stand
        "Front lever" -> R.drawable.train_front_lever
        "Flag" -> R.drawable.train_flag
        "Muscle-up" -> R.drawable.train_muscle_up
        else -> R.drawable.handstand_org
    }
}

@Composable
fun SelectionTitle(title: String, testTag: String) {
  Text(
      text = title,
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(bottom = 8.dp).testTag(testTag))
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
        modifier = Modifier
            .width(buttonSize.width)
    ) {
        Button(

            onClick = onClick,
            modifier = Modifier
                .size(buttonSize.width, buttonSize.height)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) INTERACTION_COLOR_DARK else Color.Gray,
                    shape = RoundedCornerShape(20.dp)
                )
                .testTag(testTag),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor =  INTERACTION_COLOR_LIGHT
            )
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = text,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
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
                        navigationActions.navigateToTrainParam(activity, isTimeDependent, type)
                    }
                }
            }
        )
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
