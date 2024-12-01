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
   * @param loginState True if the user is logged in, false otherwise
   */
  suspend fun setLoginState(loginState: Boolean)

  /**
   * Returns the user's uid.
   *
   * @return The user's uid
   */
  suspend fun getUid(): String

  /**
   * Sets the user's uid.
   *
   * @param uid The user's uid
   */
  suspend fun setUid(uid: String)

  /**
   * Returns the user's name.
   *
   * @return The user's name
   */
  suspend fun getName(): String

  /**
   * Sets the user's name.
   *
   * @param name The user's name
   */
  suspend fun setName(name: String)

  /**
   * Returns the user's score.
   *
   * @return The user's score
   */
  suspend fun getScore(): Int

  /**
   * Sets the user's score.
   *
   * @param score The user's score
   */
  suspend fun setScore(score: Int)
}
