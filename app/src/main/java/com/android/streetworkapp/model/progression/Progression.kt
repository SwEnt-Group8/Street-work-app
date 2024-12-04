package com.android.streetworkapp.model.progression

import com.android.sample.R

/**
 * Represents the progression of a User.
 *
 * @param progressionId The progression ID.
 * @param uid The id of the user linked to this progression.
 * @param currentGoal The next score that the user should obtain.
 * @param eventsCreated The number of events created.
 * @param eventsJoined The number of events joined.
 * @param achievements The list of achievements obtained.
 */
data class Progression(
    val progressionId: String = "None",
    val uid: String = "None",
    var currentGoal: Int = Ranks.BRONZE.score,
    var eventsCreated: Int = 0,
    var eventsJoined: Int = 0,
    var achievements: List<String> = emptyList()
)

/**
 * Represents an Achievement.
 *
 * @param icon The icon.
 * @param title The title of this progression.
 * @param tags The tags linked to this achievement.
 * @param description A description of the achievement.
 */
data class Achievement(
    val icon: Int,
    val title: String,
    val tags: List<String>,
    var description: String
)

/** Represents the different ranks needed to obtain the next medal */
enum class Ranks(val score: Int) {
  BRONZE(100),
  SILVER(1000),
  GOLD(10000),
  PLATINUM(100000),
  CEILING(100000)
}

/** Represents a type of achievement based on points obtained */
enum class MedalsAchievement(val achievement: Achievement) {
  // Note: Bronze is used as a place holder for missing medals in the Figma (will be updated once
  // the figma is updated)
  NONE(
      Achievement(
          R.drawable.bronze_achievement_icon,
          "No Medal",
          emptyList(),
          "You have no medal yet, try to obtain more points!")),
  BRONZE(
      Achievement(
          R.drawable.bronze_achievement_icon,
          "Bronze Medal",
          listOf("Bronze"),
          "You obtained 100 Points!")),
  SILVER(
      Achievement(
          R.drawable.silver_achievement_icon,
          "Silver Medal",
          listOf("Silver"),
          "You obtained 1000 Points!")),
  GOLD(
      Achievement(
          R.drawable.gold_achievement_icon,
          "Gold Medal",
          listOf("Gold"),
          "You obtained 10000 Points!")),
  PLATINUM(
      Achievement(
          R.drawable.bronze_achievement_icon,
          "Platinum Medal",
          listOf("Platinum"),
          "You obtained 100000 Points!"))
}

/** Represents a type of achievement linked to the performance of the user on different exercises */
enum class ExerciseAchievement(val achievement: Achievement) {
  HANDSTAND(Achievement(R.drawable.train_hand_stand, "Handstand", emptyList(), "")),
  FRONT_LEVER(Achievement(R.drawable.train_front_lever, "Front Lever", emptyList(), ""))
}

/** Represents the number of points obtained for different actions in the app */
enum class ScoreIncrease(val points: Int) {
  ADD_EVENT(30),
  JOIN_EVENT(60),
  ADD_FRIEND(90)
}
