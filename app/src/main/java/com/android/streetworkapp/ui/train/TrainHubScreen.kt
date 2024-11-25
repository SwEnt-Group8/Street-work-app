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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.streetworkapp.model.workout.WorkoutViewModel
import com.android.streetworkapp.ui.navigation.NavigationActions
import com.android.streetworkapp.ui.theme.ColorPalette.BORDER_COLOR
import com.android.streetworkapp.ui.theme.ColorPalette.INTERACTION_COLOR_DARK

@Composable
fun TrainHubScreen(
    navigationActions: NavigationActions,
    workoutViewModel: WorkoutViewModel,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    BoxWithConstraints {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        val buttonHeight = if (screenHeight < 600.dp) 70.dp else 100.dp
        val buttonWidth = if (screenWidth < 360.dp) 80.dp else 100.dp
        val gridSpacing = if (screenWidth < 360.dp) 4.dp else 8.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp), // Adjust padding for proper layout
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Section: Choose Your Role
                Text(
                    text = "Choose your training type",
                    fontSize = if (screenWidth < 360.dp) 14.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                RoleSelectionRow(
                    roles = listOf("Solo", "Coach", "Challenge"),
                    buttonWidth = buttonWidth,
                    buttonHeight = buttonHeight,
                    gridSpacing = gridSpacing,
                    onRoleSelected = { /* TODO: Handle Role Selection */ }
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 1.dp,
                    color = BORDER_COLOR
                )

                Text(
                    text = "Choose your activity",
                    fontSize = if (screenWidth < 360.dp) 14.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ActivitySelectionGrid(
                    activities = listOf("Push-ups", "Dips", "Burpee", "Lunge", "Planks", "Handstand", "Front lever", "Flag", "Muscle-up"),
                    buttonWidth = buttonWidth,
                    buttonHeight = buttonHeight,
                    gridSpacing = gridSpacing,
                    onActivitySelected = { /* TODO: Handle Activity Selection */ }
                )
            }

            // Confirm Button
            Button(
                onClick = {
                    // TODO: Handle confirmation action
                },
                modifier = Modifier
                    .fillMaxWidth(0.5f) // Ensure the button size is visible
                    .align(Alignment.CenterHorizontally), // Center the button
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = INTERACTION_COLOR_DARK)
            ) {
                Text(
                    text = "Confirm",
                    color = Color.White,
                    fontSize = if (screenWidth < 360.dp) 12.sp else 14.sp,
                    fontWeight = FontWeight.Bold
                )
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

    val roleImages = mapOf(
        "Solo" to R.drawable.training_solo,
        "Coach" to R.drawable.training_coach,
        "Challenge" to R.drawable.training_challenge
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(buttonWidth + gridSpacing),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(gridSpacing),
        verticalArrangement = Arrangement.spacedBy(gridSpacing)
    ) {
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
                }
            )
        }
    }
}

@Composable
fun ActivitySelectionGrid(
    activities: List<String>,
    buttonWidth: Dp,
    buttonHeight: Dp,
    gridSpacing: Dp,
    onActivitySelected: (String) -> Unit
) {
    var selectedActivity by remember { mutableStateOf<String?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(buttonWidth + gridSpacing),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(gridSpacing),
        verticalArrangement = Arrangement.spacedBy(gridSpacing)
    ) {
        items(activities) { activity ->
            ActivityButton(
                text = activity,
                isSelected = selectedActivity == activity,
                imageResId = R.drawable.pushup,
                buttonWidth = buttonWidth,
                buttonHeight = buttonHeight,
                onClick = {
                    selectedActivity = activity
                    onActivitySelected(activity)
                }
            )
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
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(buttonWidth)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .size(width = buttonWidth, height = buttonHeight)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) INTERACTION_COLOR_DARK else Color.Gray,
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = text,
                modifier = Modifier.size(buttonWidth * 0.4f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SelectionButton(
    text: String,
    isSelected: Boolean,
    imageResId: Int,
    buttonWidth: Dp,
    buttonHeight: Dp,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(buttonWidth)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .size(width = buttonWidth, height = buttonHeight)
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) INTERACTION_COLOR_DARK else Color.Gray,
                    shape = RoundedCornerShape(20.dp)
                ),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = text,
                modifier = Modifier.size(buttonWidth * 0.4f)
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