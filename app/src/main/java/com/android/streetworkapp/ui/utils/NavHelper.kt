package com.android.streetworkapp.ui.utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

typealias TrainComposableParams =
    @Composable
    (activity: String, isTimeDependent: Boolean, time: Int?, sets: Int?, reps: Int?) -> Unit

/**
 * Composable to handle the navigation to the train screens
 *
 * @param route the route of the screen
 * @param content the lambda with the parameters of the screen
 */
fun NavGraphBuilder.trainComposable(route: String, content: TrainComposableParams) {
  composable(
      route = route,
      arguments =
          listOf(
              navArgument("activity") { type = NavType.StringType },
              navArgument("isTimeDependent") { type = NavType.BoolType },
              navArgument("time") {
                type = NavType.IntType
                defaultValue = 0
              },
              navArgument("sets") {
                type = NavType.IntType
                defaultValue = 0
              },
              navArgument("reps") {
                type = NavType.IntType
                defaultValue = 0
              })) { backStackEntry ->
        val activity = backStackEntry.arguments?.getString("activity") ?: "Unknown"
        val isTimeDependent = backStackEntry.arguments?.getBoolean("isTimeDependent") ?: false
        val time = backStackEntry.arguments?.getInt("time")
        val sets = backStackEntry.arguments?.getInt("sets")
        val reps = backStackEntry.arguments?.getInt("reps")

        content(activity, isTimeDependent, time, sets, reps)
      }
}
