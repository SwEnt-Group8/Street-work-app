package com.android.streetworkapp.ui.train

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.streetworkapp.model.workout.WorkoutViewModel

@Composable
fun TrainCoachScreen(
    activity: String,
    isTimeDependent: Boolean,
    time: Int?,
    sets: Int?,
    reps: Int?,
    workoutViewModel: WorkoutViewModel,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
  Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        Text("Train Coach")
        Text("Activity: $activity")
        Text("Time Dependent: $isTimeDependent")
      }
}
