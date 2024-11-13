package com.android.streetworkapp.model.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class UserViewModel(private val repository: UserRepository) : ViewModel() {

  // MutableStateFlow to hold the current values
  private val _currentUser = MutableStateFlow<User?>(null)
  val currentUser: StateFlow<User?>
    get() = _currentUser

  private val _user = MutableStateFlow<User?>(null)
  val user: StateFlow<User?>
    get() = _user

  private val _friends = MutableStateFlow<List<User?>>(emptyList())
  val friends: StateFlow<List<User?>>
    get() = _friends

  /**
   * Sets the current user to the provided User object.
   *
   * @param user The User object to set as the current user.
   */
  fun setCurrentUser(user: User?) {
    _currentUser.value = user
  }

  /**
   * Loads the current user from Firestore based on the provided user.
   *
   * @param user The user to load.
   */
  fun loadCurrentUser(user: User?) {
    if (user != null) {
      require(user.uid.isNotEmpty()) { "UID must not be empty" }
    }
    viewModelScope.launch { _currentUser.value = user }
  }

  // Companion object to provide a factory for UserViewModel
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // Create the UserRepository with the required dependencies
            val repository = UserRepositoryFirestore(FirebaseFirestore.getInstance())
            return UserViewModel(repository) as T
          }
        }
  }

  /**
   * Generates a new unique ID for a user.
   *
   * @return A new unique ID (document ID) generated by Firestore.
   */
  fun getNewUid(): String {
    return repository.getNewUid()
  }

  /**
   * Retrieves a user from Firestore based on the provided ID.
   *
   * @param uid The unique ID of the user to retrieve.
   * @return The User object if found, or null if the user doesn't exist or an error occurs.
   */
  fun getUserByUid(uid: String) {
    Log.d("DEBUGSWENT", "Getting user by uid: $uid")
    viewModelScope.launch {
      val user = repository.getUserByUid(uid)
      Log.d("DEBUGSWENT", "Fetched user: $user")
      if (user != null) {
        _user.value = user
      }
    }
  }

  /**
   * Retrieves a user from Firestore based on the provided email.
   *
   * @param email The email of the user to retrieve.
   * @return The User object if found, or null if the user doesn't exist or an error occurs.
   */
  fun getUserByEmail(email: String) {
    viewModelScope.launch {
      val fetchedUser = repository.getUserByEmail(email)
      _user.value = fetchedUser
    }
  }

  /**
   * Retrieves a user from Firestore based on the provided username.
   *
   * @param userName The username of the user to retrieve.
   * @return The User object if found, or null if the user doesn't exist or an error occurs.
   */
  fun getUserByUserName(userName: String) {
    viewModelScope.launch {
      val fetchedUser = repository.getUserByUserName(userName)
      _user.value = fetchedUser
    }
  }

  /**
   * Retrieves a list of friends for a user based on the provided ID.
   *
   * @param uid The unique ID of the user to retrieve friends for.
   * @return A list of User objects representing the user's friends.
   */
  fun getFriendsByUid(uid: String) {
    Log.d("DEBUGSWENT", "Getting friends by uid: $uid")
    viewModelScope.launch {
      val fetchedFriends = repository.getFriendsByUid(uid)
      Log.d("DEBUGSWENT", "Fetched friends: $fetchedFriends")
      if (fetchedFriends != null) {
        _friends.value = fetchedFriends
      }
    }
  }

  /**
   * Adds a new user to Firestore.
   *
   * @param user The User object to add to Firestore.
   */
  fun addUser(user: User) = viewModelScope.launch { repository.addUser(user) }

  /**
   * Retrieves a user from Firestore based on the provided ID, or adds it if they don't exist.
   *
   * @param uid The unique ID of the user to retrieve or add.
   * @param user The User object to add if the user doesn't exist.
   */
  fun getOrAddUserByUid(uid: String, user: User) {
    viewModelScope.launch {
      val fetchedUser = repository.getOrAddUserByUid(uid, user)
      _user.value = fetchedUser
    }
  }

  /**
   * Updates the user's score in Firestore.
   *
   * @param uid The unique ID of the user whose score is being updated.
   * @param newScore The new score to set for the user.
   */
  fun updateUserScore(uid: String, newScore: Int) =
      viewModelScope.launch { repository.updateUserScore(uid, newScore) }

  /**
   * Increases the user's score in Firestore by the specified number of points.
   *
   * @param uid The unique ID of the user whose score is being increased.
   * @param points The number of points to add to the user's score.
   */
  fun increaseUserScore(uid: String, points: Int) =
      viewModelScope.launch { repository.increaseUserScore(uid, points) }

  /**
   * Adds a friend to both the user's and friend's friend lists in Firestore.
   *
   * @param uid The unique ID of the user.
   * @param friendUid The ID of the friend to add to the user's friend list.
   */
  fun addFriend(uid: String, friendUid: String) =
      viewModelScope.launch { repository.addFriend(uid, friendUid) }

  /**
   * Removes a friend from the user's friend list in Firestore.
   *
   * @param uid The unique ID of the user.
   * @param friendUid The ID of the friend to remove from the user's friend list.
   */
  fun removeFriend(uid: String, friendUid: String) =
      viewModelScope.launch { repository.removeFriend(uid, friendUid) }

  /**
   * Deletes a user from Firestore based on the provided ID.
   *
   * @param uid The unique ID of the user to delete.
   */
  fun deleteUserByUid(uid: String) = viewModelScope.launch { repository.deleteUserByUid(uid) }
}
