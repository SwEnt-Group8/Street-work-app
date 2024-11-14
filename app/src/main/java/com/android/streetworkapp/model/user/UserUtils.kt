package com.android.streetworkapp.model.user

import com.google.firebase.auth.FirebaseUser

/** A utility class for the user model. */
class UserUtils {

  /**
   * Creates a new [User] object from the provided [FirebaseUser].
   *
   * @param firebaseUser The FirebaseUser object to create the User object from.
   * @return The User object created from the FirebaseUser.
   */
  fun createNewUserFromFirebaseUser(firebaseUser: FirebaseUser): User {
    require(firebaseUser.uid.isNotEmpty()) { "UID must not be empty" }
    return User(
        uid = firebaseUser.uid,
        username = firebaseUser.displayName ?: "",
        email = firebaseUser.email ?: "",
        score = 0,
        friends = emptyList())
  }
}
