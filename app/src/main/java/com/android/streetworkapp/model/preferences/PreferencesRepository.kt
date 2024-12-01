package com.android.streetworkapp.model.preferences

/** A repository interface for preferences data. */
interface PreferencesRepository {
  /**
   * Returns the login state of the user.
   *
   * @return True if the user is logged in, false otherwise
   */
  suspend fun getLoginState(): Boolean

  /**
   * Sets the login state of the user.
   *
   * @param isLoggedIn True if the user is logged in, false otherwise
   */
  suspend fun setIsLoginState(isLoggedIn: Boolean)

  /**
   * Returns the user's uid.
   *
   * @return The user's uid
   */
  suspend fun getUid(): String

  /**
   * Sets the user's uid.
   *
   * @param savedUid The user's uid
   */
  suspend fun setUid(savedUid: String)

  /**
   * Returns the user's name.
   *
   * @return The user's name
   */
  suspend fun getName(): String

  /**
   * Sets the user's name.
   *
   * @param savedName The user's name
   */
  suspend fun setName(savedName: String)

  /**
   * Returns the user's score.
   *
   * @return The user's score
   */
  suspend fun getScore(): Int

  /**
   * Sets the user's score.
   *
   * @param savedScore The user's score
   */
  suspend fun setScore(savedScore: Int)
}
