package com.android.streetworkapp.model.user

data class User(
    val uid: String,
    val name: String,
    val email: String,
    val score: Int,
    val friends: List<String>
)
