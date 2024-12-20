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
enum class MedalsAchievement(val achievement: Achievement, val rank: Ranks) {

  BRONZE(
      Achievement(
          R.drawable.bronze_icon_final,
          "Bronze Medal",
          listOf("Bronze"),
          "You obtained 100 Points!"),
      Ranks.BRONZE),
  SILVER(
      Achievement(
          R.drawable.silver_icon_final,
          "Silver Medal",
          listOf("Silver"),
          "You obtained 1000 Points!"),
      Ranks.SILVER),
  GOLD(
      Achievement(
          R.drawable.gold_icon_final, "Gold Medal", listOf("Gold"), "You obtained 10000 Points!"),
      Ranks.GOLD),
  PLATINUM(
      Achievement(
          R.drawable.platinum_icon_final,
          "Platinum Medal",
          listOf("Platinum"),
          "You obtained 100000 Points!"),
      Ranks.PLATINUM)
}

/** Represents a type of achievement linked to the social interactions of the user */
enum class SocialAchievement(val achievement: Achievement, val numberOfFriends: Int) {
  SOCIAL1(
      Achievement(
          R.drawable.first_friend_achievement,
          "First Friend",
          listOf("Social"),
          "You added your first friend!"),
      1),
  SOCIAL3(
      Achievement(
          R.drawable.group_friend_achievement,
          "Workout Group",
          listOf("Social"),
          "You added 3 friends!"),
      3)
}

/** Represents the number of points obtained for different actions in the app */
enum class ScoreIncrease(val points: Int) {
  ADD_EVENT(30),
  JOIN_EVENT(60),
  ADD_FRIEND(90),
  FIND_NEW_PARK(100),
  ADD_RATING(20)
}
