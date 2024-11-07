package com.android.streetworkapp.model.progression

data class Progression(
    val uid: String,
    val currentGoal: Ranks,
    val eventsCreated: Int,
    val eventsJoined: Int,
    val achievements: List<Achievements>
)

enum class Ranks(val score: Int) {
  BRONZE(100),
  SILVER(1000),
  GOLD(10000)
}

data class Achievements(
    val icon: Int,
    val title: String,
    val tag: List<String>,
    val description: String
)
