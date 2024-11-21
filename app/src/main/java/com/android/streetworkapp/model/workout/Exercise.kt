package com.android.streetworkapp.model.workout

/**
 * A data class representing an exercise performed in a workout session.
 *
 * @param name Name of the exercise (e.g., "Push-up")
 * @param reps Optional number of repetitions
 * @param sets Optional number of sets
 * @param weight Optional weight for the exercise
 * @param duration Optional duration for the exercise
 */
data class Exercise(
    val name: String,
    val reps: Int? = null,
    val sets: Int? = null,
    val weight: Float? = null,
    val duration: Int? = null
)
