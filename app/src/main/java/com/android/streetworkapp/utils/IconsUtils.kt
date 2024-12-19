package com.android.streetworkapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.android.sample.R

/**
 * This function can be used to find the Icon corresponding to a given exercise name.
 *
 * @param name: The exercise name
 */
@Composable
fun exerciseNameToIcon(name: String): Int {
  return when (name) {
    LocalContext.current.getString(R.string.Pushups) -> R.drawable.train_pushup
    LocalContext.current.getString(R.string.Dips) -> R.drawable.train_dips
    LocalContext.current.getString(R.string.Burpee) -> R.drawable.train_burpee
    LocalContext.current.getString(R.string.Lunge) -> R.drawable.train_lunge
    LocalContext.current.getString(R.string.Planks) -> R.drawable.train_planks
    LocalContext.current.getString(R.string.Handstand) -> R.drawable.train_hand_stand
    LocalContext.current.getString(R.string.Frontlever) -> R.drawable.train_front_lever
    LocalContext.current.getString(R.string.Flag) -> R.drawable.train_flag
    LocalContext.current.getString(R.string.Muscleup) -> R.drawable.train_muscle_up
    else -> R.drawable.handstand_org
  }
}
