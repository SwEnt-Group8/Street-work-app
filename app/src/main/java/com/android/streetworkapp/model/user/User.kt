package com.android.streetworkapp.model.user

data class User(
    val uid: String,
    val username: String,
    val email: String,
    val score: Int,
    val friends: List<String>,
    val picture: String,
    val parks: List<String> = emptyList()
)
