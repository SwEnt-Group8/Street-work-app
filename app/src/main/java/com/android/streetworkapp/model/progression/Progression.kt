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
    val description: String
)

enum class Ranks(val score: Int) {
  BRONZE(100),
  SILVER(1000),
  GOLD(10000),
  PLATINUM(100000)
}

enum class MedalsAchievement(val achievement: Achievement) {
  NONE(Achievement(R.drawable.place_holder, "No medal", emptyList(), "No medal")),
  BRONZE(Achievement(R.drawable.place_holder, "Bronze medal", emptyList(), "First medal")),
  SILVER(Achievement(R.drawable.place_holder, "Silver medal", emptyList(), "Second medal")),
  GOLD(Achievement(R.drawable.place_holder, "Gold medal", emptyList(), "Third medal")),
  PLATINUM(Achievement(R.drawable.place_holder, "Platinum medal", emptyList(), "Last medal"))
}

enum class SCORE_INCREASE(val scoreAdded: Int) {
  CREATE_EVENT(10),
  JOIN_EVENT(5),
  ADD_FRIEND(10)
}
 